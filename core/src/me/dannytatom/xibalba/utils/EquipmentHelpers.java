package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;

import java.util.Objects;

public class EquipmentHelpers {
  public EquipmentHelpers() {

  }

  public boolean isEquip(Entity entity, Entity item) {
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);

    return equipmentComponent.slots.containsValue(item);
  }

  public String getLocation(Entity entity, Entity item) {
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);

    if (equipmentComponent.slots.get("rightHand") == item) {
      return "right hand";
    } else if (Objects.equals(itemComponent.location, "leftHand")) {
      return "left hand";
    } else {
      return itemComponent.location;
    }
  }

  public void holdItem(Entity entity, Entity item) {
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);

    if (itemComponent.actions.get("canHold")) {
      equipmentComponent.slots.put("rightHand", item);
    }
  }

  public void wearItem(Entity entity, Entity item) {
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);

    equipmentComponent.slots.put(itemComponent.location, item);
  }

  public void removeItem(Entity entity, Entity item) {
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);

    if (equipmentComponent.slots.get("rightHand") == item) {
      equipmentComponent.slots.put("rightHand", null);
    } else {
      equipmentComponent.slots.put(itemComponent.location, null);
    }
  }

  public Entity getPrimaryWeapon(Entity entity) {
    return entity.getComponent(EquipmentComponent.class).slots.get("rightHand");
  }

  public int getCombinedDefense(Entity entity) {
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);
    int defense = 0;

    for (Entity item : equipmentComponent.slots.values()) {
      if (item != null) {
        ItemComponent itemComponent = item.getComponent(ItemComponent.class);

        if (Objects.equals(itemComponent.type, "armor")) {
          defense += item.getComponent(ItemComponent.class).attributes.get("defense");
        }
      }
    }

    return defense;
  }
}
