package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class PoisonedComponent implements Component {
  public int damage = 0;
  public int life = 0;
  public int counter = 0;

  public PoisonedComponent(int damage, int life) {
    this.damage = damage;
    this.life = life;
  }
}
