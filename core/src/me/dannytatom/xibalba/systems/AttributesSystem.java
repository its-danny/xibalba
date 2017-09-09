package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.GodComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class AttributesSystem extends UsesEnergySystem {
  public AttributesSystem() {
    super(Family.all(AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    attributes.energy += attributes.speed;
    
    if (ComponentMappers.player.has(entity)) {
      GodComponent god = ComponentMappers.god.get(WorldManager.god);

      if (attributes.divineFavor <= 0) {
        god.hasWrath = true;
      } else if (attributes.divineFavor >= 25) {
        god.hasWrath = false;
      }
    }
  }
}
