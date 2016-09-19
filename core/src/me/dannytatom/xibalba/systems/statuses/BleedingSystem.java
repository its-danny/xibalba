package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class BleedingSystem extends UsesEnergySystem {
  public BleedingSystem() {
    super(Family.all(BleedingComponent.class, AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BleedingComponent bleeding = ComponentMappers.bleeding.get(entity);

    if (bleeding.counter == 5) {
      entity.remove(BleedingComponent.class);
    } else {
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);
      attributes.health -= 5;
      bleeding.counter += 1;
    }
  }
}
