package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class RaiseSpeed extends Effect {
  public int amount;

  @Override
  public void act(Entity caster, Entity target) {
    AttributesComponent attributes = ComponentMappers.attributes.get(target);
    attributes.speed += amount;

    if (ComponentMappers.player.has(target)) {
      WorldManager.log.add("stats.speedRaised", "You", amount);

      ComponentMappers.player.get(target).totalDamageHealed += amount;
    } else {
      WorldManager.log.add("stats.speedRaised", attributes.name, amount);
    }
  }

  @Override
  public void revoke(Entity caster, Entity target) {
    AttributesComponent attributes = ComponentMappers.attributes.get(target);
    attributes.speed -= amount;

    if (ComponentMappers.player.has(target)) {
      WorldManager.log.add("stats.speedLowered", "You", amount);

      ComponentMappers.player.get(target).totalDamageHealed += amount;
    } else {
      WorldManager.log.add("stats.speedLowered", attributes.name, amount);
    }
  }
}
