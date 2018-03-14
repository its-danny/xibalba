package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.PoisonedComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class PoisonedSystem extends UsesEnergySystem {
  public PoisonedSystem() {
    super(Family.all(PoisonedComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PoisonedComponent poisoned = ComponentMappers.poisoned.get(entity);

    if (poisoned.counter == poisoned.life) {
      entity.remove(PoisonedComponent.class);
    } else {
      WorldManager.entityHelpers.takeDamage(entity, poisoned.damage);

      if (WorldManager.entityHelpers.canSee(WorldManager.player, entity)) {
        boolean isPlayer = ComponentMappers.player.has(entity);
        AttributesComponent attributes = ComponentMappers.attributes.get(entity);

        WorldManager.log.add(
            "effects.poisoned.tookDamage", (isPlayer ? "You" : attributes.name), poisoned.damage
        );

        if (attributes.health <= 0) {
          WorldManager.log.add("effects.poisoned.died", (isPlayer ? "You" : attributes.name));
        }
      }

      poisoned.counter += 1;
    }
  }
}
