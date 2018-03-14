package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component {
  public static final int COST = 100;

  public final Vector2 pos;

  /**
   * Component for movement.
   *
   * @param pos Target position on the current map
   */
  public MovementComponent(Vector2 pos) {
    this.pos = pos;
  }
}
