package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.YamlToEnemy;
import me.dannytatom.xibalba.utils.YamlToItem;

import java.util.HashMap;

public class EffectsComponent implements Component {
  public final HashMap<String, String> effects;

  public EffectsComponent(YamlToItem data) {
    this.effects = data.effects;
  }

  public EffectsComponent(YamlToEnemy data) {
    this.effects = data.effects;
  }

  public EffectsComponent() {
    this.effects = new HashMap<>();
  }
}
