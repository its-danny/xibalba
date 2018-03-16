package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

import me.dannytatom.xibalba.world.WorldManager;

public class DealDamage extends Effect {
  public int damage;

  @Override
  public void act(Entity caster, Entity target) {
    WorldManager.entityHelpers.takeDamage(target, damage);
  }

  @Override
  public void revoke(Entity caster, Entity target) {

  }
}
