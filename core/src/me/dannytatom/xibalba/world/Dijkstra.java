package me.dannytatom.xibalba.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

public class Dijkstra {
  public int[][] map;

  public Dijkstra(boolean[][] geometry, Array<Vector2> goals) {
    createMap(geometry, goals);
  }

  // To get a Dijkstra map, you start with an integer array representing your map,
  // with some set of goal cells set to zero and all the rest set to a very high number.
  //
  // Iterate through the map's "floor" cells -- skip the impassable wall cells.
  // If any floor tile has a value that is at least 2 greater than its lowest-value floor neighbor
  // (in a cardinal direction - i.e. up, down, left or right), set it to be exactly 1 greater
  // than its lowest value neighbor. Repeat until no changes are made.
  //
  // The resulting grid of numbers represents the number of steps that it
  // will take to get from any given tile to the nearest goal.
  //
  // To find a path, you just walk downhill from starting position to goal position.
  public void createMap(boolean[][] geometry, Array<Vector2> goals) {
    map = new int[geometry.length][geometry[0].length];

    for (int[] row : map) {
      Arrays.fill(row, 100);
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
          boolean blocked = !WorldManager.mapHelpers.cellExists(new Vector2(x, y))
              || WorldManager.mapHelpers.getCell(x, y).isWall();

          if (!blocked) {
            Array<Integer> neighbours = new Array<>();

            if (WorldManager.mapHelpers.cellExists(new Vector2(x, y + 1))
                && !WorldManager.mapHelpers.getCell(x, y + 1).isWall()) {
              neighbours.add(map[x][y + 1]);
            }

            if (WorldManager.mapHelpers.cellExists(new Vector2(x + 1, y))
                && !WorldManager.mapHelpers.getCell(x + 1, y).isWall()) {
              neighbours.add(map[x + 1][y]);
            }

            if (WorldManager.mapHelpers.cellExists(new Vector2(x, y - 1))
                && !WorldManager.mapHelpers.getCell(x, y - 1).isWall()) {
              neighbours.add(map[x][y - 1]);
            }

            if (WorldManager.mapHelpers.cellExists(new Vector2(x - 1, y))
                && !WorldManager.mapHelpers.getCell(x - 1, y).isWall()) {
              neighbours.add(map[x - 1][y]);
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

  public Array<Vector2> findPath(Vector2 start, Vector2 end) {
    Array<Vector2> path = new Array<>();

    path.add(start);

    search:
    for (int x = (int) path.get(path.size - 1).x; x < map.length; x++) {
      for (int y = (int) path.get(path.size - 1).y; y < map[x].length; y++) {
        if (end.x == x && end.y == y) {
          break search;
        }

        int last = get((int) path.get(path.size - 1).x, (int) path.get(path.size - 1).y);

        if (WorldManager.mapHelpers.cellExists(x, y + 1) && get(x, y + 1) > last) {
          path.add(new Vector2(x, y + 1));
        } else if (WorldManager.mapHelpers.cellExists(x + 1, y) && get(x + 1, y) > last) {
          path.add(new Vector2(x + 1, y));
        } else if (WorldManager.mapHelpers.cellExists(x, y - 1) && get(x, y - 1) > last) {
          path.add(new Vector2(x, y - 1));
        } else if (WorldManager.mapHelpers.cellExists(x - 1, y) && get(x - 1, y) > last) {
          path.add(new Vector2(x - 1, y));
        }
      }
    }

    return path;
  }

  public int get(int cellX, int cellY) {
    return map[cellX][cellY];
  }
}
