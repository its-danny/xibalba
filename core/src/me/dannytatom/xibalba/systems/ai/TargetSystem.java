package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.MovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.ArrayList;
import java.util.List;

public class TargetSystem extends IteratingSystem {
  private final Map map;

  public TargetSystem(Map map) {
    super(Family.all(TargetComponent.class, PositionComponent.class, MovementComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MovementComponent movement = ComponentMappers.movement.get(entity);

    // Create path to a random open cell on the map if one
    // doesn't already exist.
    if (movement.path == null || movement.path.isEmpty()) {
      TargetComponent target = ComponentMappers.target.get(entity);
      PositionComponent position = ComponentMappers.position.get(entity);

      NavigationGrid<GridCell> grid = new NavigationGrid<>(map.createGridCells());
      AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

      movement.path = finder.findPath((int) position.pos.x, (int) position.pos.y,
          (int) target.pos.x, (int) target.pos.y, grid);
    }

    // If a path to the target could be found, start walking.
    // If it becomes blocked, reset the path.
    //
    // TODO: Instead of checking next cell, check any cell in the path that's in their vision
    if (movement.path != null) {
      GridCell cell = movement.path.get(0);

      if (cell.isWalkable()) {
        movement.pos = new Vector2(cell.getX(), cell.getY());

        List<GridCell> newPath = new ArrayList<>(movement.path);
        newPath.remove(cell);

        movement.path = newPath;
      } else {
        movement.path = null;
      }
    }
  }
}
