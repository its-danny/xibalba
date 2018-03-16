package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.PoisonedComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class Poison extends Effect {
  public float chance;
  public int damage;
  public int life;

  @Override
  public void act(Entity caster, Entity target) {
    if (ComponentMappers.poisoned.has(target)) {
      return;
    }

    if (MathUtils.random() < chance / 100) {
      target.add(new PoisonedComponent(damage, life));

      if (ComponentMappers.player.has(target)) {
        WorldManager.log.add("effects.poisoned.started", "You", "are");
      } else {
        AttributesComponent attributes = ComponentMappers.attributes.get(target);

        WorldManager.log.add("effects.poisoned.started", attributes.name, "is");
      }
    }
  }

  @Override
  public void revoke(Entity caster, Entity target) {

  }
}
