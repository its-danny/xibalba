package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

public class SickComponent implements Component {
  public final int damage;
  public final int life;
  public int counter = 0;

  /**
   * Component for sick status.
   *
   * @param life How long this status lasts
   */
  public SickComponent(int life) {
    this.life = life;
    this.damage = MathUtils.random(5, 10);
  }
}
