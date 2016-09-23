package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

public class WetComponent implements Component {
  public final int life = MathUtils.random(1, 2);
  public int counter = 0;

  public WetComponent() {

  }
}
