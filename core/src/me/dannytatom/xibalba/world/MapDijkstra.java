package me.dannytatom.xibalba.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MapDijkstra {
  public Dijkstra explore;
  public Array<Vector2> exploreGoals;

  public MapDijkstra() {

  }

  public void updateExplore() {
    Map map = WorldManager.world.getCurrentMap();
    exploreGoals = new Array<>();

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        if (map.getCellMap()[x][y].hidden && map.getCellMap()[x][y].isFloor()) {
          exploreGoals.add(new Vector2(x, y));
        }
      }
    }

    explore = new Dijkstra(map.width, map.height, exploreGoals);
  }

  public Array<Vector2> findExplorePath(Vector2 start) {
    return explore.findPath(start);
  }
}
