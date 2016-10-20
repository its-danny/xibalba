package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.TrapComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.Map;
import me.dannytatom.xibalba.world.MapCell;
import me.dannytatom.xibalba.world.WorldManager;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.ArrayList;

public class MapHelpers {
  public MapHelpers() {

  }

  /**
   * Finding out if a cell exists within the world.
   *
   * @param cellX x of the position we're checking
   * @param cellY y of the position we're checking
   *
   * @return If it does indeed exist
   */
  public boolean cellExists(int cellX, int cellY) {
    MapCell[][] map = WorldManager.world.getCurrentMap().getCellMap();

    return cellX > 0 && cellX < map.length
        && cellY > 0 && cellY < map[0].length
        && getCell(cellX, cellY) != null;
  }

  public boolean cellExists(Vector2 position) {
    return cellExists((int) position.x, (int) position.y);
  }

  private MapCell getCell(int mapIndex, int cellX, int cellY) {
    return WorldManager.world.getMap(mapIndex).getCellMap()[cellX][cellY];
  }

  public MapCell getCell(int cellX, int cellY) {
    return getCell(WorldManager.world.currentMapIndex, cellX, cellY);
  }

  public MapCell getCell(float cellX, float cellY) {
    return getCell(WorldManager.world.currentMapIndex, (int) cellX, (int) cellY);
  }

  public boolean isBlocked(Vector2 position) {
    return isBlocked(WorldManager.world.currentMapIndex, position);
  }

  /**
   * Returns whether or not the given position is blocked.
   *
   * @param position Position to check
   *
   * @return Is it blocked?
   */
  public boolean isBlocked(int mapIndex, Vector2 position) {
    MapCell[][] map = WorldManager.world.getMap(mapIndex).getCellMap();

    boolean blocked = map[(int) position.x][(int) position.y].isWall()
        || map[(int) position.x][(int) position.y].isNothing();

    if (!blocked) {
      ImmutableArray<Entity> entities =
          WorldManager.engine.getEntitiesFor(Family.all(PositionComponent.class).get());

      for (Entity entity : entities) {
        PositionComponent ep = ComponentMappers.position.get(entity);

        if (ep != null && ep.pos.epsilonEquals(position, 0.00001f)) {
          if (ComponentMappers.decoration.has(entity)) {
            if (ComponentMappers.decoration.get(entity).blocks) {
              blocked = true;
              break;
            }
          } else if (ComponentMappers.trap.has(entity)) {
            blocked = false;
          } else {
            blocked = true;
            break;
          }
        }
      }
    }

    return blocked;
  }

  /**
   * Get pathfinding cells.
   *
   * @return 2d array of GridCells
   */
  public GridCell[][] createPathfindingMap(boolean avoidDeepWater) {
    Map map = WorldManager.world.getCurrentMap();
    GridCell[][] cells = new GridCell[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        boolean walkable = !isBlocked(WorldManager.world.currentMapIndex, new Vector2(x, y));

        if (walkable && avoidDeepWater) {
          walkable = !getCell(x, y).isDeepWater();
        }

        cells[x][y] = new GridCell(x, y, walkable);
      }
    }

    return cells;
  }

  /**
   * Get starting light world. 1 is blocked, 0 is not.
   *
   * @return Resistance world
   */
  float[][] createFovMap() {
    Map map = WorldManager.world.getCurrentMap();
    float[][] resistanceMap = new float[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        resistanceMap[x][y] = (getCell(x, y).isWall() || getCell(x, y).isNothing()) ? 1 : 0;
      }
    }

    return resistanceMap;
  }

  /**
   * Create path for targeting (used for throwing weapons).
   *
   * @param start Starting cell
   * @param end   Where they're throwing to
   */
  void createTargetingPath(Vector2 start, Vector2 end) {
    Map map = WorldManager.world.getCurrentMap();

    Vector2 oldTarget;
    GridCell[][] cells = new GridCell[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        boolean canTarget = cellExists(new Vector2(x, y))
            && !getCell(x, y).isWall()
            && !getCell(x, y).isNothing()
            && !getCell(x, y).hidden;

        cells[x][y] = new GridCell(x, y, canTarget);
      }
    }

    NavigationGrid<GridCell> grid = new NavigationGrid<>(cells, false);
    AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

    PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);

    if (playerDetails.target == null) {
      oldTarget = null;
      playerDetails.target = start.cpy().add(end);
    } else {
      oldTarget = playerDetails.target.cpy();
      playerDetails.target = playerDetails.target.add(end);
    }

    playerDetails.path = finder.findPath(
        (int) start.x, (int) start.y,
        (int) playerDetails.target.x, (int) playerDetails.target.y, grid
    );

    AttributesComponent playerAttributes = ComponentMappers.attributes.get(WorldManager.player);
    int maxDistance = (playerAttributes.strength <= 4 ? 4 : playerAttributes.strength);

    if (playerDetails.path == null || playerDetails.path.size() > maxDistance) {
      playerDetails.target = oldTarget;

      if (playerDetails.target != null) {
        playerDetails.path = finder.findPath(
            (int) start.x, (int) start.y,
            (int) playerDetails.target.x, (int) playerDetails.target.y, grid
        );
      }
    }
  }

  /**
   * Create a path for looking around.
   *
   * @param start Start position
   * @param end   End position
   */
  public void createLookingPath(Vector2 start, Vector2 end, boolean careAboutWalls) {
    Map map = WorldManager.world.getCurrentMap();

    Vector2 oldTarget;
    GridCell[][] cells = new GridCell[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        boolean canTarget;

        if (careAboutWalls) {
          canTarget = cellExists(new Vector2(x, y))
              && !getCell(x, y).hidden
              && !getCell(x, y).isWall();
        } else {
          canTarget = cellExists(new Vector2(x, y)) && !getCell(x, y).hidden;
        }

        cells[x][y] = new GridCell(x, y, canTarget);
      }
    }

    NavigationGrid<GridCell> grid = new NavigationGrid<>(cells, false);
    AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

    PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);

    if (playerDetails.target == null) {
      oldTarget = null;
      playerDetails.target = start.cpy().add(end);
    } else {
      oldTarget = playerDetails.target.cpy();
      playerDetails.target = playerDetails.target.add(end);
    }

    playerDetails.path = finder.findPath(
        (int) start.x, (int) start.y,
        (int) playerDetails.target.x, (int) playerDetails.target.y, grid
    );

    if (playerDetails.path == null) {
      playerDetails.target = oldTarget;

      if (playerDetails.target != null) {
        playerDetails.path = finder.findPath(
            (int) start.x, (int) start.y,
            (int) playerDetails.target.x, (int) playerDetails.target.y, grid
        );
      }
    }
  }

  public Entity getEntityAt(Vector2 position) {
    return getEntityAt(position.x, position.y);
  }

  /**
   * Try to find an entity at x, y.
   *
   * @param cellX x
   * @param cellY y
   *
   * @return Either the entity or null if none were found
   */
  public Entity getEntityAt(float cellX, float cellY) {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(
            Family.all(PositionComponent.class).exclude(DecorationComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition.pos.x == cellX && entityPosition.pos.y == cellY) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get all entities at a given position.
   *
   * @param position Where we're searching
   *
   * @return ArrayList of entities
   */
  public ArrayList<Entity> getEntitiesAt(Vector2 position) {
    ArrayList<Entity> list = new ArrayList<>();

    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(PositionComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition != null && entityPosition.pos.epsilonEquals(position, 0.00001f)) {
        list.add(entity);
      }
    }

    return list;
  }

  /**
   * Get enemy from a location.
   *
   * @param position Where the enemy is
   *
   * @return The enemy
   */
  public Entity getEnemyAt(Vector2 position) {
    return getEnemyAt((int) position.x, (int) position.y);
  }

  /**
   * Similar to getEntityAt, but only return an entity if it contains EnemyComponent.
   *
   * @param cellX x
   * @param cellY y
   *
   * @return Either an enemy or null if none were found
   */
  public Entity getEnemyAt(int cellX, int cellY) {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition.pos.x == cellX && entityPosition.pos.y == cellY) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get all entities in vision of the player.
   *
   * @return An ArrayList of entities
   */
  ArrayList<Entity> getEnemiesInPlayerVision() {
    ArrayList<Entity> enemies = new ArrayList<>();

    PositionComponent position = ComponentMappers.position.get(WorldManager.player);
    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);

    int positionX = (int) position.pos.x;
    int positionY = (int) position.pos.y;

    for (int x = positionX - attributes.vision; x < positionX + attributes.vision; x++) {
      for (int y = positionY - attributes.vision; y < positionY + attributes.vision; y++) {
        Entity enemy = getEnemyAt(x, y);

        if (enemy != null) {
          enemies.add(enemy);
        }
      }
    }

    enemies.sort((enemy1, enemy2) -> {
      PositionComponent enemy1Position = ComponentMappers.position.get(enemy1);
      PositionComponent enemy2Position = ComponentMappers.position.get(enemy2);

      return enemy1Position.pos.x > enemy2Position.pos.x
          && enemy1Position.pos.y > enemy2Position.pos.y
          ? -1 : 1;
    });

    return enemies;
  }

  /**
   * Get trap at given position.
   *
   * @param position The position to check
   *
   * @return The trap if one is found, null if not
   */
  public Entity getTrapAt(Vector2 position) {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(
            Family.all(TrapComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition.pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get a random space within 1 cel radius of the entity.
   *
   * @param target Area to check
   *
   * @return Vector2 position if one is found, null otherwise
   */
  public Vector2 getRandomOpenSpaceNearEntity(Entity target) {
    Vector2 targetPosition = ComponentMappers.position.get(target).pos;
    Vector2 position;

    int cellX = MathUtils.random(-1, 1);
    int cellY = MathUtils.random(-1, 1);

    if (!isBlocked(
        WorldManager.world.currentMapIndex,
        new Vector2(targetPosition.x + cellX, targetPosition.y + cellY))
        ) {
      position = new Vector2(targetPosition.x + cellX, targetPosition.y + cellY);
    } else {
      position = null;
    }

    return position;
  }

  /**
   * Find a random open cell on any world.
   *
   * @return Random open cell
   */
  public Vector2 getRandomOpenPosition(int index) {
    Map map = WorldManager.world.getMap(index);
    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.width - 1);
      cellY = MathUtils.random(0, map.height - 1);
    }
    while (isBlocked(index, new Vector2(cellX, cellY)));

    return new Vector2(cellX, cellY);
  }

  /**
   * Find a random open cell on current world.
   *
   * @return Random open cell
   */
  public Vector2 getRandomOpenPosition() {
    return getRandomOpenPosition(WorldManager.world.currentMapIndex);
  }

  /**
   * Get count of wall neighbours.
   *
   * @param mapIndex Which world to check
   * @param cellX    X position of cell we're checking around
   * @param cellY    Y position of cell we're checking around
   *
   * @return Amount of wall neighbours around it
   */
  public int getWallNeighbours(int mapIndex, int cellX, int cellY) {
    int count = 0;

    MapCell.Type[][] geometry = WorldManager.world.getMap(mapIndex).geometry;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int nx = cellX + i;
        int ny = cellY + j;

        if (i != 0 || j != 0) {
          if (nx >= 0 && ny >= 0 && nx < geometry.length && ny < geometry[0].length) {
            if (geometry[nx][ny] == MapCell.Type.WALL) {
              count += 1;
            }
          } else {
            count += 1;
          }
        } else {
          count += 1;
        }
      }
    }

    return count;
  }

  /**
   * Change floor to wet color.
   *
   * @param position Cell to make wet
   */
  public void makeFloorWet(Vector2 position) {
    getCell(position.x, position.y).sprite.setColor(
        Colors.get(WorldManager.world.getCurrentMap().type + "FloorWet")
    );
  }

  public void makeFloorBloody(Vector2 position) {
    getCell(position.x, position.y).sprite.setColor(Colors.get("RED"));
  }
}
