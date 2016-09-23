package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.items.AmmunitionComponent;
import me.dannytatom.xibalba.components.items.ItemEffectsComponent;
import me.dannytatom.xibalba.components.items.WeaponComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ItemHelpers {
  public ItemHelpers() {

  }

  /**
   * Get item name.
   *
   * @param entity The entity looking
   * @param item   The item itself
   *
   * @return The item name if identified, ??? otherwise
   */
  public String getName(Entity entity, Entity item) {
    ItemComponent details = ComponentMappers.item.get(item);

    if (isIdentified(entity, item)) {
      return details.name;
    } else {
      return "???";
    }
  }

  /**
   * Whether or not an item is identified.
   *
   * @param entity The entity looking
   * @param item   The item itself
   *
   * @return If it is indeed identified
   */
  public boolean isIdentified(Entity entity, Entity item) {
    PlayerComponent player = ComponentMappers.player.get(entity);
    ItemComponent details = ComponentMappers.item.get(item);

    return player == null
        || !(Objects.equals(details.type, "consumable")
        && !player.identifiedItems.contains(details.name, false));
  }

  /**
   * Add an item to an entity's inventory.
   *
   * @param entity Entity we wanna to give shit to
   * @param item   The item itself
   */
  public void addToInventory(Entity entity, Entity item) {
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);

    if (inventory != null) {
      if (inventory.items.size() < 10) {
        item.remove(PositionComponent.class);
        inventory.items.add(item);

        EquipmentComponent equipment = ComponentMappers.equipment.get(entity);
        ItemComponent itemDetails = ComponentMappers.item.get(item);

        WorldManager.log.add(
            "You picked up a " + WorldManager.itemHelpers.getName(entity, item)
        );

        if (Objects.equals(itemDetails.type, "weapon")
            && equipment.slots.get("right hand") == null) {
          hold(entity, item);
          WorldManager.log.add(
              "You are now holding a " + WorldManager.itemHelpers.getName(entity, item)
          );
        }
      }
    }
  }

  /**
   * Equip item to right hand (primary weapon slot).
   *
   * @param entity The entity we want to hold the item
   * @param item   The item itself
   */
  public void hold(Entity entity, Entity item) {
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
  public void wear(Entity entity, Entity item) {
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
   * Eat some shit, bro.
   *
   * @param entity Who eating?
   * @param item   What they eating
   */
  public void eat(Entity entity, Entity item) {
    if (item != null) {
      ItemComponent itemDetails = ComponentMappers.item.get(item);

      WorldManager.log.add("You eat a " + itemDetails.name);

      ItemEffectsComponent itemEffects = ComponentMappers.itemEffects.get(item);

      if (itemEffects != null) {
        for (Map.Entry<String, String> entry : itemEffects.effects.entrySet()) {
          String event = entry.getKey();
          String action = entry.getValue();

          if (Objects.equals(event, "onConsume")) {
            WorldManager.entityHelpers.applyEffect(entity, action);
          }
        }
      }

      PlayerComponent player = ComponentMappers.player.get(entity);

      if (player != null && !player.identifiedItems.contains(itemDetails.name, true)) {
        player.identifiedItems.add(itemDetails.name);
      }

      destroy(entity, item);
    }
  }

  /**
   * Apply an item to another item.
   *
   * @param entity       Who doing this?
   * @param applyingItem Item we're applying
   * @param targetItem   What we're applying it to
   */
  public void apply(Entity entity, Entity applyingItem, Entity targetItem) {
    if (applyingItem != null && targetItem != null) {
      ItemEffectsComponent applyingItemEffects = ComponentMappers.itemEffects.get(applyingItem);
      ItemEffectsComponent targetItemEffects = ComponentMappers.itemEffects.get(targetItem);

      if (targetItemEffects == null) {
        targetItem.add(new ItemEffectsComponent());
      }

      if (targetItemEffects != null) {
        targetItemEffects.effects.put("onHit", applyingItemEffects.effects.get("onApply"));
      }

      destroy(entity, applyingItem);
    }
  }

  /**
   * Remove an item from either their right hand or wherever it's slotted.
   *
   * @param entity The entity we want to remove the item from
   * @param item   The item itself
   */
  public void remove(Entity entity, Entity item) {
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);
    ItemComponent itemDetails = ComponentMappers.item.get(item);

    if (equipment.slots.get("right hand") == item) {
      equipment.slots.put("right hand", null);
    } else {
      equipment.slots.put(itemDetails.location, null);
    }
  }

  /**
   * Drop an item to the ground.
   *
   * @param entity   Whose shit we're dropping
   * @param item     What we're dropping
   * @param position Where we gonna drop it
   */
  public void drop(Entity entity, Entity item, Vector2 position) {
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);

    if (inventory != null) {
      if (isEquipped(entity, item)) {
        remove(entity, item);
      }

      WorldManager.entityHelpers.updatePosition(item, position);
      inventory.items.remove(item);

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add(
            "You dropped a " + WorldManager.itemHelpers.getName(entity, item)
        );
      }
    }
  }

  /**
   * This seems mostly unnecessary. Drop it at their feet.
   *
   * @param entity Whose feet we're dropping shit on
   * @param item   What we're dropping
   */
  public void drop(Entity entity, Entity item) {
    if (ComponentMappers.inventory.has(entity)) {
      drop(entity, item, ComponentMappers.position.get(entity).pos);
    }
  }

  /**
   * It's like drop except we remove it 100%.
   *
   * @param entity Entity we want to take shit from
   * @param item   The shit we gonna take
   */
  public void destroy(Entity entity, Entity item) {
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);

    if (inventory != null && item != null) {
      inventory.items.remove(item);
      WorldManager.world.removeEntity(item);
    }
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

  public Entity getRightHand(Entity entity) {
    return ComponentMappers.equipment.get(entity).slots.get("right hand");
  }

  /**
   * Iterate through inventory, return whatever item has the `throwing` flag set to true.
   *
   * @param entity Entity whose throwing actions
   *
   * @return The item being thrown
   */
  public Entity getThrowing(Entity entity) {
    ArrayList<Entity> items = ComponentMappers.inventory.get(entity).items;

    for (Entity item : items) {
      ItemComponent itemDetails = ComponentMappers.item.get(item);

      if (itemDetails.throwing) {
        return item;
      }
    }

    return null;
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

  /**
   * Find out if an entity has the ammunition it needs.
   *
   * @param entity Who needs to know
   * @param type   Type of ammunition they're looking for
   *
   * @return Whether or not they got it
   */
  public boolean hasAmmunitionOfType(Entity entity, String type) {
    ArrayList<Entity> items = ComponentMappers.inventory.get(entity).items;

    for (Entity item : items) {
      AmmunitionComponent ammunition = ComponentMappers.ammunition.get(item);

      if (ammunition != null && Objects.equals(ammunition.type, type)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Get a count of ammo.
   *
   * @param entity Who has the ammo?
   * @param type   What type of ammo?
   *
   * @return How much they got
   */
  public int amountOfAmmunitionType(Entity entity, String type) {
    ArrayList<Entity> items = ComponentMappers.inventory.get(entity).items;
    int count = 0;

    for (Entity item : items) {
      AmmunitionComponent ammunition = ComponentMappers.ammunition.get(item);

      if (ammunition != null && Objects.equals(ammunition.type, type)) {
        count += 1;
      }
    }

    return count;
  }

  /**
   * Return the first item of ammunition type given.
   *
   * @param entity Entity whose inventory we're looking at
   * @param type   Type of ammunition we're looking for
   *
   * @return Some (or, well, 1) ammunition
   */
  public Entity getAmmunitionOfType(Entity entity, String type) {
    ArrayList<Entity> items = ComponentMappers.inventory.get(entity).items;

    for (Entity item : items) {
      AmmunitionComponent ammunition = ComponentMappers.ammunition.get(item);

      if (ammunition != null && Objects.equals(ammunition.type, type)) {
        return item;
      }
    }

    return null;
  }
}
