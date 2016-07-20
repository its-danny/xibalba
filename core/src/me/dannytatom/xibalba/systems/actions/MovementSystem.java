package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

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
      move(entity, position, movement, visual);

      attributes.energy -= MovementComponent.COST;
    } else {
      // If we can't, and the entity is the player, figure out what to do instead
      if (ComponentMappers.player.has(entity)) {
        Entity thing = WorldManager.entityHelpers.getEntityAt(movement.pos);

        if (WorldManager.entityHelpers.isItem(thing)) {
          WorldManager.inventoryHelpers.addItem(WorldManager.player, thing);

          move(entity, position, movement, visual);

          attributes.energy -= MovementComponent.COST;
        } else if (WorldManager.entityHelpers.isEnemy(thing)) {
          WorldManager.combatHelpers.preparePlayerForMelee(thing, "body");
        } else if (WorldManager.entityHelpers.isExit(thing)) {
          WorldManager.state = WorldManager.State.GOING_DOWN;
          attributes.energy -= MovementComponent.COST;
        } else if (WorldManager.entityHelpers.isEntrance(thing)) {
          WorldManager.state = WorldManager.State.GOING_UP;
          attributes.energy -= MovementComponent.COST;
        }
      }
    }

    entity.remove(MovementComponent.class);
  }

  private void move(Entity entity, PositionComponent position,
                    MovementComponent movement, VisualComponent visual) {
    WorldManager.entityHelpers.updatePosition(
        entity, movement.pos
    );

    if (ComponentMappers.player.has(entity)) {
      WorldManager.world.updateLighting(movement.pos.x, movement.pos.y);
    }
  }
}
