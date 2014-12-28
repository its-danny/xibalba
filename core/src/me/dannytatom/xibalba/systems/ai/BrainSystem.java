package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class BrainSystem extends IteratingSystem {
  private final Map map;

  /**
   * THA CONTROL CENTER. Handles AI states.
   *
   * @param map The map we're currently on
   */
  public BrainSystem(Map map) {
    super(Family.all(BrainComponent.class, AttributesComponent.class,
        PositionComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    if (entity.getComponent(WanderComponent.class) != null) {
      handleWander(entity);
    }

    if (entity.getComponent(TargetComponent.class) != null) {
      handleTarget(entity);
    }
  }

  private void handleWander(Entity entity) {
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    // If he can attack the player, do that
    if (map.isNearPlayer(position.pos, 1) && attributes.energy >= MovementComponent.COST) {
      switchToWander(entity);

      return;
    }

    // If he can move
    if (attributes.energy >= MovementComponent.COST) {
      // If he's near the player, target
      if (map.isNearPlayer(position.pos, attributes.vision)) {
        switchToTarget(entity, map.getNearPlayer());
      }
    }
  }

  private void switchToWander(Entity entity) {
    entity.remove(TargetComponent.class);

    entity.add(new MovementComponent());
    entity.add(new WanderComponent());
  }

  private void handleTarget(Entity entity) {
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    TargetComponent target = ComponentMappers.target.get(entity);
    Vector2 playerPosition = map.getPlayerPosition();

    // If he can attack the player, do that
    if (map.isNearPlayer(position.pos, 1) && attributes.energy >= MovementComponent.COST) {
      switchToWander(entity);

      return;
    }

    // If he can move
    if (attributes.energy >= MovementComponent.COST) {
      // If he's near the player & player moves, retarget
      // Otherwise, go back to wandering
      if (map.isNearPlayer(position.pos, attributes.vision)) {
        if (playerPosition != target.pos) {
          switchToTarget(entity, map.getNearPlayer());
        }
      } else {
        switchToWander(entity);
      }
    }
  }

  private void switchToTarget(Entity entity, Vector2 target) {
    entity.remove(WanderComponent.class);

    entity.add(new MovementComponent());
    entity.add(new TargetComponent(target));
  }
}
