package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.yaml.ItemData;

public class ArmorComponent implements Component {
  private final String type;

  public ArmorComponent(ItemData data) {
    this.type = data.armorType;
  }
}
