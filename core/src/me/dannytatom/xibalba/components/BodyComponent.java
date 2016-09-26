package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;
import java.util.TreeMap;

public class BodyComponent implements Component {
  public final TreeMap<String, Integer> parts;
  public final HashMap<String, Integer> damage;

  /**
   * Body parts.
   *
   * @param parts HashMap of parts
   */
  public BodyComponent(TreeMap<String, Integer> parts) {
    this.parts = parts;
    this.damage = new HashMap<>();

    for (String part : parts.keySet()) {
      this.damage.put(part, 0);
    }
  }
}
