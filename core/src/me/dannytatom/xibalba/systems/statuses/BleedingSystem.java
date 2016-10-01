package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class BleedingSystem extends UsesEnergySystem {
  public BleedingSystem() {
    super(Family.all(BleedingComponent.class, AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BleedingComponent bleeding = ComponentMappers.bleeding.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (bleeding.counter == 5) {
      entity.remove(BleedingComponent.class);
    } else {
      WorldManager.entityHelpers.dealDamage(entity, 5);

      bleeding.counter += 1;

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add("You took 5 damage from bleeding");
      } else {
        WorldManager.log.add(attributes.name + " took 5 damage from bleeding");
      }
    }
  }
}
