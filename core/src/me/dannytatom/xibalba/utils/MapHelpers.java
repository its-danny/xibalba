package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

public class MapHelpers {
  private final Main main;

  public MapHelpers(Main main) {
    this.main = main;
  }

  /**
   * Finding out if a cell exists within the map.
   *
   * @param position Position we're checking
   *
   * @return If it does indeed exist
   */
  public boolean cellExists(Vector2 position) {
    Cell[][] map = main.world.getCurrentMap().getCellMap();

    return position.x > 0 && position.x < map.length
        && position.y > 0 && position.y < map[0].length
        && getCell(position.x, position.y) != null;
  }

  /**
   * Get the cell for this position.
   *
   * @param cellX cellX pos of cell
   * @param cellY cellY pos of cell
   *
   * @return The Cell instance at this pos
   */
  public Cell getCell(int cellX, int cellY) {
    return main.world.getCurrentMap().getCellMap()[cellX][cellY];
  }

  public Cell getCell(float cellX, float cellY) {
    return getCell((int) cellX, (int) cellY);
  }

  /**
   * Returns whether or not the given position is blocked.
   *
   * @param position Position to check
   *
   * @return Is it blocked?
   */
  private boolean isBlocked(int mapIndex, Vector2 position) {
    Cell[][] map = main.world.getMap(mapIndex).getCellMap();

    boolean blocked = map[(int) position.x][(int) position.y].isWall
        || map[(int) position.x][(int) position.y].isNothing;

    if (!blocked) {
      ImmutableArray<Entity> entities =
          main.engine.getEntitiesFor(
              Family.all(PositionComponent.class).exclude(DecorationComponent.class).get()
          );

      for (Entity entity : entities) {
        PositionComponent ep = ComponentMappers.position.get(entity);

        if (ep != null && ep.map == mapIndex && ep.pos.epsilonEquals(position, 0.00001f)) {
          blocked = true;
          break;
        }
      }
    }

    return blocked;
  }

  private boolean isBlocked(Vector2 position) {
    return isBlocked(main.world.currentMapIndex, position);
  }

  public boolean isWalkable(Vector2 position) {
    return !isBlocked(position);
  }

  /**
   * Get pathfinding cells.
   *
   * @return 2d array of GridCells
   */
  public GridCell[][] createPathfindingMap() {
    Map map = main.world.getCurrentMap();
    GridCell[][] cells = new GridCell[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        cells[x][y] = new GridCell(x, y, isWalkable(new Vector2(x, y)));
      }
    }

    return cells;
  }

  /**
   * Get starting light map.
   * <p/>
   * 1 is blocked, 0 is not
   *
   * @return Resistance map
   */
  public float[][] createFovMap() {
    Map map = main.world.getCurrentMap();
    float[][] resistanceMap = new float[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        resistanceMap[x][y] = (getCell(x, y).isWall || getCell(x, y).isNothing) ? 1 : 0;
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
  public void createTargetingPath(Vector2 start, Vector2 end) {
    Map map = main.world.getCurrentMap();

    Vector2 oldTarget;
    GridCell[][] cells = new GridCell[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        boolean canTarget = cellExists(new Vector2(x, y))
            && !getCell(x, y).isWall
            && !getCell(x, y).isNothing
            && !getCell(x, y).hidden;

        cells[x][y] = new GridCell(x, y, canTarget);
      }
    }

    NavigationGrid<GridCell> grid = new NavigationGrid<>(cells, false);
    AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

    if (map.target == null) {
      oldTarget = null;
      map.target = start.cpy().add(end);
    } else {
      oldTarget = map.target.cpy();
      map.target = map.target.add(end);
    }

    map.targetingPath = finder.findPath(
        (int) start.x, (int) start.y, (int) map.target.x, (int) map.target.y, grid
    );

    // TODO: Instead of 5, range should be determined by strength
    if (map.targetingPath == null || map.targetingPath.size() > 5) {
      map.target = oldTarget;

      if (map.target != null) {
        map.targetingPath = finder.findPath(
            (int) start.x, (int) start.y, (int) map.target.x, (int) map.target.y, grid
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
    Map map = main.world.getCurrentMap();

    Vector2 oldTarget;
    GridCell[][] cells = new GridCell[map.width][map.height];

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        boolean canTarget;

        if (careAboutWalls) {
          canTarget = cellExists(new Vector2(x, y))
              && !getCell(x, y).hidden
              && !getCell(x, y).isWall;
        } else {
          canTarget = cellExists(new Vector2(x, y)) && !getCell(x, y).hidden;
        }

        cells[x][y] = new GridCell(x, y, canTarget);
      }
    }

    NavigationGrid<GridCell> grid = new NavigationGrid<>(cells, false);
    AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

    if (map.target == null) {
      oldTarget = null;
      map.target = start.cpy().add(end);
    } else {
      oldTarget = map.target.cpy();
      map.target = map.target.add(end);
    }

    map.lookingPath = finder.findPath(
        (int) start.x, (int) start.y, (int) map.target.x, (int) map.target.y, grid
    );

    if (map.lookingPath == null) {
      map.target = oldTarget;

      if (map.target != null) {
        map.lookingPath = finder.findPath(
            (int) start.x, (int) start.y, (int) map.target.x, (int) map.target.y, grid
        );
      }
    }
  }

  /**
   * Find if an entity is near the player.
   *
   * @param entity The entity to check
   * @param radius How many spaces to check
   *
   * @return Whether or not they are
   */
  public boolean isNearPlayer(Entity entity, int radius) {
    PositionComponent playerPosition = main.player.getComponent(PositionComponent.class);
    PositionComponent entityPosition = entity.getComponent(PositionComponent.class);

    return (entityPosition.pos.x == playerPosition.pos.x - radius
        || entityPosition.pos.x == playerPosition.pos.x
        || entityPosition.pos.x == playerPosition.pos.x + radius)
        && (entityPosition.pos.y == playerPosition.pos.y - radius
        || entityPosition.pos.y == playerPosition.pos.y
        || entityPosition.pos.y == playerPosition.pos.y + radius);
  }

  /**
   * Returns an open position near the player.
   *
   * @return Player position
   */
  public Vector2 getEmptySpaceNearPlayer() {
    return getEmptySpaceNearEntity(
        main.player.getComponent(PositionComponent.class).pos
    );
  }

  /**
   * Returns an open position near the given position. TODO: Make this less retarded.
   *
   * @return An open position
   */
  public Vector2 getEmptySpaceNearEntity(Vector2 pos) {
    Vector2 position;

    if (isWalkable(new Vector2(pos.x + 1, pos.y))) {
      position = new Vector2(pos.x + 1, pos.y);
    } else if (isWalkable(new Vector2(pos.x - 1, pos.y))) {
      position = new Vector2(pos.x - 1, pos.y);
    } else if (isWalkable(new Vector2(pos.x, pos.y + 1))) {
      position = new Vector2(pos.x, pos.y + 1);
    } else if (isWalkable(new Vector2(pos.x, pos.y - 1))) {
      position = new Vector2(pos.x, pos.y - 1);
    } else {
      position = null;
    }

    return position;
  }

  /**
   * Find a random open cell on any map.
   *
   * @return Random open cell
   */
  public Vector2 getRandomOpenPositionOnMap(int index) {
    Map map = main.world.getMap(index);
    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.width - 1);
      cellY = MathUtils.random(0, map.height - 1);
    } while (isBlocked(index, new Vector2(cellX, cellY)));

    return new Vector2(cellX, cellY);
  }

  /**
   * Find a random open cell on current map.
   *
   * @return Random open cell
   */
  public Vector2 getRandomOpenPosition() {
    return getRandomOpenPositionOnMap(main.world.currentMapIndex);
  }
}
