package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent extends Component {
  public Vector2 pos;

  /**
   * Holds entity pos.
   *
   * @param position where to spawn it
   */
  public PositionComponent(Vector2 position) {
    this.pos = position;
  }
}
