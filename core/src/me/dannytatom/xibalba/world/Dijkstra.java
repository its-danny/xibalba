package me.dannytatom.xibalba.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

public class Dijkstra {
  private int[][] map;

  public Dijkstra(int width, int height, Array<Vector2> goals) {
    createMap(width, height, goals);
  }

  // To get a Dijkstra map, you start with an integer array representing your map,
  // with some set of goal cells set to zero and all the rest set to a very high number.
  //
  // Iterate through the map's "floor" cells -- skip the impassable wall cells.
  // If any floor tile has a value that is at least 2 greater than its lowest-value floor neighbor,
  // set it to be exactly 1 greater than its lowest value neighbor. Repeat until no changes are made.
  //
  // The resulting grid of numbers represents the number of steps that it
  // will take to get from any given tile to the nearest goal.
  //
  // To find a path, you just walk downhill from starting position to goal position.
  private void createMap(int width, int height, Array<Vector2> goals) {
    map = new int[width][height];

    for (int[] row : map) {
      Arrays.fill(row, 1000);
    }

    for (int i = 0; i < goals.size; i++) {
      Vector2 goal = goals.get(i);

      map[(int) goal.x][(int) goal.y] = 0;
    }

    boolean dirty = true;

    while (dirty) {
      boolean changed = false;

      for (int x = 0; x < map.length; x++) {
        for (int y = 0; y < map[x].length; y++) {
          if (canWalk(x, y)) {
            Array<Integer> neighbours = new Array<>();

            // North
            if (canWalk(x, y + 1)) {
              neighbours.add(map[x][y + 1]);
            }

            // NorthEast
            if (canWalk(x + 1, y + 1)) {
              neighbours.add(map[x + 1][y + 1]);
            }

            // East
            if (canWalk(x + 1, y)) {
              neighbours.add(map[x + 1][y]);
            }

            // SouthEast
            if (canWalk(x + 1, y - 1)) {
              neighbours.add(map[x + 1][y - 1]);
            }

            // South
            if (canWalk(x, y - 1)) {
              neighbours.add(map[x][y - 1]);
            }

            // SouthWest
            if (canWalk(x - 1, y - 1)) {
              neighbours.add(map[x - 1][y - 1]);
            }

            // West
            if (canWalk(x - 1, y)) {
              neighbours.add(map[x - 1][y]);
            }

            // NorthWest
            if (canWalk(x - 1, y + 1)) {
              neighbours.add(map[x - 1][y + 1]);
            }

            neighbours.sort();

            if (neighbours.size > 0 && map[x][y] > neighbours.first() + 2) {
              map[x][y] = neighbours.first() + 1;
              changed = true;
            }
          }
        }
      }

      dirty = changed;
    }
  }

  /**
   * Go until we find a goal of 0.
   *
   * @param start Starting position
   *
   * @return The path to take
   */
  public Array<Vector2> findPath(Vector2 start) {
    Array<Vector2> path = new Array<>();

    path.add(start);

    boolean buildingPath = true;

    while (buildingPath) {
      int lastX = (int) path.get(path.size - 1).x;
      int lastY = (int) path.get(path.size - 1).y;
      int lastValue = get(lastX, lastY);

      // Stop if we've gotten to a goal
      if (lastValue == 0) {
        buildingPath = false;
      }

      // North
      if (canWalk(lastX, lastY + 1) && get(lastX, lastY + 1) == lastValue - 1) {
        path.add(new Vector2(lastX, lastY + 1));

        continue;
      }

      // NorthEast
      if (canWalk(lastX + 1, lastY + 1) && get(lastX + 1, lastY + 1) == lastValue - 1) {
        path.add(new Vector2(lastX + 1, lastY + 1));

        continue;
      }

      // East
      if (canWalk(lastX + 1, lastY) && get(lastX + 1, lastY) == lastValue - 1) {
        path.add(new Vector2(lastX + 1, lastY));

        continue;
      }

      // SouthEast
      if (canWalk(lastX + 1, lastY - 1) && get(lastX + 1, lastY - 1) == lastValue - 1) {
        path.add(new Vector2(lastX + 1, lastY - 1));

        continue;
      }

      // South
      if (canWalk(lastX, lastY - 1) && get(lastX, lastY - 1) == lastValue - 1) {
        path.add(new Vector2(lastX, lastY - 1));

        continue;
      }

      // SouthWest
      if (canWalk(lastX - 1, lastY - 1) && get(lastX - 1, lastY - 1) == lastValue - 1) {
        path.add(new Vector2(lastX - 1, lastY - 1));

        continue;
      }

      // West
      if (canWalk(lastX - 1, lastY) && get(lastX - 1, lastY) == lastValue - 1) {
        path.add(new Vector2(lastX - 1, lastY));

        continue;
      }

      // NorthWest
      if (canWalk(lastX - 1, lastY + 1) && get(lastX - 1, lastY + 1) == lastValue - 1) {
        path.add(new Vector2(lastX - 1, lastY + 1));

        continue;
      }

      // If we couldn't find a next step, stop
      buildingPath = false;
    }

    return path;
  }

  public int get(int cellX, int cellY) {
    return map[cellX][cellY];
  }

  private boolean canWalk(int cellX, int cellY) {
    return WorldManager.mapHelpers.cellExists(cellX, cellY)
        && (WorldManager.mapHelpers.getCell(cellX, cellY).isFloor()
        || WorldManager.mapHelpers.getCell(cellX, cellY).isShallowWater());
  }
}
