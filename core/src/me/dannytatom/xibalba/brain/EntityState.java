package me.dannytatom.xibalba.brain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.ArrayList;
import java.util.List;

public enum EntityState implements State<Entity> {
  IDLE() {
    @Override
    public void update(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      if (canMove(entity)) {
        brain.stateMachine.changeState(WANDER);
      }
    }
  },

  WANDER() {
    @Override
    public void enter(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      brain.target = null;
    }

    @Override
    public void update(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      boolean isNearPlayer = WorldManager.entityHelpers.isNearPlayer(entity);
      boolean canSensePlayer = WorldManager.entityHelpers.canSensePlayer(entity);
      boolean playerIsAlive = WorldManager.entityHelpers.isPlayerAlive();

      if (isNearPlayer && playerIsAlive && !shouldFlee(entity)) {
        if (canAttack(entity)) {
          brain.target = WorldManager.player;
          brain.stateMachine.changeState(ATTACK);
        }
      } else if (canSensePlayer && playerIsAlive && isAggressive(entity) && !shouldFlee(entity)) {
        if (canMove(entity)) {
          brain.target = WorldManager.player;
          brain.stateMachine.changeState(TARGET);
        }
      } else {
        if (canMove(entity)) {
          updatePath(entity);
        }
      }
    }

    @Override
    public void exit(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      brain.path = null;
    }
  },

  TARGET() {
    @Override
    public void update(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      boolean isNearPlayer = WorldManager.entityHelpers.isNearPlayer(entity);
      boolean canSensePlayer = WorldManager.entityHelpers.canSensePlayer(entity);
      boolean playerIsAlive = WorldManager.entityHelpers.isPlayerAlive();

      if (isNearPlayer && playerIsAlive && canAttack(entity)) {
        brain.stateMachine.changeState(ATTACK);
      } else if (canSensePlayer && playerIsAlive) {
        if (canMove(entity)) {
          updatePath(entity);
        }
      } else {
        if (canMove(entity)) {
          brain.stateMachine.changeState(WANDER);
        }
      }
    }

    @Override
    public void exit(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      brain.path = null;
    }
  },

  ATTACK() {
    @Override
    public void update(Entity entity) {
      BrainComponent brain = ComponentMappers.brain.get(entity);

      boolean isNearPlayer = WorldManager.entityHelpers.isNearPlayer(entity);
      boolean canSensePlayer = WorldManager.entityHelpers.canSensePlayer(entity);
      boolean playerIsAlive = WorldManager.entityHelpers.isPlayerAlive();

      if (isNearPlayer && playerIsAlive && !shouldFlee(entity) && canAttack(entity)) {
        entity.add(new MeleeComponent(brain.target, "body"));
      } else if (canSensePlayer && playerIsAlive && isAggressive(entity) && !shouldFlee(entity)) {
        if (canMove(entity)) {
          brain.target = WorldManager.player;
          brain.stateMachine.changeState(TARGET);
        }
      } else {
        if (canMove(entity)) {
          brain.stateMachine.changeState(WANDER);
        }
      }
    }
  };

  @Override
  public void enter(Entity entity) {

  }

  @Override
  public void exit(Entity entity) {

  }

  @Override
  public boolean onMessage(Entity entity, Telegram telegram) {
    return false;
  }

  void updatePath(Entity entity) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    if (brain.path == null || brain.path.isEmpty()) {
      NavigationGrid<GridCell> grid =
          new NavigationGrid<>(WorldManager.mapHelpers.createPathfindingMap(), false);
      AStarGridFinder<GridCell> finder = new AStarGridFinder<>(GridCell.class);

      PositionComponent position = ComponentMappers.position.get(entity);

      if (brain.target == null) {
        do {
          Vector2 randomPosition = WorldManager.mapHelpers.getRandomOpenPosition();

          brain.path = finder.findPath((int) position.pos.x, (int) position.pos.y,
              (int) randomPosition.x, (int) randomPosition.y, grid);
        }
        while (brain.path == null);
      } else {
        Vector2 targetPosition = WorldManager.mapHelpers.getOpenSpaceNearEntity(brain.target);

        brain.path = finder.findPath((int) position.pos.x, (int) position.pos.y,
            (int) targetPosition.x, (int) targetPosition.y, grid);
      }
    }

    if (brain.path != null) {
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
  }

  boolean canMove(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    return !WorldManager.entityHelpers.shouldSkipTurn(entity)
        && attributes.energy >= MovementComponent.COST;
  }

  boolean canAttack(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    return !WorldManager.entityHelpers.shouldSkipTurn(entity)
        && attributes.energy >= MeleeComponent.COST;
  }

  boolean shouldFlee(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    return isSafe(entity) && attributes.health < (attributes.maxHealth / 2);
  }

  boolean isAggressive(Entity entity) {
    return ComponentMappers.brain.get(entity).personalities.contains(BrainComponent.Personality.AGGRESSIVE, true);
  }

  boolean travelsInPacks(Entity entity) {
    return ComponentMappers.brain.get(entity).personalities.contains(BrainComponent.Personality.PACKS, true);
  }

  boolean travelsSolo(Entity entity) {
    return ComponentMappers.brain.get(entity).personalities.contains(BrainComponent.Personality.SOLO, true);
  }

  boolean isSafe(Entity entity) {
    return ComponentMappers.brain.get(entity).personalities.contains(BrainComponent.Personality.SAFE, true);
  }

  boolean isStealthy(Entity entity) {
    return ComponentMappers.brain.get(entity).personalities.contains(BrainComponent.Personality.STEALTHY, true);
  }

  boolean flies(Entity entity) {
    return ComponentMappers.brain.get(entity).personalities.contains(BrainComponent.Personality.FLYING, true);
  }

  boolean isAquatic(Entity entity) {
    return ComponentMappers.brain.get(entity).personalities.contains(BrainComponent.Personality.AQUATIC, true);
  }
}
