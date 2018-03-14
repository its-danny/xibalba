package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class BrainSystem extends UsesEnergySystem {
  public BrainSystem() {
    super(Family.all(BrainComponent.class, AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BrainComponent brain = ComponentMappers.brain.get(entity);

    brain.stateMachine.update();

    if (brain.fear > 0) {
      brain.fear -= 0.01;
    } else if (brain.fear < 0) {
      brain.fear = 0;
    }

    if (brain.path != null && brain.path.size > 0) {
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);

      if (attributes.energy >= MovementComponent.COST) {
        Vector2 cell = brain.path.get(0);

        entity.add(new MovementComponent(cell));
        brain.path.removeIndex(0);
      }
    }
  }
}
