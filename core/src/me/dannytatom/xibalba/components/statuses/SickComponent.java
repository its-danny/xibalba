package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

public class SickComponent implements Component {
  public int damage = 0;
  public int life = 0;
  public int counter = 0;

  public SickComponent(int life) {
    this.life = life;
    this.damage = MathUtils.random(5, 10);
  }
}
