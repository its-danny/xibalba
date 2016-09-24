package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

public class SkillsComponent implements Component {
  public final HashMap<String, Integer> levels;
  public final HashMap<String, Integer> counters;

  /**
   * Skills Constructor.
   */
  public SkillsComponent() {
    levels = new HashMap<>();

    levels.put("archery", 0);
    levels.put("bashing", 0);
    levels.put("piercing", 0);
    levels.put("slashing", 0);
    levels.put("throwing", 0);
    levels.put("unarmed", 0);

    counters = new HashMap<>();

    counters.put("archery", 0);
    counters.put("bashing", 0);
    counters.put("piercing", 0);
    counters.put("slashing", 0);
    counters.put("throwing", 0);
    counters.put("unarmed", 0);
  }
}
