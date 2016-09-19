package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.items.WeaponComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.Map;

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
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);

    return equipment.slots.containsValue(item);
  }

  /**
   * Get the slot location of an item.
   *
   * @param item The item we want to check
   *
   * @return Location of item
   */
  public String getLocation(Entity entity, Entity item) {
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);

    for (Map.Entry<String, Entity> slot : equipment.slots.entrySet()) {
      if (slot.getValue() == item) {
        return slot.getKey();
      }
    }

    return null;
  }

  /**
   * Equip item to right hand (primary weapon slot).
   *
   * @param entity The entity we want to hold the item
   * @param item   The item itself
   */
  public void holdItem(Entity entity, Entity item) {
    ItemComponent itemDetails = ComponentMappers.item.get(item);
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);

    if (itemDetails.actions.contains("hold", false)) {
      equipment.slots.put("right hand", item);

      if (itemDetails.twoHanded) {
        equipment.slots.put("left hand", null);
      }
    }
  }

  /**
   * Equip an item to that item's location.
   *
   * @param entity The entity we want to wear the item
   * @param item   The item itself
   */
  public void wearItem(Entity entity, Entity item) {
    ItemComponent itemDetails = ComponentMappers.item.get(item);
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);

    equipment.slots.put(itemDetails.location, item);

    Entity primary = getRightHand(entity);

    if (primary != null) {
      ItemComponent primaryDetails = ComponentMappers.item.get(primary);

      if (primaryDetails.twoHanded) {
        equipment.slots.put(primaryDetails.location, null);
      }
    }
  }

  /**
   * Remove an item from either their right hand or wherever it's slotted.
   *
   * @param entity The entity we want to remove the item from
   * @param item   The item itself
   */
  public void removeItem(Entity entity, Entity item) {
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);
    ItemComponent itemDetails = ComponentMappers.item.get(item);

    if (equipment.slots.get("right hand") == item) {
      equipment.slots.put("right hand", null);
    } else {
      equipment.slots.put(itemDetails.location, null);
    }
  }

  public Entity getRightHand(Entity entity) {
    return ComponentMappers.equipment.get(entity).slots.get("right hand");
  }

  /**
   * Whether or not their primary weapon uses ammo.
   *
   * @param entity Entity we're checking.
   *
   * @return Does it?
   */
  public boolean primaryWeaponUsesAmmo(Entity entity) {
    Entity item = getRightHand(entity);

    if (item != null) {
      WeaponComponent weapon = ComponentMappers.weapon.get(item);

      return weapon != null && weapon.ammunitionType != null;
    } else {
      return false;
    }
  }
}
