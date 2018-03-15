package me.dannytatom.xibalba.brain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;

import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.components.GodComponent;
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

        if (brain.dna.contains(BrainComponent.Dna.AQUATIC, false)) {
          brain.path
              = WorldManager.world.getCurrentMap().dijkstra.findWanderWaterPath(position.pos);
        } else if (brain.dna.contains(BrainComponent.Dna.TERRESTRIAL, false)) {
          brain.path
              = WorldManager.world.getCurrentMap().dijkstra.findWanderLandPath(position.pos);
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

      // If they've already finished the path or the player has changed positions,
      // create a new path.

      boolean makeNewPath = brain.path == null || brain.path.size == 0
          || !playerPosition.pos.epsilonEquals(brain.path.get(brain.path.size - 1), 0.00001f);

      if (makeNewPath) {
        if (brain.dna.contains(BrainComponent.Dna.AQUATIC, false)) {
          brain.path = WorldManager.world.getCurrentMap().dijkstra.findTargetPlayerWaterPath(
              position.pos
          );
        } else if (brain.dna.contains(BrainComponent.Dna.TERRESTRIAL, false)) {
          brain.path = WorldManager.world.getCurrentMap().dijkstra.findTargetPlayerLandPath(
              position.pos
          );
        }
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

      if (ComponentMappers.charmed.has(entity)) {
        return;
      }

      entity.add(new MeleeComponent(brain.target, "body", false));
    }
  };

  private static boolean shouldSleep() {
    return MathUtils.random() > 0.75f;
  }

  private static boolean shouldWakeUp() {
    return MathUtils.random() > 0.5f;
  }

  /**
   * Whether or not an entity should switch to the WANDER state.
   *
   * <p>- They don't sense their target
   * - The player is dead
   * - They're not afraid
   *
   * @param entity Who got the brain
   * @return Whether or not they should wander
   */
  private static boolean shouldWander(Entity entity) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    return !WorldManager.entityHelpers.canSense(entity, brain.target)
        || WorldManager.state == WorldManager.State.DEAD
        || brain.fear > brain.fearThreshold;
  }

  /**
   * Whether or not an entity should switch to the TARGET state.
   *
   * <p>- They sense their target
   * - They're not already near their target
   * - They're not afraid
   * - They're aggressive
   * - The player is not dead
   *
   * @param entity Who got the brain
   * @param target Who they're targeting
   * @return Whether or not they should target
   */
  private static boolean shouldTarget(Entity entity, Entity target) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    return WorldManager.entityHelpers.canSense(entity, target)
        && !WorldManager.entityHelpers.isNear(entity, target)
        && brain.fear <= brain.fearThreshold
        && isAggressive(entity)
        && WorldManager.state != WorldManager.State.DEAD;
  }

  /**
   * Whether or not an entity should switch to the ATTACK state.
   *
   * <p>- They sense their target
   * - They're near their target
   * - They're not afraid
   * - They're aggressive
   * - The player is not dead
   *
   * @param entity Who got the brain
   * @param target Who they're targeting
   * @return Whether or not they should attack
   */
  private static boolean shouldAttack(Entity entity, Entity target) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    return WorldManager.entityHelpers.canSense(entity, target)
        && WorldManager.entityHelpers.isNear(entity, target)
        && brain.fear <= brain.fearThreshold
        && isAggressive(entity)
        && WorldManager.state != WorldManager.State.DEAD;
  }

  private static boolean isAggressive(Entity entity) {
    BrainComponent brain = ComponentMappers.brain.get(entity);
    GodComponent god = ComponentMappers.god.get(WorldManager.god);

    return MathUtils.random() < brain.aggression
        || (god.hasWrath && god.wrath.contains("Animals more aggressive"));
  }

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
