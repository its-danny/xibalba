package me.dannytatom.xibalba.systems.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.CharmedComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class CharmedSystem extends UsesEnergySystem {
  public CharmedSystem() {
    super(Family.all(CharmedComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    CharmedComponent charmed = ComponentMappers.charmed.get(entity);

    if (charmed.counter == charmed.life) {
      entity.remove(CharmedComponent.class);

      boolean isPlayer = ComponentMappers.player.has(entity);
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);

      WorldManager.log.add(
          "effects.charmed.stopped",
          (isPlayer ? "You" : attributes.name),
          (isPlayer ? "are" : "is")
      );
    } else {
      charmed.counter += 1;
    }
  }
}
