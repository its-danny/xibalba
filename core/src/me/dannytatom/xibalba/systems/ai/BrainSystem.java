package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.ai.AttackComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class BrainSystem extends UsesEnergySystem {
  public BrainSystem() {
    super(Family.all(BrainComponent.class).get());
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
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (WorldManager.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (WorldManager.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          switchToTarget(entity, WorldManager.mapHelpers.getOpenSpaceNearPlayer());
        } else {
          switchToWander(entity);
        }
      }
    }
  }

  private void handleWander(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (WorldManager.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      } else {
        switchToWaiting(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (WorldManager.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          switchToTarget(entity, WorldManager.mapHelpers.getOpenSpaceNearPlayer());
        } else {
          switchToWander(entity);
        }
      } else {
        switchToWaiting(entity);
      }
    }
  }

  private void handleTarget(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    TargetComponent target = ComponentMappers.target.get(entity);
    Vector2 playerPosition = ComponentMappers.position.get(WorldManager.player).pos;

    if (WorldManager.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else {
        switchToWaiting(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (WorldManager.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          if (playerPosition != target.pos) {
            switchToTarget(entity, WorldManager.mapHelpers.getOpenSpaceNearPlayer());
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
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (WorldManager.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        entity.add(new MeleeComponent(WorldManager.player, "body"));
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (WorldManager.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          switchToTarget(entity, WorldManager.mapHelpers.getOpenSpaceNearPlayer());
        } else {
          switchToWander(entity);
        }
      } else {
        switchToWaiting(entity);
      }
    }
  }

  private void switchToWaiting(Entity entity) {
    ComponentMappers.brain.get(entity).state = BrainComponent.State.WAITING;

    entity.remove(WanderComponent.class);
    entity.remove(TargetComponent.class);
    entity.remove(AttackComponent.class);
  }

  private void switchToWander(Entity entity) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    if (WorldManager.entityHelpers.skipTurn(entity)) {
      switchToWaiting(entity);

      return;
    }

    if (brain.state == BrainComponent.State.TARGETING) {
      brain.path = null;
    }

    brain.state = BrainComponent.State.WANDERING;

    entity.remove(TargetComponent.class);
    entity.remove(AttackComponent.class);
    entity.add(new WanderComponent());
  }

  private void switchToTarget(Entity entity, Vector2 target) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    if (WorldManager.entityHelpers.skipTurn(entity)) {
      switchToWaiting(entity);

      return;
    }

    if (brain.state == BrainComponent.State.WANDERING) {
      brain.path = null;
    }

    brain.state = BrainComponent.State.TARGETING;

    entity.remove(WanderComponent.class);
    entity.remove(AttackComponent.class);
    entity.add(new TargetComponent(target));
  }

  private void switchToAttack(Entity entity) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    brain.path = null;
    brain.state = BrainComponent.State.ATTACKING;

    entity.remove(WanderComponent.class);
    entity.remove(TargetComponent.class);
    entity.add(new AttackComponent());
  }
}
