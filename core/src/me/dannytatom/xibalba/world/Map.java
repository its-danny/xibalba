package me.dannytatom.xibalba.world;

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

    paintFirstCoat();
    paintSecondCoat();
  }

  public MapCell[][] getCellMap() {
    return map;
  }

  // Determine floors & walls
  private void paintFirstCoat() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y]) {
          map[x][y] = new MapCell(Main.atlas.createSprite(
              "Level/Cave/Environment/Floor/" + MathUtils.random(1, 2)
          ), false, false, "a cave floor");
        } else {
          int neighbours = getGroundNeighbours(x, y);

          if (neighbours > 0) {
            map[x][y] = new MapCell(Main.atlas.createSprite(
                "Level/Cave/UI/Color/3"
            ), true, false, "a cave wall");
          } else {
            map[x][y] = new MapCell(Main.atlas.createSprite(
                "Level/Cave/UI/Color/4"
            ), false, true, "nothing");
          }
        }

        map[x][y].sprite.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);
      }
    }
  }

  // Do corners
  private void paintSecondCoat() {
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        int neighbours = getGroundNeighbours(x, y);

        if (map[x][y].isNothing || map[x][y].isWall && neighbours > 0) {
          String spritePath = null;

          MapCell above = getCellAbove(x, y);
          MapCell right = getCellRight(x, y);
          MapCell below = getCellBelow(x, y);
          MapCell left = getCellLeft(x, y);
          MapCell aboveLeft = getCellAboveLeft(x, y);
          MapCell aboveRight = getCellAboveRight(x, y);
          MapCell belowLeft = getCellBelowLeft(x, y);
          MapCell belowRight = getCellBelowRight(x, y);

          if (
              (above == null || above.isNothing || above.isWall)
                  && (below != null && !below.isWall && !below.isNothing)
                  && (left != null && left.isWall)
                  && (right != null && right.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Front-" + MathUtils.random(1, 6);
          } else if (
              (above == null || above.isNothing || above.isWall)
                  && (below != null && below.isWall)
                  && (left == null || left.isNothing || left.isWall)
                  && (right != null && right.isWall)
                  && (belowRight != null && !belowRight.isNothing && !belowRight.isWall)
                  && (aboveLeft == null || aboveLeft.isNothing || aboveLeft.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Front-Left-Turn-Down-" + MathUtils.random(1, 2);
          } else if (
              (above == null || above.isNothing || above.isWall)
                  && (below != null && below.isWall)
                  && (left != null && left.isWall)
                  && (right == null || right.isNothing || right.isWall)
                  && (belowLeft != null && !belowLeft.isNothing && !belowLeft.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Front-Right-Turn-Down-" + MathUtils.random(1, 2);
          } else if (
              (above != null && above.isWall)
                  && (below != null && !below.isWall && !below.isNothing)
                  && (left != null && !left.isWall && !left.isNothing)
                  && (right != null && right.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Front-Left-Turn-Up-" + MathUtils.random(1, 2);
          } else if (
              (above != null && above.isWall)
                  && (below != null && !below.isWall && !below.isNothing)
                  && (left != null && left.isWall)
                  && (right != null && !right.isWall && !right.isNothing)) {
            spritePath =
                "Level/Cave/Environment/Wall/Front-Right-Turn-Up-" + MathUtils.random(1, 2);
          } else if (
              (above != null && above.isWall)
                  && (below != null && below.isWall)
                  && (left == null || left.isWall || left.isNothing)
                  && (right != null && !right.isWall && !right.isNothing)) {
            spritePath =
                "Level/Cave/Environment/Wall/Side-with-Floor-Right-1";
          } else if (
              (above != null && above.isWall)
                  && (below != null && below.isWall)
                  && (left != null && !left.isWall && !left.isNothing)
                  && (right == null || right.isWall || right.isNothing)) {
            spritePath =
                "Level/Cave/Environment/Wall/Side-with-Floor-Left-" + MathUtils.random(1, 3);
          } else if (
              (above != null && !above.isWall && !above.isNothing)
                  && (below == null || below.isNothing || below.isWall)
                  && (left != null && left.isWall)
                  && (right != null && right.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Behind-" + MathUtils.random(1, 2);
          } else if (
              (above != null && above.isWall)
                  && (below == null || below.isNothing || below.isWall)
                  && (left == null || left.isNothing || left.isWall)
                  && (right != null && right.isWall)
                  && (aboveRight != null && !aboveRight.isNothing && !aboveRight.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Behind-Left-Turn-Up-1";
          } else if (
              (above != null && above.isWall)
                  && (below == null || below.isNothing || below.isWall)
                  && (left != null && left.isWall)
                  && (right == null || right.isNothing || right.isWall)
                  && (aboveLeft != null && !aboveLeft.isNothing && !aboveLeft.isWall)
                  && (belowRight == null || belowRight.isNothing || belowRight.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Behind-Right-Turn-Up-1";
          } else if (
              (above != null && !above.isWall && !above.isNothing)
                  && (below != null && below.isWall)
                  && (left != null && !left.isWall && !left.isNothing)
                  && (right != null && right.isWall)) {
            spritePath =
                "Level/Cave/Environment/Wall/Behind-Left-Turn-Down-1";
          } else if (
              (above != null && !above.isWall && !above.isNothing)
                  && (below != null && below.isWall)
                  && (left != null && left.isWall)
                  && (right != null && !right.isWall && !right.isNothing)) {
            spritePath =
                "Level/Cave/Environment/Wall/Behind-Right-Turn-Down-1";
          }

          if (spritePath != null) {
            map[x][y].sprite.setRegion(Main.atlas.findRegion(spritePath));
          }
        }
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

  private MapCell getCellAbove(int cellX, int cellY) {
    if (cellExists(cellX, cellY + 1)) {
      return getCell(cellX, cellY + 1);
    } else {
      return null;
    }
  }

  private MapCell getCellRight(int cellX, int cellY) {
    if (cellExists(cellX + 1, cellY)) {
      return getCell(cellX + 1, cellY);
    } else {
      return null;
    }
  }

  private MapCell getCellBelow(int cellX, int cellY) {
    if (cellExists(cellX, cellY - 1)) {
      return getCell(cellX, cellY - 1);
    } else {
      return null;
    }
  }

  private MapCell getCellLeft(int cellX, int cellY) {
    if (cellExists(cellX - 1, cellY)) {
      return getCell(cellX - 1, cellY);
    } else {
      return null;
    }
  }

  private MapCell getCellAboveLeft(int cellX, int cellY) {
    if (cellExists(cellX - 1, cellY + 1)) {
      return getCell(cellX - 1, cellY + 1);
    } else {
      return null;
    }
  }

  private MapCell getCellAboveRight(int cellX, int cellY) {
    if (cellExists(cellX + 1, cellY + 1)) {
      return getCell(cellX + 1, cellY + 1);
    } else {
      return null;
    }
  }

  private MapCell getCellBelowLeft(int cellX, int cellY) {
    if (cellExists(cellX - 1, cellY - 1)) {
      return getCell(cellX - 1, cellY - 1);
    } else {
      return null;
    }
  }

  private MapCell getCellBelowRight(int cellX, int cellY) {
    if (cellExists(cellX + 1, cellY - 1)) {
      return getCell(cellX + 1, cellY - 1);
    } else {
      return null;
    }
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