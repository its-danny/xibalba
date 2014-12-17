package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class BrainComponent extends Component {
  public int vision;

  public BrainComponent(int vision) {
    this.vision = vision;
  }
}
