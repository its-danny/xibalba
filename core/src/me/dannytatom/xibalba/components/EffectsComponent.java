package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

import me.dannytatom.xibalba.utils.yaml.EnemyData;
import me.dannytatom.xibalba.utils.yaml.ItemData;

public class EffectsComponent implements Component {
  public final HashMap<String, String> effects;

  /**
   * Effects for an item.
   *
   * @param data Item data
   */
  public EffectsComponent(ItemData data) {
    this.effects = data.effects;
  }

  /**
   * Effects for an enemy.
   *
   * @param data Enemy data
   */
  public EffectsComponent(EnemyData data) {
    this.effects = data.effects;
  }

  public EffectsComponent() {
    this.effects = new HashMap<>();
  }
}
