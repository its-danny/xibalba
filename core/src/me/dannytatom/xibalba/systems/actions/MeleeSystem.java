package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class MeleeSystem extends UsesEnergySystem {
  public MeleeSystem() {
    super(Family.all(MeleeComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MeleeComponent melee = ComponentMappers.melee.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (melee.target != null && !entity.isScheduledForRemoval()) {
      WorldManager.combatHelpers.melee(entity, melee.target, melee.bodyPart, melee.isFocused);
    }

    attributes.energy -= MeleeComponent.COST;
    entity.remove(MeleeComponent.class);
  }
}
