package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.ArrayList;
import java.util.List;

public class WanderSystem extends IteratingSystem {
  private final Map map;

  /**
   * WanderSystem constructor.
   *
   * @param map The map we're on
   */
  public WanderSystem(Map map) {
    super(Family.all(WanderComponent.class, PositionComponent.class,
        MovementComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    WanderComponent wander = ComponentMappers.wander.get(entity);
    MovementComponent movement = ComponentMappers.movement.get(entity);

    // Create path to a random open cell on the map if one
    // doesn't already exist.
    if (wander.path == null || wander.path.isEmpty()) {
      PositionComponent position = ComponentMappers.position.get(entity);

      NavigationGrid<GridCell> grid = new NavigationGrid<>(map.createGridCells());
      AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

      do {
        Vector2 randomPosition = map.getRandomOpenPosition();
        wander.path = finder.findPath((int) position.pos.x, (int) position.pos.y,
            (int) randomPosition.x, (int) randomPosition.y, grid);
      } while (wander.path == null);
    }

    // Start walking.
    // If the path becomes blocked, reset the path.
    //
    // TODO: Instead of checking next cell, check any cell in the path that's in their vision
    GridCell cell = wander.path.get(0);

    if (cell.isWalkable()) {
      movement.pos = new Vector2(cell.getX(), cell.getY());

      List<GridCell> newPath = new ArrayList<>(wander.path);
      newPath.remove(cell);

      wander.path = newPath;
    } else {
      wander.path = null;
    }
  }
}
