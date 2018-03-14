package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.statuses.DrowningComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class DrowningSystem extends UsesEnergySystem {
  /**
   * Handles drowning. </p> Take 5 damage for every turn you're in deep water. Once you leave deep
   * water we remove the DrowningComponent as you're no longer drowning.
   */
  public DrowningSystem() {
    super(
        Family.all(
            DrowningComponent.class, AttributesComponent.class, PositionComponent.class
        ).get()
    );
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent position = ComponentMappers.position.get(entity);

    if (!WorldManager.mapHelpers.getCell(position.pos.x, position.pos.y).isDeepWater()) {
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);
      attributes.oxygen = attributes.maxOxygen;

      entity.remove(DrowningComponent.class);
    } else {
      WorldManager.entityHelpers.takeDamage(entity, 5);

      AttributesComponent attributes = ComponentMappers.attributes.get(entity);

      if (WorldManager.entityHelpers.canSee(WorldManager.player, entity)) {
        boolean isPlayer = ComponentMappers.player.has(entity);

        WorldManager.log.add(
            "effects.drowning.tookDamage", (isPlayer ? "You" : attributes.name), 5
        );

        if (attributes.health <= 0) {
          WorldManager.log.add("effects.drowning.died", (isPlayer ? "You" : attributes.name));
        }
      }
    }
  }
}
