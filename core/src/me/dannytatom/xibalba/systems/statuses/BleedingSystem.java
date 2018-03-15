package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class BleedingSystem extends UsesEnergySystem {
  public BleedingSystem() {
    super(Family.all(BleedingComponent.class, AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BleedingComponent bleeding = ComponentMappers.bleeding.get(entity);

    if (bleeding.counter == bleeding.life) {
      entity.remove(BleedingComponent.class);
    } else {
      WorldManager.entityHelpers.takeDamage(entity, bleeding.damage);

      PositionComponent position = ComponentMappers.position.get(entity);
      WorldManager.mapHelpers.makeFloorBloody(position.pos);

      if (WorldManager.entityHelpers.canSee(WorldManager.player, entity)) {
        AttributesComponent attributes = ComponentMappers.attributes.get(entity);

        boolean isPlayer = ComponentMappers.player.has(entity);

        WorldManager.log.add(
            "effects.bleeding.tookDamage", (isPlayer ? "You" : attributes.name),
            bleeding.damage
        );

        if (attributes.health <= 0) {
          WorldManager.log.add("effects.bleeding.died", (isPlayer ? "You" : attributes.name));
        }
      }

      bleeding.counter += 1;
    }
  }
}
