package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

public class EquipmentComponent implements Component {
  public HashMap<String, Entity> slots = new HashMap<>();

  public EquipmentComponent() {
    slots.put("head", null);
    slots.put("body", null);
    slots.put("arms", null);
    slots.put("leftHand", null);
    slots.put("rightHand", null);
    slots.put("legs", null);
    slots.put("feet", null);
  }
}
