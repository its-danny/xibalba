package me.dannytatom.xibalba.world;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.Main;

public class Map {
  public final int width;
  public final int height;
  public final boolean[][] geometry;
  public float[][] lightMap;
  private MapCell[][] map;

  /**
   * Holds logic for dealing with maps.
   *
   * @param geometry The world geometry
   */
  public Map(boolean[][] geometry) {
    this.geometry = geometry;

    this.width = this.geometry.length;
    this.height = this.geometry[0].length;
  }

  /**
   * Start creating Cells and giving em sprites.
   */
  public void paintCave() {
    map = new MapCell[width][height];

    paint();
  }

  public MapCell[][] getCellMap() {
    return map;
  }

  // Determine floors & walls
  private void paint() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y]) {
          Sprite floor = Main.terminalAtlas.createSprite("floor-" + MathUtils.random(1, 3));
          floor.setColor(Colors.get("caveFloor"));
          map[x][y] = new MapCell(floor, false, false, "a cave floor");
        } else {
          int neighbours = getGroundNeighbours(x, y);

          if (neighbours > 0) {
            Sprite wall = Main.terminalAtlas.createSprite("wall");
            wall.setColor(Colors.get("caveWall"));
            map[x][y] = new MapCell(wall, true, false, "a cave wall");
          } else {
            Sprite blank = Main.terminalAtlas.createSprite("blank");
            map[x][y] = new MapCell(blank, false, true, "nothing");
          }
        }

        map[x][y].sprite.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);
      }
    }
  }

  private int getGroundNeighbours(int cellX, int cellY) {
    int count = 0;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int nx = cellX + i;
        int ny = cellY + j;

        if (i != 0 || j != 0) {
          if (nx >= 0 && ny >= 0 && nx < geometry.length && ny < geometry[0].length) {
            if (geometry[nx][ny]) {
              count += 1;
            }
          }
        }
      }
    }

    return count;
  }

  private boolean cellExists(int cellX, int cellY) {
    return cellX > 0 && cellX < map.length
        && cellY > 0 && cellY < map[0].length
        && getCell(cellX, cellY) != null;
  }

  private MapCell getCell(int cellX, int cellY) {
    return map[cellX][cellY];
  }
}
