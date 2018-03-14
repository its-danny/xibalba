package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.TreeMap;

public class EquipmentComponent implements Component {
  public final TreeMap<String, Entity> slots = new TreeMap<>();

  /**
   * Equipment component.
   */
  public EquipmentComponent() {
    slots.put("head", null);
    slots.put("body", null);
    slots.put("left hand", null);
    slots.put("right hand", null);
    slots.put("feet", null);
  }
}
