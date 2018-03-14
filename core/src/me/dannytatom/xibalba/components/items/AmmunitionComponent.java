package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;

public class AmmunitionComponent implements Component {
  public final String type;

  /**
   * Component for ammunition.
   *
   * @param type Ammunition type
   */
  public AmmunitionComponent(String type) {
    this.type = type;
  }
}
