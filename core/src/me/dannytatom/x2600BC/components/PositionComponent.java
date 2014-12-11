package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent extends Component {
  public int x;
  public int y;
  public String moveDir;

  /**
   * Component that holds position data for all entities.
   *
   * @param x The entity's current x position
   * @param y The entity's current y position
   */
  public PositionComponent(int x, int y) {
    this.x = x;
    this.y = y;
    moveDir = null;
  }
}
