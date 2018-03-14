package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.statuses.WetComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class WetSystem extends UsesEnergySystem {
  public WetSystem() {
    super(Family.all(WetComponent.class, PositionComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    WetComponent wet = ComponentMappers.wet.get(entity);
    PositionComponent position = ComponentMappers.position.get(entity);

    if (!WorldManager.mapHelpers.getCell(position.pos.x, position.pos.y).isWater()) {
      if (wet.counter == wet.life) {
        entity.remove(WetComponent.class);
      } else {
        WorldManager.mapHelpers.makeFloorWet(position.pos);

        wet.counter += 1;
      }
    }
  }
}
