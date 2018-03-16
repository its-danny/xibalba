package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class StartFire extends Effect {
  @Override
  public void act(Entity caster, Entity target) {
    WorldManager.mapHelpers.startFire(ComponentMappers.position.get(target).pos);
  }

  @Override
  public void revoke(Entity caster, Entity target) {

  }
}
