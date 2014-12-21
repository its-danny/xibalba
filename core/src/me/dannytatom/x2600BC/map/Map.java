package me.dannytatom.x2600BC.map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.x2600BC.components.PlayerComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;

public class Map {
  public int width;
  public int height;
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

    this.width = this.geometry.length;
    this.height = this.geometry[0].length;
  }

  /**
   * Does a cell at this position exist?
   *
   * @param x x position of cell
   * @param y y position of cell
   * @return Whether or not a cell exists
   */
  public boolean hasCell(int x, int y) {
    return (x >= 0 && x <= map.length - 1) && (y >= 0 && y <= map[x].length - 1);
  }

  /**
   * Get the cell for this position
   *
   * @param x x position of cell
   * @param y y position of cell
   * @return The Cell instance at this position
   */
  public Cell getCell(int x, int y) {
    return map[x][y];
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
    boolean blocked = map[x][y].isWall;

    if (!blocked) {
      ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(PositionComponent.class).get());

      for (Entity entity : entities) {
        PositionComponent position = ComponentMappers.position.get(entity);

        if (position.x == x && position.y == y) {
          blocked = true;
          break;
        }
      }
    }

    return blocked;
  }

  public boolean isWalkable(int x, int y) {
    return !isBlocked(x, y);
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

  public GridCell[][] createGridCells() {
    GridCell[][] cells = new GridCell[width][height];

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        cells[x][y] = new GridCell(x, y, isWalkable(x, y));
      }
    }

    return cells;
  }

  /**
   * Find a random open cell
   *
   * @return Random open cell
   */
  public Vector2 getRandomOpenPosition() {
    int x, y;

    do {
      x = MathUtils.random(0, map.length - 1);
      y = MathUtils.random(0, map[x].length - 1);
    } while (isBlocked(x, y));

    return new Vector2(x, y);
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
        if (isWalkable(x, y)) {
          space.add(x, y);

          break search;
        }
      }
    }

    return space;
  }
}
