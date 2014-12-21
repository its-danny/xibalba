package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent extends Component {
  public int energy;
  public int speed;
  public int vision;

  /**
   * Holds entity attributes.
   *
   * @param speed The entity's speed
   */
  public AttributesComponent(int speed, int vision) {
    this.energy = speed;
    this.speed = speed;
    this.vision = vision;
  }
}
