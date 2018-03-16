package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class StartFire extends Effect {
  @Override
  public void act(Entity entity) {
    WorldManager.mapHelpers.startFire(ComponentMappers.position.get(entity).pos);
  }

  @Override
  public void revoke(Entity entity) {

  }
}
