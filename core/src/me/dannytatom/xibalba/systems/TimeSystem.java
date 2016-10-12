package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class TimeSystem extends UsesEnergySystem {
  public TimeSystem() {
    super(Family.all(AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    switch (WorldManager.world.getCurrentMap().time.time) {
      case DAWN:
        attributes.vision = attributes.maxVision / 4 * 3;
        break;
      case DAY:
        attributes.vision = attributes.maxVision;
        break;
      case DUSK:
        attributes.vision = attributes.maxVision / 4 * 3;
        break;
      case NIGHT:
        attributes.vision = attributes.maxVision / 2;
        break;
      default:
        break;
    }
  }
}
