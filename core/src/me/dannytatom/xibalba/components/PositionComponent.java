package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
  public final Vector2 pos;

  /**
   * Holds entity pos.
   *
   * @param position where to spawn it
   */
  public PositionComponent(Vector2 position) {
    this.pos = position;
  }

  public PositionComponent(int cellX, int cellY) {
    this.pos = new Vector2(cellX, cellY);
  }

  public PositionComponent() {
    this.pos = new Vector2();
  }
}
