package me.dannytatom.xibalba.world;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.utils.SpriteAccessor;

import java.util.Arrays;

public class Map {
  public final int width;
  public final int height;
  public final String type;
  public final MapCell.Type[][] geometry;
  public final MapTime time;
  public float[][] lightMap;
  private MapCell[][] map;
  private MapCell.Type[][] flooded;
  private int floodedCount = 0;

  /**
   * Holds logic for dealing with maps.
   *
   * @param geometry The world geometry
   */
  public Map(String type, MapCell.Type[][] geometry) {
    this.type = type;
    this.geometry = geometry;

    this.width = this.geometry.length;
    this.height = this.geometry[0].length;

    this.time = new MapTime();
  }

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
          map[x][y] = new MapCell(floor, MapCell.Type.FLOOR, "the forest floor");
        } else {
          Sprite wall = Main.asciiAtlas.createSprite("0" + MathUtils.random(5, 6) + "00");
          wall.setColor(Colors.get("forestTree-" + MathUtils.random(1, 3)));
          map[x][y] = new MapCell(wall, MapCell.Type.WALL, "a tree");
        }

        map[x][y].sprite.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);
      }
    }

    if (MathUtils.randomBoolean()) {
      createWater();
    }
  }

  private void paintCave() {
    map = new MapCell[width][height];

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y] == MapCell.Type.FLOOR) {
          Sprite floor = Main.asciiAtlas.createSprite("0011");
          floor.setColor(Colors.get("caveFloor"));
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

    if (MathUtils.randomBoolean()) {
      createWater();
    }
  }

  private void createWater() {
    flooded = new MapCell.Type[width][height];

    for (MapCell.Type[] row : flooded) {
      Arrays.fill(row, MapCell.Type.WALL);
    }

    int floodStartX;
    int floodStartY;

    do {
      floodStartX = MathUtils.random(0, width - 1);
      floodStartY = MathUtils.random(0, height - 1);
    }
    while (!map[floodStartX][floodStartY].isFloor());

    flood(floodStartX, floodStartY);

    for (int x = 0; x < flooded.length; x++) {
      for (int y = 0; y < flooded[0].length; y++) {
        if (flooded[x][y] == MapCell.Type.FLOOR) {
          Sprite water = Main.asciiAtlas.createSprite("0715");
          water.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);

          MapCell.Type type;
          Color lightColor;
          Color darkColor;

          if (getGroundNeighbours(x, y) < 8) {
            type = MapCell.Type.SHALLOW_WATER;
            lightColor = Colors.get("CYAN");
            darkColor = Colors.get("DARK_CYAN");
          } else {
            type = MapCell.Type.DEEP_WATER;
            lightColor = Colors.get("DARK_CYAN");
            darkColor = Colors.get("DARKER_CYAN");
          }

          water.setColor(lightColor);
          Tween.to(water, SpriteAccessor.COLOR, .5f).target(
              darkColor.r, darkColor.g, darkColor.b
          ).repeatYoyo(Tween.INFINITY, MathUtils.random()).start(Main.tweenManager);

          map[x][y] = new MapCell(water, type, "water");
        }
      }
    }
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
