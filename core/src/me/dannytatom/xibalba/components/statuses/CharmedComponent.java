package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class CharmedComponent implements Component {
  public final int life;
  public int counter = 0;

  /**
   * Component for charmed status.
   *
   * @param life How long this status lasts
   */
  public CharmedComponent(int life) {
    this.life = life;
  }
}
