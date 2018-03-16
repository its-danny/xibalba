package me.dannytatom.xibalba.world;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.utils.SpriteAccessor;

public class Map {
  public final int width;
  public final int height;
  public final int depth;
  public final String type;
  public final MapCell.Type[][] geometry;
  public final MapDijkstra dijkstra;
  public boolean hasWater = false;
  public Vector2 entrance;
  public Vector2 exit;
  public MapLight light;
  public MapWeather weather;
  public ArrayList<MapFire> fires;
  private MapCell[][] map;
  private MapCell.Type[][] flooded;
  private int floodedCount = 0;

  /**
   * Holds logic for dealing with maps.
   *
   * @param geometry The world geometry
   */
  public Map(int depth, String type, MapCell.Type[][] geometry) {
    this.depth = depth;
    this.type = type;
    this.geometry = geometry;

    this.width = this.geometry.length;
    this.height = this.geometry[0].length;

    this.dijkstra = new MapDijkstra(this);
    this.fires = new ArrayList<>();
  }

  /**
   * Turn geometry into a MapCell[][] of real tiles based on map type.
   */
  public void paint() {
    switch (type) {
      case "forest":
        paintForest();
        break;
      case "cave":
        paintCave();
        break;
      default:
        break;
    }
  }

  private void paintForest() {
    map = new MapCell[width][height];

    Array<String> floorTypes = new Array<>();
    floorTypes.add("0915");
    floorTypes.add("1202");

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y] == MapCell.Type.FLOOR) {
          Sprite floor = Main.asciiAtlas.createSprite(floorTypes.random());
          floor.setColor(Colors.get("forestFloor"));
          floor.setFlip(MathUtils.randomBoolean(), false);

          map[x][y] = new MapCell(
              floor, MapCell.Type.FLOOR, "the forest floor"
          );
        } else {
          Sprite wall = Main.asciiAtlas.createSprite("0" + MathUtils.random(5, 6) + "00");
          Color color = Colors.get("forestTree-" + MathUtils.random(1, 3));
          wall.setColor(color);

          map[x][y] = new MapCell(wall, MapCell.Type.WALL, "a tree");
        }

        map[x][y].sprite.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);
      }
    }

    if (MathUtils.random() > .5f) {
      createWater();
      createBridge();
    }
  }

  private void paintCave() {
    map = new MapCell[width][height];

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y] == MapCell.Type.FLOOR) {
          Sprite floor = Main.asciiAtlas.createSprite("0915");
          Color color = Colors.get("caveFloor-" + +MathUtils.random(1, 3));
          floor.setColor(color);
          map[x][y] = new MapCell(floor, MapCell.Type.FLOOR, "a cave floor");
        } else {
          int neighbours = getGroundNeighbours(x, y);

          if (neighbours > 0) {
            Sprite wall = Main.asciiAtlas.createSprite("1113");
            wall.setColor(Colors.get("caveWall"));
            map[x][y] = new MapCell(wall, MapCell.Type.WALL, "a cave wall");
          } else {
            Sprite nothing = Main.asciiAtlas.createSprite("0000");
            map[x][y] = new MapCell(nothing, MapCell.Type.NOTHING, "nothing");
          }
        }

        map[x][y].sprite.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);
      }
    }

    if (MathUtils.random() > .75f) {
      createWater();
      createBridge();
    }
  }

  private void createWater() {
    hasWater = true;
    flooded = new MapCell.Type[width][height];

    for (MapCell.Type[] row : flooded) {
      Arrays.fill(row, MapCell.Type.WALL);
    }

    int floodStartX;
    int floodStartY;

    do {
      floodStartX = MathUtils.random(0, width - 1);
      floodStartY = MathUtils.random(0, height - 1);
    } while (!map[floodStartX][floodStartY].isFloor());

    flood(floodStartX, floodStartY);

    for (int x = 0; x < flooded.length; x++) {
      for (int y = 0; y < flooded[0].length; y++) {
        if (flooded[x][y] == MapCell.Type.FLOOR) {
          Sprite water = Main.asciiAtlas.createSprite("0715");
          water.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);

          MapCell.Type waterType;
          Color lightColor;
          Color darkColor;

          if (getGroundNeighbours(x, y) < 8) {
            waterType = MapCell.Type.SHALLOW_WATER;

            lightColor = Colors.get(
                Objects.equals(type, "forest") ? "waterShallowLightBlue" : "waterShallowLightGreen"
            );

            darkColor = Colors.get(
                Objects.equals(type, "forest") ? "waterShallowDarkBlue" : "waterShallowDarkGreen"
            );
          } else {
            waterType = MapCell.Type.DEEP_WATER;

            lightColor = Colors.get(
                Objects.equals(type, "forest") ? "waterDeepLightBlue" : "waterDeepLightGreen"
            );

            darkColor = Colors.get(
                Objects.equals(type, "forest") ? "waterDeepDarkBlue" : "waterDeepDarkGreen"
            );
          }

          water.setColor(lightColor);
          Tween tween = Tween.to(water, SpriteAccessor.COLOR, .5f).target(
              darkColor.r, darkColor.g, darkColor.b
          ).repeatYoyo(Tween.INFINITY, MathUtils.random());

          map[x][y] = new MapCell(water, waterType, "water", tween);
        }
      }
    }
  }

  // Find the largest section of water with land on both sides
  // Connect it with a bridge
  private void createBridge() {
    Sprite bridge = Main.asciiAtlas.createSprite("0302");
    Vector2 start = null;
    int length = 0;

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        MapCell cell = map[x][y];

        // Found water tile, go up until no more water.
        // Store start position and the length.
        if (cell.isWater()) {
          int count = 0;

          while (map[x][y + count].isWater()) {
            count++;
          }

          if (count > length) {
            start = new Vector2(x, y);
            length = count;
          }
        }
      }
    }

    if (start != null) {
      for (int y = 0; y < length; y++) {
        makeCellBridge(map[(int) start.x][(int) start.y + y], bridge);
      }
    }
  }

  private void makeCellBridge(MapCell cell, Sprite bridge) {
    cell.sprite.setRegion(
        bridge.getRegionX(), bridge.getRegionY(),
        bridge.getRegionWidth(), bridge.getRegionHeight()
    );

    cell.sprite.setColor(Colors.get("bridge"));
    cell.type = MapCell.Type.FLOOR;
    cell.description = "a bridge";

    cell.tween.kill();
  }

  public MapCell[][] getCellMap() {
    return map;
  }

  private int getGroundNeighbours(int cellX, int cellY) {
    int count = 0;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int nx = cellX + i;
        int ny = cellY + j;

        if (i != 0 || j != 0) {
          if (nx >= 0 && ny >= 0 && nx < geometry.length && ny < geometry[0].length) {
            if (geometry[nx][ny] == MapCell.Type.FLOOR) {
              count += 1;
            }
          }
        }
      }
    }

    return count;
  }

  private void flood(int cellX, int cellY) {
    if (geometry[cellX][cellY] == MapCell.Type.FLOOR
        && flooded[cellX][cellY] == MapCell.Type.WALL) {
      flooded[cellX][cellY] = MapCell.Type.FLOOR;
      floodedCount += 1;
    } else {
      return;
    }

    if (floodedCount >= MathUtils.random(100, 300)) {
      return;
    }

    flood(cellX + 1, cellY);
    flood(cellX - 1, cellY);
    flood(cellX, cellY + 1);
    flood(cellX, cellY - 1);
  }
}
