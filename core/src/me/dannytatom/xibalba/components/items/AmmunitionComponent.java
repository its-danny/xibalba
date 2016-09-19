package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.YamlToItem;

public class AmmunitionComponent implements Component {
  public final String type;

  public AmmunitionComponent(YamlToItem data) {
    this.type = data.ammunitionType;
  }
}
