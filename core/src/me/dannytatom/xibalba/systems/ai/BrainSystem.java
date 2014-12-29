package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.ai.AttackComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.EntityHelpers;

public class BrainSystem extends IteratingSystem {
  private final EntityHelpers entityHelpers;
  private final Map map;

  /**
   * THA CONTROL CENTER. Handles AI states.
   *
   * @param map The map we're currently on
   */
  public BrainSystem(EntityHelpers entityHelpers, Map map) {
    super(Family.all(BrainComponent.class).get());

    this.entityHelpers = entityHelpers;
    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    switch (brain.state) {
      case WAITING:
        handleWaiting(entity);
        break;
      case WANDERING:
        handleWander(entity);
        break;
      case TARGETING:
        handleTarget(entity);
        break;
      case ATTACKING:
        handleAttack(entity);
        break;
      default:
    }
  }

  private void handleWaiting(Entity entity) {
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (map.isNearPlayer(position.pos)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (map.canSeePlayer(position.pos, attributes.vision)) {
          switchToTarget(entity, map.getNearPlayer());
        } else {
          switchToWander(entity);
        }
      }
    }
  }

  private void handleWander(Entity entity) {
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (map.isNearPlayer(position.pos)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      } else {
        switchToWaiting(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (map.canSeePlayer(position.pos, attributes.vision)) {
          switchToTarget(entity, map.getNearPlayer());
        } else {
          switchToWander(entity);
        }
      } else {
        switchToWaiting(entity);
      }
    }
  }

  private void handleTarget(Entity entity) {
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    TargetComponent target = ComponentMappers.target.get(entity);
    Vector2 playerPosition = map.getPlayerPosition();

    if (map.isNearPlayer(position.pos)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else {
        switchToWaiting(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (map.canSeePlayer(position.pos, attributes.vision)) {
          if (playerPosition != target.pos) {
            switchToTarget(entity, map.getNearPlayer());
          }
        } else {
          switchToWander(entity);
        }
      } else {
        switchToWaiting(entity);
      }
    }
  }

  private void handleAttack(Entity entity) {
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (map.isNearPlayer(position.pos)) {
      if (attributes.energy >= MeleeComponent.COST) {
        entity.add(new MeleeComponent(entityHelpers.getPlayer()));
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (map.canSeePlayer(position.pos, attributes.vision)) {
          switchToTarget(entity, map.getNearPlayer());
        } else {
          switchToWander(entity);
        }
      } else {
        switchToWaiting(entity);
      }
    }
  }

  private void switchToWaiting(Entity entity) {
    entity.getComponent(BrainComponent.class).state = BrainComponent.State.WAITING;

    entity.remove(WanderComponent.class);
    entity.remove(TargetComponent.class);
    entity.remove(AttackComponent.class);
  }

  private void switchToWander(Entity entity) {
    BrainComponent brain = entity.getComponent(BrainComponent.class);

    if (brain.state == BrainComponent.State.TARGETING) {
      brain.path = null;
    }

    brain.state = BrainComponent.State.WANDERING;

    entity.remove(TargetComponent.class);
    entity.remove(AttackComponent.class);
    entity.add(new WanderComponent());
  }

  private void switchToTarget(Entity entity, Vector2 target) {
    BrainComponent brain = entity.getComponent(BrainComponent.class);

    if (brain.state == BrainComponent.State.WANDERING) {
      brain.path = null;
    }

    brain.state = BrainComponent.State.TARGETING;

    entity.remove(WanderComponent.class);
    entity.remove(AttackComponent.class);
    entity.add(new TargetComponent(target));
  }

  private void switchToAttack(Entity entity) {
    BrainComponent brain = entity.getComponent(BrainComponent.class);

    brain.path = null;
    brain.state = BrainComponent.State.ATTACKING;

    entity.remove(WanderComponent.class);
    entity.remove(TargetComponent.class);
    entity.add(new AttackComponent());
  }
}
