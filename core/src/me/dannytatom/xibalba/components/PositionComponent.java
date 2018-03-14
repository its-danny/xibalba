package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
  public final Vector2 pos;

  /**
   * Holds entity pos.
   *
   * @param position Where to spawn it
   */
  public PositionComponent(Vector2 position) {
    this.pos = position;
  }

  /**
   * Holds entity pos.
   *
   * @param cellX X position of map cell to spawn it
   * @param cellY Y position of map cell to spawn it
   */
  public PositionComponent(int cellX, int cellY) {
    this.pos = new Vector2(cellX, cellY);
  }

  public PositionComponent() {
    this.pos = new Vector2();
  }
}
