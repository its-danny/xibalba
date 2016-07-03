package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

public class BodyComponent implements Component {
  public final HashMap<String, Integer> parts;
  public final HashMap<String, Integer> damage;

  public BodyComponent(HashMap<String, Integer> parts) {
    this.parts = parts;
    this.damage = new HashMap<>();

    for (String part : parts.keySet()) {
      this.damage.put(part, 0);
    }
  }
}
