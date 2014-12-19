package me.dannytatom.x2600BC.map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.x2600BC.components.PlayerComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.utils.ComponentMappers;

public class Map {
  private Engine engine;
  private boolean[][] geometry;
  private Cell[][] map;

  /**
   * Holds logic for dealing with maps.
   *
   * @param engine   The Ashley engine
   * @param geometry The map geometry
   * @param map      The actual map
   */
  public Map(Engine engine, boolean[][] geometry, Cell[][] map) {
    this.engine = engine;
    this.geometry = geometry;
    this.map = map;
  }

  /**
   * Find player position.
   *
   * @return Vector2 of player position
   */
  public Vector2 getPlayerPosition() {
    Entity player = engine.getEntitiesFor(Family.one(PlayerComponent.class).get()).first();
    PositionComponent position = ComponentMappers.position.get(player);

    return new Vector2(position.x, position.y);
  }

  public boolean isBlocked(int x, int y) {
    return map[x][y].isBlocked;
  }

  /**
   * Check if something is near the player.
   *
   * @param x        x of cell to check
   * @param y        y of cell to check
   * @param distance distance around cell to look
   * @return whether we're near the player or not
   */
  public boolean isNearPlayer(int x, int y, int distance) {
    Vector2 playerPosition = getPlayerPosition();

    return x < playerPosition.x + distance
        && x > playerPosition.x - distance
        && y < playerPosition.y + distance
        && y > playerPosition.y - distance;
  }

  /**
   * Find player start.
   *
   * @return First open cell
   */
  public Vector2 findPlayerStart() {
    Vector2 space = new Vector2();

    search:
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        if (!map[x][y].isBlocked) {
          space.add(x, y);

          break search;
        }
      }
    }

    return space;
  }

  /**
   * Find mob start.
   *
   * @return Random open cell
   */
  public Vector2 findMobStart() {
    Vector2 space = new Vector2();

    int x;
    int y;

    do {
      x = MathUtils.random(0, map.length - 1);
      y = MathUtils.random(0, map[x].length - 1);
    } while (map[x][y].isBlocked);

    space.add(x, y);

    return space;
  }
}
