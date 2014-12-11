package me.dannytatom.x2600BC.generators;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.x2600BC.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CaveGenerator {
  public int[][] geometry;
  public Sprite[][] map;
  int width;
  int height;
  TextureAtlas atlas;

  /**
   * Generates a cave.
   *
   * @param atlas  Texture atlas to use when placing tiles
   * @param width  How wide the map should be in cells
   * @param height How long the map should be in cells
   */
  public CaveGenerator(TextureAtlas atlas, int width, int height) {
    this.atlas = atlas;
    this.width = width;
    this.height = height;

    geometry = new int[width][height];
    map = new Sprite[width][height];

    for (int[] row : geometry) {
      Arrays.fill(row, Constants.GROUND);
    }

    initialize();

    for (int i = 0; i < 4; i++) {
      firstShapingStep();
    }

    for (int i = 0; i < 3; i++) {
      secondShapingStep();
    }

    emptyEdges();
    makeWalls();
    paintSprites();
  }

  // Start off with all ground, then create emptiness randomly
  // (43% chance)
  private void initialize() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (MathUtils.random() < 0.43f) {
          geometry[x][y] = Constants.EMPTINESS;
        }
      }
    }
  }

  // A tile becomes empty if it's already empty and 4 or more of its nine neighbours are empty,
  // or if it is not empty and 5 or more neighbours are or the tile is in open space
  private void firstShapingStep() {
    int[][] newGeo = new int[width][height];

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        int neighbours1 = emptyNeighbours(1, x, y);
        int neighbours2 = emptyNeighbours(2, x, y);

        if (geometry[x][y] == Constants.EMPTINESS) {
          if (neighbours1 >= 4) {
            newGeo[x][y] = Constants.EMPTINESS;
          } else {
            newGeo[x][y] = Constants.GROUND;
          }
        } else {
          if (neighbours1 >= 5 || neighbours2 <= 2) {
            newGeo[x][y] = Constants.EMPTINESS;
          } else {
            newGeo[x][y] = Constants.GROUND;
          }
        }
      }
    }

    geometry = newGeo;
  }

  // Same as #firstShapingStep, except we don't care about open space
  private void secondShapingStep() {
    int[][] newGeo = new int[width][height];

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        int neighbours = emptyNeighbours(1, x, y);

        if (geometry[x][y] == Constants.EMPTINESS) {
          if (neighbours >= 4) {
            newGeo[x][y] = Constants.EMPTINESS;
          } else {
            newGeo[x][y] = Constants.GROUND;
          }
        } else {
          if (neighbours >= 5) {
            newGeo[x][y] = Constants.EMPTINESS;
          } else {
            newGeo[x][y] = Constants.GROUND;
          }
        }
      }
    }

    geometry = newGeo;
  }

  // Edge of the geometry should always be inaccessible
  private void emptyEdges() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (x == 0 || y == 0) {
          geometry[x][y] = Constants.EMPTINESS;
        }

        if (x == geometry.length - 1 || y == geometry[x].length - 1) {
          geometry[x][y] = Constants.EMPTINESS;
        }
      }
    }
  }

  // If a space is empty & has at least some ground near it, make a well!
  private void makeWalls() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        int neighbours = groundNeighbours(x, y);

        if (geometry[x][y] == Constants.EMPTINESS && neighbours > 0) {
          geometry[x][y] = Constants.WALL;
        }
      }
    }
  }

  // I'M GONNA PAINT THE CAVE RED
  private void paintSprites() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        switch (geometry[x][y]) {
          case Constants.GROUND:
            float rand = MathUtils.random();

            if (rand <= .8) {
              // Less dirty
              map[x][y] = atlas.createSprite("caveFloor-" + MathUtils.random(10, 16));
            } else {
              // More dirty
              map[x][y] = atlas.createSprite("caveFloor-" + MathUtils.random(1, 9));
            }

            break;
          case Constants.WALL:
            map[x][y] = atlas.createSprite("caveWallBack-1");
            break;
          default:
            map[x][y] = atlas.createSprite("nothing");
        }
      }
    }
  }

  // Returns number of empty neighbours around (x, y) within
  // the amount of spaces given

  /**
   * Returns number of empty neighbors around cell within
   * the amount of space given.
   *
   * @param amount How many neighboring cells to check
   * @param x      x of cell to search from
   * @param y      y of cell to search from
   * @return number of empty neighbors
   */
  private int emptyNeighbours(int amount, int x, int y) {
    int count = 0;

    for (int i = -amount; i < amount + 1; i++) {
      for (int j = -amount; j < amount + 1; j++) {
        int nx = x + i;
        int ny = y + j;

        if (i != 0 || j != 0) {
          if (nx < 0 || ny < 0 || nx >= geometry.length || ny >= geometry[0].length) {
            count += 1;
          } else if (geometry[nx][ny] == Constants.EMPTINESS) {
            count += 1;
          }
        }
      }
    }

    return count;
  }

  /**
   * Get number of ground cells around a cell.
   *
   * @param x x of cell to search from
   * @param y x of cell to search from
   * @return number of ground neighbors
   */
  private int groundNeighbours(int x, int y) {
    int count = 0;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int nx = x + i;
        int ny = y + j;

        if (i != 0 || j != 0) {
          if (nx >= 0 && ny >= 0 && nx < geometry.length && ny < geometry[0].length) {
            if (geometry[nx][ny] == Constants.GROUND) {
              count += 1;
            }
          }
        }
      }
    }

    return count;
  }

  /**
   * Find a good starting area for player.
   *
   * @return x & y position of cell
   */
  public Map<String, Integer> findPlayerStart() {
    Map<String, Integer> space = new HashMap<>();

    search:
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y] == Constants.GROUND) {
          space.put("x", x);
          space.put("y", y);

          break search;
        }
      }
    }

    return space;
  }

  /**
   * Find a good spot for a mob.
   *
   * @return x & y position of cell
   */
  public Map<String, Integer> findMobStart() {
    Map<String, Integer> space = new HashMap<>();
    int x;
    int y;

    do {
      x = MathUtils.random(0, geometry.length - 1);
      y = MathUtils.random(0, geometry[x].length - 1);
    } while (geometry[x][y] != Constants.GROUND);

    space.put("x", x);
    space.put("y", y);

    return space;
  }
}
