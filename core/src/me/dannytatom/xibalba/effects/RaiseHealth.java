package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.world.WorldManager;

public class RaiseHealth extends Effect {
  public int amount;

  @Override
  public void act(Entity caster, Entity target) {
    WorldManager.entityHelpers.raiseHealth(target, amount);
  }

  @Override
  public void revoke(Entity caster, Entity target) {

  }
}
