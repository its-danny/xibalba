package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

public class EquipmentComponent implements Component {
  public final HashMap<String, Entity> slots = new HashMap<>();

  /**
   * Set up the equipment slots.
   */
  public EquipmentComponent() {
    slots.put("head", null);
    slots.put("body", null);
    slots.put("arms", null);
    slots.put("left hand", null);
    slots.put("right hand", null);
    slots.put("legs", null);
    slots.put("feet", null);
  }
}
