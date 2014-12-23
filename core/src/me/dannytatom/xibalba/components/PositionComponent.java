package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent extends Component {
  public Vector2 pos;

  /**
   * Holds entity pos.
   *
   * @param x The entity's current x pos
   * @param y The entity's current y pos
   */
  public PositionComponent(Vector2 position) {
    this.pos = position;
  }
}
