package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.yaml.EnemyData;
import me.dannytatom.xibalba.utils.yaml.ItemData;

import java.util.HashMap;

public class EffectsComponent implements Component {
  public final HashMap<String, String> effects;

  public EffectsComponent(ItemData data) {
    this.effects = data.effects;
  }

  public EffectsComponent(EnemyData data) {
    this.effects = data.effects;
  }

  public EffectsComponent() {
    this.effects = new HashMap<>();
  }
}
