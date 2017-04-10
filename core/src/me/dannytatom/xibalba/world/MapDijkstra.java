package me.dannytatom.xibalba.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MapDijkstra {
  private Map map;
  public Array<Vector2> exploreGoals;
  public Dijkstra wander;
  public Dijkstra explore;
  public Dijkstra playerPosition;

  public MapDijkstra(Map map) {
    this.map = map;
  }

  public void updateAll() {
    updateWander();
    updateExplore();
    updatePlayerPosition();
  }

  public void updateWander() {
    Array<Vector2> goals = new Array<>();

    for (int i = 0; i < 20; i++) {
      goals.add(WorldManager.mapHelpers.getRandomOpenPosition(map.depth));
    }

    wander = new Dijkstra(map, goals);
  }

  public Array<Vector2> findWanderPath(Vector2 start) {
    return wander.findPath(start);
  }

  public void updateExplore() {
    exploreGoals = new Array<>();

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        if (map.getCellMap()[x][y].hidden && map.getCellMap()[x][y].isFloor()) {
          exploreGoals.add(new Vector2(x, y));
        }
      }
    }

    explore = new Dijkstra(map, exploreGoals);
  }

  public Array<Vector2> findExplorePath(Vector2 start) {
    return explore.findPath(start);
  }

  public void updatePlayerPosition() {
    Vector2 position = ComponentMappers.position.get(WorldManager.player).pos;
    Array<Vector2> goals = new Array<>();
    goals.add(position);

    playerPosition = new Dijkstra(map, goals);
  }

  public Array<Vector2> findPlayerPositionPath(Vector2 start) {
    return playerPosition.findPath(start);
  }
}
