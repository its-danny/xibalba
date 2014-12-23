package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent extends Component {
  public final int speed;
  public final int vision;
  public int energy;

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
