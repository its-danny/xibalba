package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent extends Component {
  public int energy;
  public int speed;

  /**
   * Holds entity attributes.
   *
   * @param speed The entity's speed
   */
  public AttributesComponent(int speed) {
    this.energy = speed;
    this.speed = speed;
  }
}
