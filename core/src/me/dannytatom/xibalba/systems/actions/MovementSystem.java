package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.map.Map;
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
    MovementComponent movement = ComponentMappers.movement.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    Map map = main.getMap();

    // If we can move, move
    if (map.isWalkable(movement.pos)) {
      position.pos = movement.pos;
    } else {
      // If we can't, and the entity is the player, figure out what to do instead
      if (entity.getComponent(PlayerComponent.class) != null) {
        Entity thing = map.getEntityAt(movement.pos);

        if (main.entityHelpers.isItem(thing)) {
          if (main.inventoryHelpers.addItem(main.player, thing)) {
            main.log.add("You pick up a " + thing.getComponent(ItemComponent.class).name);
          }

          position.pos = movement.pos;
          attributes.energy -= MovementComponent.COST;
        } else if (main.entityHelpers.isEnemy(thing) && attributes.energy >= MeleeComponent.COST) {
          main.player.add(new MeleeComponent(thing));
        } else if (main.entityHelpers.isExit(thing)) {
          // TODO: Switch maps
        }
      }
    }

    entity.remove(MovementComponent.class);
  }
}
