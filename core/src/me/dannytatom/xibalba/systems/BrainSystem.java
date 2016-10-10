package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class BrainSystem extends UsesEnergySystem {
  public BrainSystem() {
    super(Family.all(BrainComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    brain.stateMachine.update();
  }
}
