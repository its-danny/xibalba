package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.world.WorldManager;

public class DealDamage extends Effect {
  public int damage;

  @Override
  public void act(Entity entity) {
    WorldManager.entityHelpers.takeDamage(entity, damage);
  }

  @Override
  public void revoke(Entity entity) {

  }
}
