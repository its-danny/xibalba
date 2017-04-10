package me.dannytatom.xibalba.brain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public enum Brain implements State<Entity> {
  SLEEP() {
    @Override
    public void enter(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      brain.path = null;
    }

    @Override
    public void update(Entity entity) {
      if (Brain.shouldWakeUp()) {
        BrainComponent brain = ComponentMappers.brain.get(entity);

        brain.stateMachine.changeState(WANDER);
      }
    }
  },

  WANDER() {
    @Override
    public void enter(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      brain.path = null;
    }

    @Override
    public void update(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      if (shouldTarget(entity, WorldManager.player)) {
        brain.target = WorldManager.player;
        brain.stateMachine.changeState(TARGET);

        return;
      }

      if (shouldSleep()) {
        brain.stateMachine.changeState(SLEEP);

        return;
      }

      if (brain.path == null || brain.path.size == 0) {
        PositionComponent position = ComponentMappers.position.get(entity);

        brain.path = WorldManager.world.getCurrentMap().dijkstra.findWanderPath(position.pos);
      }
    }
  },

  TARGET() {
    @Override
    public void enter(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      brain.path = null;
    }

    @Override
    public void update(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      if (Brain.shouldAttack(entity, brain.target)) {
        brain.stateMachine.changeState(ATTACK);

        return;
      }

      if (shouldWander(entity)) {
        brain.stateMachine.changeState(WANDER);

        return;
      }

      PositionComponent position = ComponentMappers.position.get(entity);
      PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

      boolean makeNewPath = brain.path == null || brain.path.size == 0
          || !playerPosition.pos.epsilonEquals(brain.path.get(brain.path.size - 1), 0.00001f);

      if (makeNewPath) {
        brain.path = WorldManager.world.getCurrentMap().dijkstra.findPlayerPositionPath(
            position.pos
        );
      }
    }
  },

  ATTACK() {
    @Override
    public void enter(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      brain.path = null;
    }

    @Override
    public void update(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      if (Brain.shouldTarget(entity, brain.target)) {
        brain.stateMachine.changeState(TARGET);

        return;
      }

      if (Brain.shouldWander(entity)) {
        brain.stateMachine.changeState(WANDER);

        return;
      }

      entity.add(new MeleeComponent(brain.target, "body", false));
    }
  };

  @Override
  public void enter(Entity entity) {

  }

  @Override
  public void update(Entity entity) {

  }

  @Override
  public void exit(Entity entity) {

  }

  @Override
  public boolean onMessage(Entity entity, Telegram telegram) {
    return false;
  }

  private static boolean shouldSleep() {
    return MathUtils.random() > 0.75f;
  }

  private static boolean shouldWakeUp() {
    return MathUtils.random() > 0.5f;
  }

  private static boolean shouldWander(Entity entity) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    return !WorldManager.entityHelpers.canSense(entity, brain.target);
  }

  private static boolean shouldTarget(Entity entity, Entity target) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    return WorldManager.entityHelpers.canSense(entity, target)
        && !brain.stateMachine.isInState(ATTACK)
        && brain.hostility > brain.fear;
  }

  private static boolean shouldAttack(Entity entity, Entity target) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    return WorldManager.entityHelpers.isNear(entity, target)
        && brain.hostility > brain.fear;
  }
}
