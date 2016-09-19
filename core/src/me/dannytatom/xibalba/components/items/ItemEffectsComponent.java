package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.YamlToItem;

import java.util.HashMap;

public class ItemEffectsComponent implements Component {
  public HashMap<String, String> effects;

  public ItemEffectsComponent(YamlToItem data) {
    this.effects = data.effects;
  }

  public ItemEffectsComponent() {
    this.effects = new HashMap<>();
  }
}
