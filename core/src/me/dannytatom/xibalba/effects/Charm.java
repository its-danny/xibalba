package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.statuses.CharmedComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class Charm extends Effect {
  public int life;

  @Override
  public void act(Entity entity) {
    if (ComponentMappers.charmed.has(entity)) {
      return;
    }

    entity.add(new CharmedComponent(life));

    if (ComponentMappers.player.has(entity)) {
      WorldManager.log.add("effects.charmed.started", "You", "are");
    } else {
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);

      WorldManager.log.add("effects.charmed.started", attributes.name, "is");
    }
  }

  @Override
  public void revoke(Entity entity) {

  }
}
