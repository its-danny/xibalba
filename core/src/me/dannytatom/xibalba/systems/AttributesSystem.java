package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class AttributesSystem extends UsesEnergySystem {
  public AttributesSystem() {
    super(Family.all(AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    attributes.energy += attributes.speed;

    if (ComponentMappers.player.has(entity)) {
      // Decrease divineFavor over time.
      if (attributes.divineFavor > 0) {
        attributes.divineFavor -= attributes.divineFavor * 0.005;
      }
    }
  }
}
