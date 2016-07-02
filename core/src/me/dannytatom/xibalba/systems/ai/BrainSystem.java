package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.ai.AttackComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.Comparator;

public class BrainSystem extends SortedIteratingSystem {
  private final Main main;

  /**
   * Handles AI states.
   *
   * @param main Instance of Main class
   */
  public BrainSystem(Main main) {
    super(Family.all(BrainComponent.class).get(), new EnergyComparator());

    this.main = main;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BrainComponent brain = ComponentMappers.brain.get(entity);
    PositionComponent position = ComponentMappers.position.get(entity);

    if (position.map == main.world.currentMapIndex) {
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
    } else {
      if (brain.state != BrainComponent.State.WAITING) {
        brain.state = BrainComponent.State.WAITING;

        entity.remove(WanderComponent.class);
        entity.remove(TargetComponent.class);
        entity.remove(AttackComponent.class);
      }
    }
  }

  private void handleWaiting(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (main.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (main.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          switchToTarget(entity, main.mapHelpers.getOpenSpaceNearPlayer());
        } else {
          switchToWander(entity);
        }
      }
    }
  }

  private void handleWander(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (main.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      } else {
        switchToWaiting(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (main.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          switchToTarget(entity, main.mapHelpers.getOpenSpaceNearPlayer());
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
    Vector2 playerPosition = ComponentMappers.position.get(main.player).pos;

    if (main.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        switchToAttack(entity);
      } else {
        switchToWaiting(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (main.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          if (playerPosition != target.pos) {
            switchToTarget(entity, main.mapHelpers.getOpenSpaceNearPlayer());
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

    if (main.entityHelpers.isNearPlayer(entity)) {
      if (attributes.energy >= MeleeComponent.COST) {
        entity.add(new MeleeComponent(main.player, "body"));
      } else if (attributes.energy >= MovementComponent.COST) {
        switchToWander(entity);
      }
    } else {
      if (attributes.energy >= MovementComponent.COST) {
        if (main.entityHelpers.canSeePlayer(entity, attributes.vision)) {
          switchToTarget(entity, main.mapHelpers.getOpenSpaceNearPlayer());
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

  private static class EnergyComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity e1, Entity e2) {
      AttributesComponent a1 = ComponentMappers.attributes.get(e1);
      AttributesComponent a2 = ComponentMappers.attributes.get(e2);

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
