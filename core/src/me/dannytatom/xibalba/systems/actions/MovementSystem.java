package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.screens.PlayScreen;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MovementSystem extends ActionSystem {
  private final Main main;

  /**
   * Handles movement.
   *
   * @param main Instance of Main class
   */
  public MovementSystem(Main main) {
    super(Family.all(MovementComponent.class).get());

    this.main = main;
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
    if (!main.mapHelpers.isBlocked(main.world.currentMapIndex, movement.pos)) {
      position.pos = movement.pos;
    } else {
      // If we can't, and the entity is the player, figure out what to do instead
      if (ComponentMappers.player.get(entity) != null) {
        Entity thing = main.entityHelpers.getEntityAt(movement.pos);

        if (main.entityHelpers.isItem(thing)) {
          main.inventoryHelpers.addItem(main.player, thing);

          position.pos = movement.pos;
        } else if (main.entityHelpers.isEnemy(thing)) {
          main.combatHelpers.preparePlayerForMelee(thing, "body");
        } else if (main.entityHelpers.isExit(thing)) {
          main.world.currentMapIndex += 1;
          main.playScreen = new PlayScreen(main);
          main.setScreen(main.playScreen);

          entity.remove(MouseMovementComponent.class);
          position.map = main.world.currentMapIndex;
        } else if (main.entityHelpers.isEntrance(thing)) {
          main.world.currentMapIndex -= 1;
          main.playScreen = new PlayScreen(main);
          main.setScreen(main.playScreen);

          entity.remove(MouseMovementComponent.class);
          position.map = main.world.currentMapIndex;
        }
      }
    }

    attributes.energy -= MovementComponent.COST;
    entity.remove(MovementComponent.class);
  }
}
