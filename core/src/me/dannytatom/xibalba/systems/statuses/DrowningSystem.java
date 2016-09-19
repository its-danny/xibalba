package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.statuses.DrowningComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class DrowningSystem extends UsesEnergySystem {
  public DrowningSystem() {
    super(
        Family.all(
            DrowningComponent.class, AttributesComponent.class, PositionComponent.class
        ).get()
    );
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent position = ComponentMappers.position.get(entity);

    if (!WorldManager.mapHelpers.getCell(position.pos.x, position.pos.y).isDeepWater()) {
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);
      attributes.vision = attributes.maxVision;
      attributes.oxygen = attributes.maxOxygen;

      entity.remove(DrowningComponent.class);
    } else {
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);
      attributes.health -= 5;

      if (attributes.vision > 1) {
        attributes.vision -= 1;
      }
    }
  }
}
