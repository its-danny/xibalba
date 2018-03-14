package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.statuses.EncumberedComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class EncumberedSystem extends UsesEnergySystem {
  public EncumberedSystem() {
    super(Family.all(EncumberedComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    EncumberedComponent encumbered = ComponentMappers.encumbered.get(entity);

    if (encumbered.turnCounter == 2) {
      encumbered.turnCounter = 0;
    } else {
      encumbered.turnCounter += 1;
    }
  }
}
