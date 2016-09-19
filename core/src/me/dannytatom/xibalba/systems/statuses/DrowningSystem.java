package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.DrowningComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class DrowningSystem extends UsesEnergySystem {
  public DrowningSystem() {
    super(Family.all(DrowningComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    DrowningComponent drowning = ComponentMappers.drowning.get(entity);

    if (drowning.instance.shouldRemove(entity)) {
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);
      attributes.vision = attributes.maxVision;

      entity.remove(DrowningComponent.class);
    } else {
      drowning.instance.onTurn(entity);
    }
  }
}
