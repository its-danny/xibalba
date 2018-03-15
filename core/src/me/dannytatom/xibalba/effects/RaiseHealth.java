package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.world.WorldManager;

public class RaiseHealth extends Effect {
  public int amount;

  @Override
  public void act(Entity entity) {
    WorldManager.entityHelpers.raiseHealth(entity, amount);
  }

  @Override
  public void revoke(Entity entity) {

  }
}
