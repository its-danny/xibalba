package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

import org.xguzm.pathfinding.grid.GridCell;

public class MouseMovementSystem extends EntitySystem {
  private ImmutableArray<Entity> entities;

  public MouseMovementSystem() {

  }

  /**
   * Does this really need a comment? I GUESS SO.
   *
   * @param engine Ashley engine
   */
  public void addedToEngine(Engine engine) {
    entities = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, MouseMovementComponent.class).get()
    );
  }

  /**
   * Get next step in moving path, add a movement component with that position, remove step.
   *
   * @param deltaTime Time between now and previous frame
   */
  public void update(float deltaTime) {
    for (Entity entity : entities) {
      PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);
      AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);

      // Remove mouse movement component once path is empty
      if (playerDetails.path == null || playerDetails.path.isEmpty()) {
        attributes.energy -= MovementComponent.COST;

        entity.remove(MouseMovementComponent.class);
        WorldManager.state = WorldManager.State.PLAYING;
      } else {
        if (attributes.energy >= MovementComponent.COST) {
          // Start walking
          GridCell cell = playerDetails.path.get(0);

          entity.add(new MovementComponent(new Vector2(cell.getX(), cell.getY())));

          List<GridCell> newPath = new ArrayList<>(playerDetails.path);
          newPath.remove(cell);

          playerDetails.path = newPath;
        }
      }
    }
  }
}
