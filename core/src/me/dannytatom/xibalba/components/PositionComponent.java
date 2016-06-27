package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
  public int map;
  public Vector2 pos;

  /**
   * Holds entity pos.
   *
   * @param position where to spawn it
   */
  public PositionComponent(int map, Vector2 position) {
    this.map = map;
    this.pos = position;
  }
}
