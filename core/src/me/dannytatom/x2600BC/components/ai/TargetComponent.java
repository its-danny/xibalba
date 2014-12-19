package me.dannytatom.x2600BC.components.ai;

import com.badlogic.ashley.core.Component;

public class TargetComponent extends Component {
  public int x;
  public int y;

  public TargetComponent(int x, int y) {
    this.x = x;
    this.y = y;
  }
}
