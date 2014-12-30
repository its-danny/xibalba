package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WanderSystem extends SortedIteratingSystem {
  private final Map map;

  /**
   * WanderSystem constructor.
   *
   * @param map The map we're on
   */
  public WanderSystem(Map map) {
    super(Family.all(WanderComponent.class, PositionComponent.class).get(), new EnergyComparator());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    // Create path to a random open cell on the map if one
    // doesn't already exist.
    if (brain.path == null || brain.path.isEmpty()) {
      PositionComponent position = ComponentMappers.position.get(entity);

      NavigationGrid<GridCell> grid = new NavigationGrid<>(map.createGridCells());
      AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

      do {
        Vector2 randomPosition = map.getRandomOpenPosition();
        brain.path = finder.findPath((int) position.pos.x, (int) position.pos.y,
            (int) randomPosition.x, (int) randomPosition.y, grid);
      } while (brain.path == null);
    }

    // Start walking.
    // If the path becomes blocked, reset the path.
    //
    // TODO: Instead of checking next cell, check any cell in the path that's in their vision
    GridCell cell = brain.path.get(0);

    if (cell.isWalkable()) {
      entity.add(new MovementComponent(new Vector2(cell.getX(), cell.getY())));

      List<GridCell> newPath = new ArrayList<>(brain.path);
      newPath.remove(cell);

      brain.path = newPath;
    } else {
      brain.path = null;
    }
  }

  private static class EnergyComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity e1, Entity e2) {
      AttributesComponent a1 = e1.getComponent(AttributesComponent.class);
      AttributesComponent a2 = e2.getComponent(AttributesComponent.class);

      if (a2.energy > a1.energy) {
        return 1;
      } else if (a1.energy > a2.energy) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
