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
      if (MathUtils.random() > .5f) {
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

      if (WorldManager.entityHelpers.canSensePlayer(entity)) {
        if (brain.hostility > brain.fear) {
          brain.target = WorldManager.player;
          brain.stateMachine.changeState(TARGET);
        }
      } else {
        if (MathUtils.random() > .75f) {
          brain.stateMachine.changeState(SLEEP);
        } else {
          if (brain.path == null || brain.path.size == 0) {
            PositionComponent position = ComponentMappers.position.get(entity);

            brain.path = WorldManager.world.getCurrentMap().dijkstra.findWanderPath(position.pos);
          }
        }
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

      if (WorldManager.entityHelpers.isNear(entity, brain.target)) {
        brain.stateMachine.changeState(ATTACK);
      } else if (WorldManager.entityHelpers.canSense(entity, brain.target)) {
        PositionComponent position = ComponentMappers.position.get(entity);
        PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

        boolean makeNewPath = brain.path == null || brain.path.size == 0
            || !playerPosition.pos.epsilonEquals(brain.path.get(brain.path.size - 1), 0.00001f);

        if (makeNewPath) {
          brain.path = WorldManager.world.getCurrentMap().dijkstra.findPlayerPositionPath(
              position.pos
          );
        }
      } else {
        brain.stateMachine.changeState(WANDER);
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

      if (WorldManager.entityHelpers.isNearPlayer(entity)) {
        entity.add(new MeleeComponent(brain.target, "body", false));
      } else if (WorldManager.entityHelpers.canSense(entity, brain.target)) {
        if (brain.hostility > brain.fear) {
          brain.stateMachine.changeState(TARGET);
        }
      } else {
        brain.stateMachine.changeState(WANDER);
      }
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
}
