package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.StatusComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class StatusSystem extends UsesEnergySystem {
  public StatusSystem() {
    super(Family.all(StatusComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    StatusComponent status = ComponentMappers.status.get(entity);

    // When crippled, you get 1 turn for every 2. It only lasts for 5 turns.
    //
    // When crippled is true and the turn counter is 0, entity is free to move
    if (status.crippled) {
      if (status.crippledLifeCounter == 5) {
        status.crippled = false;
        status.crippledLifeCounter = 0;
        status.crippledTurnCounter = 0;
      } else {
        if (status.crippledTurnCounter == 2) {
          status.crippledTurnCounter = 0;
          status.crippledLifeCounter += 1;
        } else {
          status.crippledTurnCounter += 1;
        }
      }
    }
  }
}
