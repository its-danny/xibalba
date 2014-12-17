package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class FleeComponent extends Component {
  public int targetX;
  public int targetY;

  public FleeComponent(int targetX, int targetY) {
    this.targetX = targetX;
    this.targetY = targetY;
  }
}
