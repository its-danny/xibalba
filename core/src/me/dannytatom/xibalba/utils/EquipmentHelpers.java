package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;

import java.util.Objects;

public class EquipmentHelpers {
  public EquipmentHelpers() {

  }

  /**
   * Check if an item is equipped.
   *
   * @param entity The entity whose equipment we're checking
   * @param item   The item we want to check
   *
   * @return Whether or not it's equipped
   */
  public boolean isEquipped(Entity entity, Entity item) {
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);

    return equipmentComponent.slots.containsValue(item);
  }

  /**
   * Get the slot location of an item.
   *
   * @param entity The entity whose equipment we're checking
   * @param item   The item we want to check
   *
   * @return Location of item
   */
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

  /**
   * Equip item to right hand (primary weapon slot).
   *
   * @param entity The entity we want to hold the item
   * @param item   The item itself
   */
  public void holdItem(Entity entity, Entity item) {
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);

    if (itemComponent.actions.get("canHold")) {
      equipmentComponent.slots.put("rightHand", item);
    }
  }

  /**
   * Equip an item to that item's location.
   *
   * @param entity The entity we want to wear the item
   * @param item   The item itself
   */
  public void wearItem(Entity entity, Entity item) {
    EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);

    equipmentComponent.slots.put(itemComponent.location, item);
  }

  /**
   * Remove an item from either their right hand or whever it's slotted.
   *
   * @param entity The entity we want to remove the item from
   * @param item   The item itself
   */
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

  /**
   * Get combined defense from all the items they're wearing.
   *
   * @param entity Who we're getting defense of
   *
   * @return Their combined defense
   */
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
