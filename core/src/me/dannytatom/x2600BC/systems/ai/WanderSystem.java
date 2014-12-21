package me.dannytatom.x2600BC.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.x2600BC.components.MovementComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.components.ai.WanderComponent;
import me.dannytatom.x2600BC.map.Map;
import me.dannytatom.x2600BC.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.ArrayList;
import java.util.List;

public class WanderSystem extends IteratingSystem {
  private Map map;

  public WanderSystem(Map map) {
    super(Family.all(WanderComponent.class, PositionComponent.class, MovementComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MovementComponent movement = ComponentMappers.movement.get(entity);

    // Create path to a random open cell on the map if one
    // doesn't already exist.
    if (movement.path == null || movement.path.isEmpty()) {
      PositionComponent position = ComponentMappers.position.get(entity);

      NavigationGrid<GridCell> grid = new NavigationGrid<>(map.createGridCells());
      AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);
      Vector2 randomPosition = map.getRandomOpenPosition();

      movement.path = finder.findPath(position.x, position.y, (int) randomPosition.x, (int) randomPosition.y, grid);
    }

    // If a path to the target could be found, start walking.
    // If it becomes blocked, create a new path to somewhere else.
    //
    // TODO: Instead of checking next cell, check any cell in the path that's in their vision
    if (movement.path != null) {
      GridCell cell = movement.path.get(0);

      if (cell.isWalkable()) {
        movement.position = new Vector2(cell.getX(), cell.getY());

        List<GridCell> newPath = new ArrayList<>(movement.path);
        newPath.remove(cell);

        movement.path = newPath;
      } else {
        movement.path = null;
      }
    }
  }
}
