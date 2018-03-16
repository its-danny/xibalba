package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.statuses.BurningComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class BurningSystem extends UsesEnergySystem {
  public BurningSystem() {
    super(Family.all(BurningComponent.class, AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    BurningComponent burning = ComponentMappers.burning.get(entity);

    if (burning.counter == burning.life) {
      entity.remove(BurningComponent.class);
    } else {
      WorldManager.entityHelpers.takeDamage(entity, burning.damage);

      PositionComponent position = ComponentMappers.position.get(entity);
      WorldManager.mapHelpers.makeFloorBloody(position.pos);

      if (WorldManager.entityHelpers.canSee(WorldManager.player, entity)) {
        AttributesComponent attributes = ComponentMappers.attributes.get(entity);

        boolean isPlayer = ComponentMappers.player.has(entity);

        WorldManager.log.add(
            "effects.burning.tookDamage", (isPlayer ? "You" : attributes.name),
            burning.damage
        );

        if (attributes.health <= 0) {
          WorldManager.log.add("effects.burning.died", (isPlayer ? "You" : attributes.name));
        }
      }

      burning.counter += 1;
    }
  }
}
