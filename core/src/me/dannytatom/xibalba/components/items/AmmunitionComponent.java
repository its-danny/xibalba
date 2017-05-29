package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.yaml.ItemData;

public class AmmunitionComponent implements Component {
  public final String type;

  public AmmunitionComponent(ItemData data) {
    this.type = data.ammunitionType;
  }
}
