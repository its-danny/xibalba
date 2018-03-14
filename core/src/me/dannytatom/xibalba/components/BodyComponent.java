package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;
import java.util.TreeMap;

public class BodyComponent implements Component {
  public final TreeMap<String, Integer> parts;
  public final TreeMap<String, String> wearable;
  public final HashMap<String, Integer> damage;

  /**
   * Body parts.
   *
   * @param parts    The entity's body parts
   * @param wearable Which body parts are wearable
   */
  public BodyComponent(TreeMap<String, Integer> parts, TreeMap<String, String> wearable) {
    this.parts = parts;
    this.wearable = wearable;

    this.damage = new HashMap<>();
    for (String part : parts.keySet()) {
      this.damage.put(part, 0);
    }
  }

  public BodyComponent(TreeMap<String, Integer> parts) {
    this(parts, null);
  }
}
