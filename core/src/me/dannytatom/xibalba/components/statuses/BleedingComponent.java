package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class BleedingComponent implements Component {
  public final int damage;
  public final int life;
  public int counter = 0;

  /**
   * Component for bleeding status.
   *
   * @param life How long this status lasts
   */
  public BleedingComponent(int damage, int life) {
    this.damage = damage;
    this.life = life;
  }
}
