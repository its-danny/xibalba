package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.statuses.CrippledComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class CrippledSystem extends UsesEnergySystem {
  public CrippledSystem() {
    super(Family.all(CrippledComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    CrippledComponent crippled = ComponentMappers.crippled.get(entity);

    if (crippled.counter == crippled.life) {
      entity.remove(CrippledComponent.class);
    } else {
      if (crippled.turnCounter == 2) {
        crippled.turnCounter = 0;
        crippled.counter += 1;
      } else {
        crippled.turnCounter += 1;
      }
    }
  }
}
