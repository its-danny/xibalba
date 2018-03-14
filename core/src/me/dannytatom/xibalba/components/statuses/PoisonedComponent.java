package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class PoisonedComponent implements Component {
  public final int damage;
  public final int life;
  public int counter = 0;

  /**
   * Component for poisoned status.
   *
   * @param damage How much damage they should take each turn
   * @param life   How long this status lasts
   */
  public PoisonedComponent(int damage, int life) {
    this.damage = damage;
    this.life = life;
  }
}
