package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MovementSystem extends ActionSystem {
  private final Main main;

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

    if (movement.pos != null && main.getCurrentMap().isWalkable(movement.pos)) {
      position.pos = movement.pos;
      attributes.energy -= MovementComponent.COST;
    }

    entity.remove(MovementComponent.class);
  }
}
