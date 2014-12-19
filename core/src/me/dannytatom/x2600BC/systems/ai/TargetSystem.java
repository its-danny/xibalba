package me.dannytatom.x2600BC.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.components.MovementComponent;
import me.dannytatom.x2600BC.components.ai.TargetComponent;
import me.dannytatom.x2600BC.map.Map;

public class TargetSystem extends IteratingSystem {
  private Map map;

  /**
   * AI State for fleeing a target.
   *
   * @param map the map we're moving on
   */
  public TargetSystem(Map map) {
    super(Family.all(TargetComponent.class, MovementComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    // TODO: Flee
  }
}
