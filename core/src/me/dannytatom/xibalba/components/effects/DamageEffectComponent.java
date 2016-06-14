package me.dannytatom.xibalba.components.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class DamageEffectComponent implements Component {
  public final Entity starter;
  public final String type;
  public final int turns;
  public final int damage;
  public int currentTurn = 0;

  /**
   * Damage effect component.
   *
   * @param starter Who's damaging
   * @param type    The type of damage (poison, fire, etc)
   * @param turns   How many turns the damage lasts
   * @param damage  How much damage it does
   */
  public DamageEffectComponent(Entity starter, String type, int turns, int damage) {
    this.starter = starter;
    this.type = type;
    this.turns = turns;
    this.damage = damage;
  }
}
