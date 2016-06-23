package me.dannytatom.xibalba.map;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

public class CaveGenerator {
  private final int width;
  private final int height;
  public boolean[][] geometry;

  /**
   * Generates a cave.
   * <p/>
   * http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels#Example_Output
   *
   * @param width  How wide the map should be in cells
   * @param height How long the map should be in cells
   */
  public CaveGenerator(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void generate() {
    geometry = new boolean[width][height];

    for (boolean[] row : geometry) {
      Arrays.fill(row, true);
    }

    initialize();

    for (int i = 0; i < 4; i++) {
      shapeGeometry();
    }

    for (int i = 0; i < 3; i++) {
      shapeGeometryAgain();
    }

    emptyGeometryEdges();
  }

  // Start off with all ground, then create emptiness randomly
  // (40% chance)
  private void initialize() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (MathUtils.random() < 0.40f) {
          geometry[x][y] = false;
        }
      }
    }
  }

  // A tile becomes empty if 5 or more of its nine neighbours are empty,
  // or it's surrounded
  private void shapeGeometry() {
    boolean[][] tempGeo = new boolean[width][height];

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        int neighbours1 = emptyNeighbours(1, x, y);
        int neighbours2 = emptyNeighbours(2, x, y);

        tempGeo[x][y] = !(neighbours1 >= 5 || neighbours2 <= 2);
      }
    }

    geometry = tempGeo;
  }

  // Same as #shapeGeometry, except we don't care about 2 step
  private void shapeGeometryAgain() {
    boolean[][] tempGeo = new boolean[width][height];

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        int neighbours = emptyNeighbours(1, x, y);

        tempGeo[x][y] = neighbours >= 3;
      }
    }

    geometry = tempGeo;
  }

  // Edge of the geometry should always be inaccessible
  private void emptyGeometryEdges() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (x == 0 || y == 0 || x == 1 || y == 1) {
          geometry[x][y] = false;
        }

        if (x == geometry.length - 1 || y == geometry[x].length - 1 || x == geometry.length - 2 || y == geometry[x].length - 2) {
          geometry[x][y] = false;
        }
      }
    }
  }

  /**
   * Returns number of empty neighbors around cell within the amount of space given.
   *
   * @param amount How many neighboring cells to check
   * @param cellX  cellX of cell to search from
   * @param cellY  cellY of cell to search from
   * @return number of empty neighbors
   */
  private int emptyNeighbours(int amount, int cellX, int cellY) {
    int count = 0;

    for (int i = -amount; i < amount + 1; i++) {
      for (int j = -amount; j < amount + 1; j++) {
        int nx = cellX + i;
        int ny = cellY + j;

        if (i != 0 || j != 0) {
          if (nx < 0 || ny < 0 || nx >= geometry.length || ny >= geometry[0].length) {
            count += 1;
          } else if (!geometry[nx][ny]) {
            count += 1;
          }
        }
      }
    }

    return count;
  }
}
