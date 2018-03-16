package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.CharmedComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class Charm extends Effect {
  public int life;

  @Override
  public void act(Entity caster, Entity target) {
    if (ComponentMappers.charmed.has(target)) {
      return;
    }

    target.add(new CharmedComponent(life));

    if (ComponentMappers.player.has(target)) {
      WorldManager.log.add("effects.charmed.started", "You", "are");
    } else {
      AttributesComponent attributes = ComponentMappers.attributes.get(target);

      WorldManager.log.add("effects.charmed.started", attributes.name, "is");
    }
  }

  @Override
  public void revoke(Entity caster, Entity target) {

  }
}
