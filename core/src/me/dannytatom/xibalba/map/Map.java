package me.dannytatom.xibalba.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.List;

public class Map {
  public final int width;
  public final int height;
  private final Main main;
  private final boolean[][] geometry;
  private final TextureAtlas atlas;
  private final Sprite defaultWallSprite;
  public List<GridCell> lookingPath = null;
  public List<GridCell> targetingPath = null;
  public Vector2 target = null;
  private Cell[][] map;

  /**
   * Holds logic for dealing with maps.
   *
   * @param main     Instance of Main
   * @param geometry The map geometry
   */
  public Map(Main main, boolean[][] geometry) {
    this.main = main;
    this.geometry = geometry;

    this.width = this.geometry.length;
    this.height = this.geometry[0].length;

    atlas = main.assets.get("sprites/main.atlas");
    defaultWallSprite = atlas.createSprite("Level/Cave/UI/Color/3");
  }

  /**
   * Start creating Cells and giving em sprites.
   */
  public void paintCave() {
    map = new Cell[width][height];

    paintFirstCoat();
    paintSecondCoat();
  }

  // Determine floors & walls
  private void paintFirstCoat() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (geometry[x][y]) {
          map[x][y] = new Cell(atlas.createSprite(
              "Level/Cave/Environment/Floor/" + MathUtils.random(1, 2)
          ), false, false, "a cave floor");
        } else {
          int neighbours = groundNeighbours(x, y);

          if (neighbours > 0) {
            map[x][y] = new Cell(defaultWallSprite, true, false, "a cave wall");
          } else {
            map[x][y] = new Cell(atlas.createSprite(
                "Level/Cave/UI/Color/4"
            ), false, true, "nothing");
          }
        }
      }
    }
  }

  // Do corners
  private void paintSecondCoat() {
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        int neighbours = groundNeighbours(x, y);

        if (map[x][y].isNothing || map[x][y].isWall && neighbours > 0) {
          String spritePath = null;

          Cell cellAbove = getCellAbove(x, y);
          Cell cellRight = getCellRight(x, y);
          Cell cellBelow = getCellBelow(x, y);
          Cell cellLeft = getCellLeft(x, y);
          Cell cellAboveLeft = getCellAboveLeft(x, y);
          Cell cellAboveRight = getCellAboveRight(x, y);
          Cell cellBelowLeft = getCellBelowLeft(x, y);
          Cell cellBelowRight = getCellBelowRight(x, y);

          if (
              (cellAbove == null || cellAbove.isNothing || cellAbove.isWall)
                  && (cellBelow != null && !cellBelow.isWall && !cellBelow.isNothing)
                  && (cellLeft != null && cellLeft.isWall)
                  && (cellRight != null && cellRight.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Front-" + MathUtils.random(1, 3);
          } else if (
              (cellAbove == null || cellAbove.isNothing || cellAbove.isWall)
                  && (cellBelow != null && cellBelow.isWall)
                  && (cellLeft == null || cellLeft.isNothing || cellLeft.isWall)
                  && (cellRight != null && cellRight.isWall)
                  && (cellBelowRight != null && !cellBelowRight.isNothing && !cellBelowRight.isWall)
                  && (cellAboveLeft == null || cellAboveLeft.isNothing || cellAboveLeft.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Front-Left-Turn-Down-1";
          } else if (
              (cellAbove == null || cellAbove.isNothing || cellAbove.isWall)
                  && (cellBelow != null && cellBelow.isWall)
                  && (cellLeft != null && cellLeft.isWall)
                  && (cellRight == null || cellRight.isNothing || cellRight.isWall)
                  && (cellBelowLeft != null && !cellBelowLeft.isNothing && !cellBelowLeft.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Front-Right-Turn-Down-1";
          } else if (
              (cellAbove != null && cellAbove.isWall)
                  && (cellBelow != null && !cellBelow.isWall && !cellBelow.isNothing)
                  && (cellLeft != null && !cellLeft.isWall && !cellLeft.isNothing)
                  && (cellRight != null && cellRight.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Front-Left-Turn-Up-1";
          } else if (
              (cellAbove != null && cellAbove.isWall)
                  && (cellBelow != null && !cellBelow.isWall && !cellBelow.isNothing)
                  && (cellLeft != null && cellLeft.isWall)
                  && (cellRight != null && !cellRight.isWall && !cellRight.isNothing)) {
            spritePath = "Level/Cave/Environment/Wall/Front-Right-Turn-Up-1";
          } else if (
              (cellAbove != null && cellAbove.isWall)
                  && (cellBelow != null && cellBelow.isWall)
                  && (cellLeft == null || cellLeft.isWall || cellLeft.isNothing)
                  && (cellRight != null && !cellRight.isWall && !cellRight.isNothing)) {
            spritePath = "Level/Cave/Environment/Wall/Side-with-Floor-Right-1";
          } else if (
              (cellAbove != null && cellAbove.isWall)
                  && (cellBelow != null && cellBelow.isWall)
                  && (cellLeft != null && !cellLeft.isWall && !cellLeft.isNothing)
                  && (cellRight == null || cellRight.isWall || cellRight.isNothing)) {
            spritePath = "Level/Cave/Environment/Wall/Side-with-Floor-Left-1";
          } else if (
              (cellAbove != null && !cellAbove.isWall && !cellAbove.isNothing)
                  && (cellBelow == null || cellBelow.isNothing || cellBelow.isWall)
                  && (cellLeft != null && cellLeft.isWall)
                  && (cellRight != null && cellRight.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Behind-1";
          } else if (
              (cellAbove != null && cellAbove.isWall)
                  && (cellBelow == null || cellBelow.isNothing || cellBelow.isWall)
                  && (cellLeft == null || cellLeft.isNothing || cellLeft.isWall)
                  && (cellRight != null && cellRight.isWall)
                  && (cellAboveRight != null && !cellAboveRight.isNothing && !cellAboveRight.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Behind-Left-Turn-Up-1";
          } else if (
              (cellAbove != null && cellAbove.isWall)
                  && (cellBelow == null || cellBelow.isNothing || cellBelow.isWall)
                  && (cellLeft != null && cellLeft.isWall)
                  && (cellRight == null || cellRight.isNothing || cellRight.isWall)
                  && (cellAboveLeft != null && !cellAboveLeft.isNothing && !cellAboveLeft.isWall)
                  && (cellBelowRight == null || cellBelowRight.isNothing || cellBelowRight.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Behind-Right-Turn-Up-1";
          } else if (
              (cellAbove != null && !cellAbove.isWall && !cellAbove.isNothing)
                  && (cellBelow != null && cellBelow.isWall)
                  && (cellLeft != null && !cellLeft.isWall && !cellLeft.isNothing)
                  && (cellRight != null && cellRight.isWall)) {
            spritePath = "Level/Cave/Environment/Wall/Behind-Left-Turn-Down-1";
          } else if (
              (cellAbove != null && !cellAbove.isWall && !cellAbove.isNothing)
                  && (cellBelow != null && cellBelow.isWall)
                  && (cellLeft != null && cellLeft.isWall)
                  && (cellRight != null && !cellRight.isWall && !cellRight.isNothing)) {
            spritePath = "Level/Cave/Environment/Wall/Behind-Right-Turn-Down-1";
          }

          if (spritePath != null) {
            map[x][y].sprite = atlas.createSprite(spritePath);
          }
        }
      }
    }
  }

  private int groundNeighbours(int cellX, int cellY) {
    int count = 0;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int nx = cellX + i;
        int ny = cellY + j;

        if (i != 0 || j != 0) {
          if (nx >= 0 && ny >= 0 && nx < geometry.length && ny < geometry[0].length) {
            if (geometry[nx][ny]) {
              count += 1;
            }
          }
        }
      }
    }

    return count;
  }

  private Cell getCellAbove(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX, cellY + 1))) {
      return getCell(cellX, cellY + 1);
    } else {
      return null;
    }
  }

  private Cell getCellRight(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX + 1, cellY))) {
      return getCell(cellX + 1, cellY);
    } else {
      return null;
    }
  }

  private Cell getCellBelow(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX, cellY - 1))) {
      return getCell(cellX, cellY - 1);
    } else {
      return null;
    }
  }

  private Cell getCellLeft(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX - 1, cellY))) {
      return getCell(cellX - 1, cellY);
    } else {
      return null;
    }
  }

  private Cell getCellAboveLeft(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX - 1, cellY + 1))) {
      return getCell(cellX - 1, cellY + 1);
    } else {
      return null;
    }
  }

  private Cell getCellAboveRight(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX + 1, cellY + 1))) {
      return getCell(cellX + 1, cellY + 1);
    } else {
      return null;
    }
  }

  private Cell getCellBelowLeft(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX - 1, cellY - 1))) {
      return getCell(cellX - 1, cellY - 1);
    } else {
      return null;
    }
  }

  private Cell getCellBelowRight(int cellX, int cellY) {
    if (cellExists(new Vector2(cellX + 1, cellY - 1))) {
      return getCell(cellX + 1, cellY - 1);
    } else {
      return null;
    }
  }

  /**
   * Get pathfinding cells.
   *
   * @return 2d array of GridCells
   */
  public GridCell[][] createPathfindingMap() {
    GridCell[][] cells = new GridCell[width][height];

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
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
    float[][] resistanceMap = new float[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
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
    Vector2 oldTarget;

    GridCell[][] cells = new GridCell[width][height];

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        boolean canTarget = cellExists(new Vector2(x, y))
            && !getCell(new Vector2(x, y)).isWall
            && !getCell(new Vector2(x, y)).isNothing
            && !getCell(new Vector2(x, y)).hidden;

        cells[x][y] = new GridCell(x, y, canTarget);
      }
    }

    NavigationGrid<GridCell> grid = new NavigationGrid<>(cells, false);
    AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

    if (target == null) {
      oldTarget = null;
      target = start.cpy().add(end);
    } else {
      oldTarget = target.cpy();
      target = target.add(end);
    }

    targetingPath = finder.findPath(
        (int) start.x, (int) start.y, (int) target.x, (int) target.y, grid
    );

    // TODO: Instead of 5, range should be determined by strength
    if (targetingPath == null || targetingPath.size() > 5) {
      target = oldTarget;

      if (target != null) {
        targetingPath = finder.findPath(
            (int) start.x, (int) start.y, (int) target.x, (int) target.y, grid
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
  public void createLookingPath(Vector2 start, Vector2 end, boolean careAboutThings) {
    Vector2 oldTarget;

    GridCell[][] cells = new GridCell[width][height];

    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        boolean canTarget;

        if (careAboutThings) {
          canTarget = cellExists(new Vector2(x, y))
              && !getCell(new Vector2(x, y)).hidden
              && isWalkable(new Vector2(x, y));
        } else {
          canTarget = cellExists(new Vector2(x, y)) && !getCell(new Vector2(x, y)).hidden;
        }

        cells[x][y] = new GridCell(x, y, canTarget);
      }
    }

    NavigationGrid<GridCell> grid = new NavigationGrid<>(cells, false);
    AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

    if (target == null) {
      oldTarget = null;
      target = start.cpy().add(end);
    } else {
      oldTarget = target.cpy();
      target = target.add(end);
    }

    lookingPath = finder.findPath(
        (int) start.x, (int) start.y, (int) target.x, (int) target.y, grid
    );

    if (lookingPath == null) {
      target = oldTarget;

      if (target != null) {
        lookingPath = finder.findPath(
            (int) start.x, (int) start.y, (int) target.x, (int) target.y, grid
        );
      }
    }
  }

  /**
   * Finding out if a cell exists within the map.
   *
   * @param position Position we're checking
   *
   * @return If it does indeed exist
   */
  public boolean cellExists(Vector2 position) {
    return position.x > 0 && position.x < map.length
        && position.y > 0 && position.y < map[0].length
        && getCell(position) != null;
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
    return map[cellX][cellY];
  }

  public Cell getCell(Vector2 position) {
    return getCell((int) position.x, (int) position.y);
  }

  /**
   * Find player pos.
   *
   * @return Vector2 of player pos
   */
  public Vector2 getPlayerPosition() {
    return main.player.getComponent(PositionComponent.class).pos;
  }

  /**
   * Attempt to get the entity at the given position, returns null if nobody is there.
   *
   * @param position The entity's position
   *
   * @return The entity
   */
  public Entity getEntityAt(Vector2 position) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(
            Family.all(PositionComponent.class).exclude(DecorationComponent.class).get()
        );

    for (Entity entity : entities) {
      if (entity.getComponent(PositionComponent.class).pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get enemy from a location.
   *
   * @param position Where the enemy is
   *
   * @return The enemy
   */
  public Entity getEnemyAt(Vector2 position) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      if (entity.getComponent(PositionComponent.class).pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get item from a location.
   *
   * @param position Where the item is
   *
   * @return The item
   */
  public Entity getItemAt(Vector2 position) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(ItemComponent.class, PositionComponent.class).get());

    for (Entity entity : entities) {
      if (entity.getComponent(PositionComponent.class).pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Returns an open position near the player.
   *
   * @return Player position
   */
  public Vector2 getNearPlayer() {
    return getEmptySpaceNearEntity(getPlayerPosition());
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
   * Returns whether or not the given position is blocked.
   *
   * @param position Position to check
   *
   * @return Is it blocked?
   */
  private boolean isBlocked(Vector2 position) {
    boolean blocked = map[(int) position.x][(int) position.y].isWall
        || map[(int) position.x][(int) position.y].isNothing;

    if (!blocked) {
      ImmutableArray<Entity> entities =
          main.engine.getEntitiesFor(
              Family.all(PositionComponent.class).exclude(DecorationComponent.class).get()
          );

      for (Entity entity : entities) {
        PositionComponent ep = ComponentMappers.position.get(entity);

        if (ep.pos.epsilonEquals(position, 0.00001f)) {
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
   * Check if something is near the player.
   *
   * @param position Starting position
   *
   * @return Whether we're near the player or not
   */
  public boolean isNearPlayer(Vector2 position) {
    Vector2 playerPosition = getPlayerPosition();

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
    Vector2 playerPosition = getPlayerPosition();

    return lightMap[(int) playerPosition.x][(int) playerPosition.y] > 0;
  }

  /**
   * Find a random open cell.
   *
   * @return Random open cell
   */
  public Vector2 getRandomOpenPosition() {
    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.length - 1);
      cellY = MathUtils.random(0, map[cellX].length - 1);
    } while (isBlocked(new Vector2(cellX, cellY)));

    return new Vector2(cellX, cellY);
  }

  /**
   * Find player start.
   *
   * @return Open cell position
   */
  public Vector2 findPlayerStart() {
    Vector2 space = new Vector2();

    search:
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        if (isWalkable(new Vector2(x, y))) {
          int neighbours = countLivingNeighbours(x, y);

          if (neighbours >= 5) {
            space.add(x, y);

            break search;
          }
        }
      }
    }

    return space;
  }

  private int countLivingNeighbours(int cellX, int cellY) {
    int count = 0;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int neighbourX = cellX + i;
        int neighbourY = cellY + j;

        if (i != 0 || j != 0) {
          if (neighbourX < 0 || neighbourY < 0
              || neighbourX >= geometry.length || neighbourY >= geometry[0].length) {
            count += 1;
          } else if (geometry[neighbourX][neighbourY]) {
            count += 1;
          }
        }
      }
    }

    return count;
  }
}
