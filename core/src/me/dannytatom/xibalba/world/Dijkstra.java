package me.dannytatom.xibalba.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.apache.commons.lang3.ArrayUtils;

class Dijkstra {
  private final Map map;
  private final MapCell[][] cellMap;
  private final MapCell.Type[] walkableTypes;
  private int[][] graph;

  /**
   * Dijkstra map.
   *
   * @param map           The level map
   * @param walkableTypes A list of walkable types
   * @param goals         A list of goals for this dijkstra map
   */
  public Dijkstra(Map map, MapCell.Type[] walkableTypes, Array<Vector2> goals) {
    this.map = map;
    this.cellMap = this.map.getCellMap();
    this.walkableTypes = walkableTypes;

    createGraph(goals);
  }

  // To get a Dijkstra graph, you start with an integer array representing your graph,
  // with some set of goal cells set to zero and all the rest set to a very high number.
  //
  // Iterate through the graph's "floor" cells -- skip the impassable wall cells.
  // If any floor tile has a value that is at least 2 greater than its lowest-value floor neighbor,
  // set it to be exactly 1 greater than its lowest value neighbor.
  // Repeat until no changes are made.
  //
  // The resulting grid of numbers represents the number of steps that it
  // will take to get from any given tile to the nearest goal.
  //
  // To find a path, you just walk downhill from starting position to goal position.
  private void createGraph(Array<Vector2> goals) {
    graph = new int[map.width][map.height];

    for (int x = 0; x < graph.length; x++) {
      for (int y = 0; y < graph[x].length; y++) {
        graph[x][y] = 100;
      }
    }

    for (int i = 0; i < goals.size; i++) {
      Vector2 goal = goals.get(i);

      graph[(int) goal.x][(int) goal.y] = 0;
    }

    boolean dirty = true;

    while (dirty) {
      boolean changed = false;

      for (int x = 0; x < graph.length; x++) {
        for (int y = 0; y < graph[x].length; y++) {
          if (canWalk(x, y)) {
            int bestNeighbour = 100;

            // North
            if (canWalk(x, y + 1)) {
              if (graph[x][y + 1] < bestNeighbour) {
                bestNeighbour = graph[x][y + 1];
              }
            }

            // NorthEast
            if (canWalk(x + 1, y + 1)) {
              if (graph[x + 1][y + 1] < bestNeighbour) {
                bestNeighbour = graph[x + 1][y + 1];
              }
            }

            // East
            if (canWalk(x + 1, y)) {
              if (graph[x + 1][y] < bestNeighbour) {
                bestNeighbour = graph[x + 1][y];
              }
            }

            // SouthEast
            if (canWalk(x + 1, y - 1)) {
              if (graph[x + 1][y - 1] < bestNeighbour) {
                bestNeighbour = graph[x + 1][y - 1];
              }
            }

            // South
            if (canWalk(x, y - 1)) {
              if (graph[x][y - 1] < bestNeighbour) {
                bestNeighbour = graph[x][y - 1];
              }
            }

            // SouthWest
            if (canWalk(x - 1, y - 1)) {
              if (graph[x - 1][y - 1] < bestNeighbour) {
                bestNeighbour = graph[x - 1][y - 1];
              }
            }

            // West
            if (canWalk(x - 1, y)) {
              if (graph[x - 1][y] < bestNeighbour) {
                bestNeighbour = graph[x - 1][y];
              }
            }

            // NorthWest
            if (canWalk(x - 1, y + 1)) {
              if (graph[x - 1][y + 1] < bestNeighbour) {
                bestNeighbour = graph[x - 1][y + 1];
              }
            }

            if (graph[x][y] > bestNeighbour + 2) {
              graph[x][y] = bestNeighbour + 1;
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

  private int get(int cellX, int cellY) {
    return graph[cellX][cellY];
  }

  private boolean canWalk(int cellX, int cellY) {
    return cellX > 0 && cellX < cellMap.length
        && cellY > 0 && cellY < cellMap[0].length
        && ArrayUtils.contains(walkableTypes, cellMap[cellX][cellY].type);
  }
}
