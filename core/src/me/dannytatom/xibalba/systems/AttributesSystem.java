package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.Comparator;

public class AttributesSystem extends SortedIteratingSystem {
  public AttributesSystem() {
    super(Family.all(AttributesComponent.class).get(), new EnergyComparator());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    attributes.energy += attributes.speed;
  }

  private static class EnergyComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity e1, Entity e2) {
      AttributesComponent a1 = e1.getComponent(AttributesComponent.class);
      AttributesComponent a2 = e2.getComponent(AttributesComponent.class);

      if (a2.energy > a1.energy) {
        return 1;
      } else if (a1.energy > a2.energy) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
