package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class CrippledComponent implements Component {
  public final int life;
  public int counter = 0;
  public int turnCounter = 0;

  /**
   * Component for crippled status.
   *
   * @param life How long this status lasts
   */
  public CrippledComponent(int life) {
    this.life = life;
  }
}
