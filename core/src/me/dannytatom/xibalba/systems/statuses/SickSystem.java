package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.SickComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class SickSystem extends UsesEnergySystem {
  public SickSystem() {
    super(Family.all(SickComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    SickComponent sick = ComponentMappers.sick.get(entity);

    if (sick.counter == sick.life) {
      entity.remove(SickComponent.class);
    } else {
      if (MathUtils.random() > 0.5) {
        WorldManager.entityHelpers.vomit(entity, sick.damage);

        if (WorldManager.entityHelpers.canSee(WorldManager.player, entity)) {
          boolean isPlayer = ComponentMappers.player.has(entity);
          AttributesComponent attributes = ComponentMappers.attributes.get(entity);

          WorldManager.log.add(
              "effects.sick.tookDamage", (isPlayer ? "You" : attributes.name), sick.damage
          );

          if (attributes.health <= 0) {
            WorldManager.log.add("effects.sick.died", (isPlayer ? "You" : attributes.name));
          }
        }

        sick.counter += 1;
      }
    }
  }
}
