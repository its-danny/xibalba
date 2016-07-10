package me.dannytatom.xibalba.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

public class Map {
  public final int width;
  public final int height;
  public final boolean[][] geometry;
  private final TextureAtlas atlas;
  private final Sprite defaultWallSprite;
  private Cell[][] map;

  /**
   * Holds logic for dealing with maps.
   *
   * @param geometry The map geometry
   * @param atlas    TextureAtlas that holds map sprites
   */
  public Map(boolean[][] geometry, TextureAtlas atlas) {
    this.geometry = geometry;
    this.atlas = atlas;

    this.width = this.geometry.length;
    this.height = this.geometry[0].length;

    defaultWallSprite = atlas.createSprite("Level/Cave/UI/Color/3");
  }

  /**
   * Start creating Cells and giving em sprites.
   */
  public void paintCave() {
    map = new Cell[width][height];

    paintFirstCoat();
    paintSecondCoat();
  }

  public Cell[][] getCellMap() {
    return map;
  }

  // Determine floors & walls
  private void paintFirstCoat() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y]) {
          map[x][y] = new Cell(atlas.createSprite(
              "Level/Cave/Environment/Floor/" + MathUtils.random(1, 2)
          ), false, false, "a cave floor");
        } else {
          int neighbours = getGroundNeighbours(x, y);

          if (neighbours > 0) {
            map[x][y] = new Cell(defaultWallSprite, true, false, "a cave wall");
          } else {
            map[x][y] = new Cell(atlas.createSprite(
                "Level/Cave/UI/Color/4"
            ), false, true, "nothing");
          }
        }
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

          Cell above = getCellAbove(x, y);
          Cell right = getCellRight(x, y);
          Cell below = getCellBelow(x, y);
          Cell left = getCellLeft(x, y);
          Cell aboveLeft = getCellAboveLeft(x, y);
          Cell aboveRight = getCellAboveRight(x, y);
          Cell belowLeft = getCellBelowLeft(x, y);
          Cell belowRight = getCellBelowRight(x, y);

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
            map[x][y].sprite = atlas.createSprite(spritePath);
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

  private Cell getCellAbove(int cellX, int cellY) {
    if (cellExists(cellX, cellY + 1)) {
      return getCell(cellX, cellY + 1);
    } else {
      return null;
    }
  }

  private Cell getCellRight(int cellX, int cellY) {
    if (cellExists(cellX + 1, cellY)) {
      return getCell(cellX + 1, cellY);
    } else {
      return null;
    }
  }

  private Cell getCellBelow(int cellX, int cellY) {
    if (cellExists(cellX, cellY - 1)) {
      return getCell(cellX, cellY - 1);
    } else {
      return null;
    }
  }

  private Cell getCellLeft(int cellX, int cellY) {
    if (cellExists(cellX - 1, cellY)) {
      return getCell(cellX - 1, cellY);
    } else {
      return null;
    }
  }

  private Cell getCellAboveLeft(int cellX, int cellY) {
    if (cellExists(cellX - 1, cellY + 1)) {
      return getCell(cellX - 1, cellY + 1);
    } else {
      return null;
    }
  }

  private Cell getCellAboveRight(int cellX, int cellY) {
    if (cellExists(cellX + 1, cellY + 1)) {
      return getCell(cellX + 1, cellY + 1);
    } else {
      return null;
    }
  }

  private Cell getCellBelowLeft(int cellX, int cellY) {
    if (cellExists(cellX - 1, cellY - 1)) {
      return getCell(cellX - 1, cellY - 1);
    } else {
      return null;
    }
  }

  private Cell getCellBelowRight(int cellX, int cellY) {
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

  private Cell getCell(int cellX, int cellY) {
    return map[cellX][cellY];
  }
}
