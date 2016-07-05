package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EntranceComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
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

  private Cell getCell(int mapIndex, int cellX, int cellY) {
    return main.world.getMap(mapIndex).getCellMap()[cellX][cellY];
  }

  public Cell getCell(int cellX, int cellY) {
    return getCell(main.world.currentMapIndex, cellX, cellY);
  }

  public Cell getCell(float cellX, float cellY) {
    return getCell(main.world.currentMapIndex, (int) cellX, (int) cellY);
  }

  /**
   * Returns whether or not the given position is blocked.
   *
   * @param position Position to check
   *
   * @return Is it blocked?
   */
  public boolean isBlocked(int mapIndex, Vector2 position) {
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
        cells[x][y] = new GridCell(x, y, !isBlocked(main.world.currentMapIndex, new Vector2(x, y)));
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

    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);

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

    // TODO: Instead of 5, range should be determined by strength
    if (playerDetails.path == null || playerDetails.path.size() > 5) {
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

    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);

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

  /**
   * Find if an entity is near the player.
   *
   * @param entity The entity to check
   * @param radius How many spaces to check
   *
   * @return Whether or not they are
   */
  public boolean isNearPlayer(Entity entity, int radius) {
    PositionComponent playerPosition = ComponentMappers.position.get(main.player);
    PositionComponent entityPosition = ComponentMappers.position.get(entity);

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
  public Vector2 getOpenSpaceNearPlayer() {
    return getOpenSpaceNearEntity(ComponentMappers.position.get(main.player).pos);
  }

  /**
   * Returns an open position near the given position. TODO: Make this less retarded.
   *
   * @return An open position
   */
  private Vector2 getOpenSpaceNearEntity(Vector2 pos) {
    Vector2 position;

    if (!isBlocked(main.world.currentMapIndex, new Vector2(pos.x + 1, pos.y))) {
      position = new Vector2(pos.x + 1, pos.y);
    } else if (!isBlocked(main.world.currentMapIndex, new Vector2(pos.x - 1, pos.y))) {
      position = new Vector2(pos.x - 1, pos.y);
    } else if (!isBlocked(main.world.currentMapIndex, new Vector2(pos.x, pos.y + 1))) {
      position = new Vector2(pos.x, pos.y + 1);
    } else if (!isBlocked(main.world.currentMapIndex, new Vector2(pos.x, pos.y - 1))) {
      position = new Vector2(pos.x, pos.y - 1);
    } else {
      position = null;
    }

    return position;
  }

  /**
   * Get position of entrance.
   *
   * @return The position
   */
  public Vector2 getEntrancePosition() {
    ImmutableArray<Entity> entrances =
        main.engine.getEntitiesFor(Family.all(EntranceComponent.class).get());

    for (Entity entrance : entrances) {
      PositionComponent position = ComponentMappers.position.get(entrance);

      if (position.map == main.world.currentMapIndex) {
        return position.pos;
      }
    }

    return getRandomOpenPositionOnMap(main.world.currentMapIndex);
  }

  /**
   * Get position of exit.
   *
   * @return The position
   */
  public Vector2 getExitPosition() {
    ImmutableArray<Entity> exits =
        main.engine.getEntitiesFor(Family.all(ExitComponent.class).get());

    for (Entity exit : exits) {
      PositionComponent position = ComponentMappers.position.get(exit);

      if (position.map == main.world.currentMapIndex) {
        return position.pos;
      }
    }

    return getRandomOpenPositionOnMap(main.world.currentMapIndex);
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

  /**
   * Get count of wall neighbours.
   *
   * @param mapIndex Which map to check
   * @param cellX    X position of cell we're checking around
   * @param cellY    Y position of cell we're checking around
   *
   * @return Amount of wall neighbours around it
   */
  public int getWallNeighbours(int mapIndex, int cellX, int cellY) {
    int count = 0;

    boolean[][] geometry = main.world.getMap(mapIndex).geometry;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int nx = cellX + i;
        int ny = cellY + j;

        if (i != 0 || j != 0) {
          if (nx >= 0 && ny >= 0 && nx < geometry.length && ny < geometry[0].length) {
            if (!geometry[nx][ny]) {
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
}
