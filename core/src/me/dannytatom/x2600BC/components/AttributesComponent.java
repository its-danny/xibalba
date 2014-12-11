package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class AttributesComponent extends Component {
  public int speed;
  public ArrayList<String> actions;

  /**
   * Holds entity attributes.
   *
   * @param speed The entity's speed
   */
  public AttributesComponent(int speed) {
    this.speed = speed;
    actions = new ArrayList<>();
  }
}
