package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class RaiseSpeed extends Effect {
  public int amount;

  @Override
  public void act(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    attributes.speed += amount;

    if (ComponentMappers.player.has(entity)) {
      WorldManager.log.add("stats.speedRaised", "You", amount);

      ComponentMappers.player.get(entity).totalDamageHealed += amount;
    } else {
      WorldManager.log.add("stats.speedRaised", attributes.name, amount);
    }
  }

  @Override
  public void revoke(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    attributes.speed -= amount;

    if (ComponentMappers.player.has(entity)) {
      WorldManager.log.add("stats.speedLowered", "You", amount);

      ComponentMappers.player.get(entity).totalDamageHealed += amount;
    } else {
      WorldManager.log.add("stats.speedLowered", attributes.name, amount);
    }
  }
}
