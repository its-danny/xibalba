package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.Cell;
import me.dannytatom.x2600BC.components.FleeComponent;
import me.dannytatom.x2600BC.components.MovementComponent;

public class FleeSystem extends IteratingSystem {
  Cell[][] map;

  /**
   * AI State for fleeing a target.
   *
   * @param map the map we're moving on
   */
  public FleeSystem(Cell[][] map) {
    super(Family.all(FleeComponent.class, MovementComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    // TODO: Flee
  }
}
