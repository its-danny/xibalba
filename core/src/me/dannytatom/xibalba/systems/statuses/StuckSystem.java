package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.statuses.StuckComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class StuckSystem extends UsesEnergySystem {
  public StuckSystem() {
    super(Family.all(StuckComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    StuckComponent stuck = ComponentMappers.stuck.get(entity);

    if (stuck.counter == stuck.life) {
      entity.remove(StuckComponent.class);
    } else {
      stuck.counter += 1;
    }
  }
}