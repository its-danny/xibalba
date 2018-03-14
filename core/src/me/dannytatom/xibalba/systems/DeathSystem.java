package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class DeathSystem extends UsesEnergySystem {
  public DeathSystem() {
    super(Family.all(AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (attributes.health <= 0) {
      PositionComponent position = ComponentMappers.position.get(entity);

      Entity corpse = WorldManager.entityFactory.createCorpse(entity, position.pos);

      WorldManager.world.addEntity(corpse);
      WorldManager.world.removeEntity(entity);
    }
  }
}
