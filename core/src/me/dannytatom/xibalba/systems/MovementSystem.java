package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.MovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.Actions;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MovementSystem extends IteratingSystem {
  private final Map map;

  /**
   * System to control movement of entities.
   *
   * @param map the map we're moving on
   */
  public MovementSystem(Map map) {
    super(Family.all(PositionComponent.class, MovementComponent.class, AttributesComponent.class).get());

    this.map = map;
  }

  /**
   * If the entities have a move action in queue,
   * and can move where they're wanting to,
   * move 'em.
   *
   * @param entity    The entity to process
   * @param deltaTime Time since last frame
   */
  public void processEntity(Entity entity, float deltaTime) {
    PositionComponent position = ComponentMappers.position.get(entity);
    MovementComponent movement = ComponentMappers.movement.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (movement.pos != null && attributes.energy >= Actions.MOVE) {
      if (map.isWalkable((int) movement.pos.x, (int) movement.pos.y)) {
        position.pos = movement.pos;
      }

      attributes.energy -= Actions.MOVE;
    }
  }
}
