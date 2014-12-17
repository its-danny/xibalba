package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent extends Component {
  public int x;
  public int y;

  /**
   * Holds entity position.
   *
   * @param cellX The entity's current x position
   * @param cellY The entity's current y position
   */
  public PositionComponent(int cellX, int cellY) {
    this.x = cellX;
    this.y = cellY;
  }
}
