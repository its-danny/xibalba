package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import java.util.ArrayList;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class MovementSystem extends UsesEnergySystem {
  public MovementSystem() {
    super(Family.all(MovementComponent.class).get());
  }

  /**
   * If the entities have a move action in queue, and can move where they're wanting to, move 'em.
   *
   * @param entity    The entity to process
   * @param deltaTime Time since last frame
   */
  public void processEntity(Entity entity, float deltaTime) {
    PositionComponent position = ComponentMappers.position.get(entity);
    VisualComponent visual = ComponentMappers.visual.get(entity);
    MovementComponent movement = ComponentMappers.movement.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (movement.pos.x < position.pos.x) {
      visual.sprite.setFlip(true, false);
    } else {
      visual.sprite.setFlip(false, false);
    }

    // If we can move, move
    if (!WorldManager.mapHelpers.isBlocked(WorldManager.world.currentMapIndex, movement.pos)) {
      move(entity, movement);

      attributes.energy -= MovementComponent.COST;
    } else {
      // If we can't, and the entity is the player, figure out what to do instead
      if (ComponentMappers.player.has(entity)) {
        Entity thing = WorldManager.mapHelpers.getEntityAt(movement.pos);

        if (thing == null) {
          return;
        }

        if (ComponentMappers.item.has(thing)) {
          ArrayList<Entity> items = WorldManager.mapHelpers.getEntitiesAt(movement.pos);

          for (Entity item : items) {
            WorldManager.itemHelpers.addToInventory(WorldManager.player, item, true);
          }

          move(entity, movement);

          attributes.energy -= MovementComponent.COST;
        } else if (ComponentMappers.enemy.has(thing)) {
          WorldManager.combatHelpers.preparePlayerForMelee(thing, "body", false);
        } else if (ComponentMappers.exit.has(thing)) {
          WorldManager.state = WorldManager.State.GOING_DOWN;
          attributes.energy -= MovementComponent.COST;
        } else if (ComponentMappers.entrance.has(thing)) {
          WorldManager.state = WorldManager.State.GOING_UP;
          attributes.energy -= MovementComponent.COST;
        }
      }
    }

    entity.remove(MovementComponent.class);
  }

  private void move(Entity entity, MovementComponent movement) {
    WorldManager.entityHelpers.updatePosition(
        entity, movement.pos.x, movement.pos.y
    );

    WorldManager.entityHelpers.updateSenses(entity);

    if (ComponentMappers.player.has(entity)) {
      WorldManager.world.getCurrentMap().dijkstra.updateTargetPlayerLand();

      PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);

      if (playerDetails.lastHitEntity != null
          && !WorldManager.entityHelpers.canSee(WorldManager.player, playerDetails.lastHitEntity)) {
        playerDetails.lastHitEntity = null;
      }
    }
  }
}
