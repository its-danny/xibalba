package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.components.AttributesComponent;
import me.dannytatom.x2600BC.components.MovementComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.map.Map;
import me.dannytatom.x2600BC.utils.ComponentMappers;

public class MovementSystem extends IteratingSystem {
  private Map map;

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

    if (movement.position != null && attributes.energy >= 100) {
      if (map.isWalkable((int) movement.position.x, (int) movement.position.y)) {
        position.x = (int) movement.position.x;
        position.y = (int) movement.position.y;
      }

      attributes.energy -= 100;
      movement.position = null;
    }
  }
}
