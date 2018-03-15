package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import me.dannytatom.xibalba.effects.Effect;

public class BodyComponent implements Component {
  public final TreeMap<String, Integer> bodyParts;
  public final TreeMap<String, ArrayList<Effect>> wearableBodyParts;
  public final HashMap<String, Integer> damage;

  /**
   * Body bodyParts.
   *
   * @param bodyParts         The entity's body bodyParts
   * @param wearableBodyParts Which body bodyParts are wearableBodyParts
   */
  public BodyComponent(TreeMap<String, Integer> bodyParts,
                       TreeMap<String, ArrayList<Effect>> wearableBodyParts) {
    this.bodyParts = bodyParts;
    this.wearableBodyParts = wearableBodyParts;

    this.damage = new HashMap<>();
    for (String part : bodyParts.keySet()) {
      this.damage.put(part, 0);
    }
  }

  public BodyComponent(TreeMap<String, Integer> bodyParts) {
    this(bodyParts, null);
  }
}
