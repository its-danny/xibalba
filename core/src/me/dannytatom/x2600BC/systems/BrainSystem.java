package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.Mappers;
import me.dannytatom.x2600BC.components.*;

public class BrainSystem extends IteratingSystem {
  private Engine engine;

  /**
   * THA CONTROL CENTER. Handles AI states.
   *
   * @param engine Instance of Ashley engine, used to get player
   */
  public BrainSystem(Engine engine) {
    super(Family.all(BrainComponent.class).get());

    this.engine = engine;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    Entity player = engine.getEntitiesFor(Family.one(PlayerComponent.class).get()).first();

    BrainComponent brain = Mappers.brain.get(entity);
    PositionComponent playerPosition = Mappers.position.get(player);

    if (nearPlayer(entity, brain.vision)) {
      entity.remove(WanderComponent.class);
      entity.add(new FleeComponent(playerPosition.x, playerPosition.y));
    } else {
      entity.remove(FleeComponent.class);
      entity.add(new WanderComponent());
    }
  }

  private boolean nearPlayer(Entity entity, int distance) {
    Entity player = engine.getEntitiesFor(Family.one(PlayerComponent.class).get()).first();

    PositionComponent entityPosition = Mappers.position.get(entity);
    PositionComponent playerPosition = Mappers.position.get(player);

    return entityPosition.x < playerPosition.x + distance
        && entityPosition.x > playerPosition.x - distance
        && entityPosition.y < playerPosition.y + distance
        && entityPosition.y > playerPosition.y - distance;
  }
}
