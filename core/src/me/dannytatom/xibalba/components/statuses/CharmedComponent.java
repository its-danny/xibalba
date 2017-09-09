package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class CharmedComponent implements Component {
  public int life = 0;
  public int counter = 0;

  public CharmedComponent(int life) {
    this.life = life;
  }
}
