package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.Cell;
import me.dannytatom.x2600BC.components.MovementComponent;
import me.dannytatom.x2600BC.components.WanderComponent;

public class WanderSystem extends IteratingSystem {
  private Cell[][] map;

  /**
   * AI State for wandering around.
   *
   * @param map the map we're moving on
   */
  public WanderSystem(Cell[][] map) {
    super(Family.all(WanderComponent.class, MovementComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    // TODO: Wander, use MoveAction.COST
  }
}
