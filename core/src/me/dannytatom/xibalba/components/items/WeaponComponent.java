package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;

public class WeaponComponent implements Component {
  public final String type;
  public final String ammunitionType;

  /**
   * Component for weapons.
   *
   * @param type           Weapon type
   * @param ammunitionType Ammunition type
   */
  public WeaponComponent(String type, String ammunitionType) {
    this.type = type;
    this.ammunitionType = ammunitionType;
  }
}