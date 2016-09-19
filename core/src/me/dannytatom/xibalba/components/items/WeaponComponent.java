package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.YamlToItem;

public class WeaponComponent implements Component {
  public final String type;
  public final String ammunitionType;

  public WeaponComponent(YamlToItem data) {
    this.type = data.weaponType;
    this.ammunitionType = data.ammunition;
  }
}
