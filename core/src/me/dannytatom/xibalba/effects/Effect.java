package me.dannytatom.xibalba.effects;

import com.badlogic.ashley.core.Entity;

public abstract class Effect {
  public Type type;
  public Trigger trigger;

  public abstract void act(Entity entity);

  public abstract void revoke(Entity entity);

  public enum Type {
    ACTIVE, PASSIVE
  }

  public enum Trigger {
    APPLY, CONSUME, HIT, WEAR, INSTANT,
  }
}