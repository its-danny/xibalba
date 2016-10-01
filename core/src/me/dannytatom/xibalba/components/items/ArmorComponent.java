package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.YamlToItem;

public class ArmorComponent implements Component {
  private final String type;

  public ArmorComponent(YamlToItem data) {
    this.type = data.armorType;
  }
}
