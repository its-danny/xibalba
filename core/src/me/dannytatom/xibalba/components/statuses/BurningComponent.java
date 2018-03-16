package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

public class BurningComponent implements Component {
  public final int life = MathUtils.random(3, 5);
  public int counter = 0;
  public int damage;

  public BurningComponent(int damage) {
    this.damage = damage;
  }
}
