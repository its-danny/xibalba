package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.Objects;

public class RangeSystem extends UsesEnergySystem {
  public RangeSystem() {
    super(Family.all(RangeComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    RangeComponent range = ComponentMappers.range.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (range.position != null && !entity.isScheduledForRemoval()) {
      Entity target = WorldManager.entityHelpers.getEnemyAt(range.position);

      if (target != null) {
        WorldManager.combatHelpers.range(entity, target, range.bodyPart, range.item, range.skill);
      }

      if (Objects.equals(range.skill, "throwing")) {
        ComponentMappers.item.get(range.item).throwing = false;
        WorldManager.inventoryHelpers.dropItem(entity, range.item, range.position);
      } else {
        if (target == null) {
          WorldManager.inventoryHelpers.dropItem(entity, range.item, range.position);
        } else {
          WorldManager.inventoryHelpers.removeItem(entity, range.item);
        }
      }
    }

    attributes.energy -= RangeComponent.COST;
    entity.remove(RangeComponent.class);
  }
}
