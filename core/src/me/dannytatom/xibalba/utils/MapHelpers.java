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
import me.dannytatom.xibalba.map.ShadowCaster;
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
    Cell[][] map = main.getMap().getCellMap();

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
    return main.getMap().getCellMap()[cellX][cellY];
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
  private boolean isBlocked(Vector2 position) {
    Cell[][] map = main.getMap().getCellMap();

    boolean blocked = map[(int) position.x][(int) position.y].isWall
        || map[(int) position.x][(int) position.y].isNothing;

    if (!blocked) {
      ImmutableArray<Entity> entities =
          main.engine.getEntitiesFor(
              Family.all(PositionComponent.class).exclude(DecorationComponent.class).get()
          );

      for (Entity entity : entities) {
        PositionComponent ep = ComponentMappers.position.get(entity);

        if (ep != null && ep.pos.epsilonEquals(position, 0.00001f)) {
          blocked = true;
          break;
        }
      }
    }

    return blocked;
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
    Map map = main.getMap();
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
    Map map = main.getMap();
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
    Map map = main.getMap();

    if (map.target != null && map.target.epsilonEquals(start.cpy().add(end), 0.00001f)) {
      return;
    }

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
    Map map = main.getMap();

    if (map.target != null && map.target.epsilonEquals(start.cpy().add(end), 0.00001f)) {
      return;
    }

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
   * Returns an open position near the player.
   *
   * @return Player position
   */
  public Vector2 getNearPlayer() {
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
   * Check if something is near the player.
   *
   * @param position Starting position
   *
   * @return Whether we're near the player or not
   */
  public boolean isNearPlayer(Vector2 position) {
    Vector2 playerPosition = main.player.getComponent(PositionComponent.class).pos;

    return position.x <= playerPosition.x + 1
        && position.x >= playerPosition.x - 1
        && position.y <= playerPosition.y + 1
        && position.y >= playerPosition.y - 1;
  }

  /**
   * Uses light map to determine if they can see the player.
   *
   * @param position Entity's position
   * @param distance Radius to use
   *
   * @return Can they see the player?
   */
  public boolean canSeePlayer(Vector2 position, int distance) {
    ShadowCaster caster = new ShadowCaster();
    float[][] lightMap = caster.calculateFov(createFovMap(),
        (int) position.x, (int) position.y, distance);
    Vector2 playerPosition = main.player.getComponent(PositionComponent.class).pos;

    return lightMap[(int) playerPosition.x][(int) playerPosition.y] > 0;
  }

  /**
   * Find a random open cell.
   *
   * @return Random open cell
   */
  public Vector2 getRandomOpenPosition() {
    Map map = main.getMap();
    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.width - 1);
      cellY = MathUtils.random(0, map.height - 1);
    } while (isBlocked(new Vector2(cellX, cellY)));

    return new Vector2(cellX, cellY);
  }
}
