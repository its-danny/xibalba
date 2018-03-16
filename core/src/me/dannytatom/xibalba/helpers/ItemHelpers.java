package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EffectsComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.LimbComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.items.AmmunitionComponent;
import me.dannytatom.xibalba.components.statuses.EncumberedComponent;
import me.dannytatom.xibalba.components.statuses.SickComponent;
import me.dannytatom.xibalba.effects.Effect;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class ItemHelpers {
  public ItemHelpers() {

  }

  /**
   * Get item name.
   *
   * @param entity The entity looking
   * @param item   The item itself
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
   * Whether or not an item has a stone material.
   *
   * @param item The item itself
   * @return Does it have it?
   */
  public boolean hasStoneMaterial(Entity item) {
    if (ComponentMappers.item.has(item)) {
      ItemComponent details = ComponentMappers.item.get(item);

      return details.stoneMaterial != null;
    } else {
      return false;
    }
  }

  /**
   * Get the item's stone material type.
   *
   * @param item The item itself
   * @return The stone material type
   */
  public ItemComponent.StoneMaterial getStoneMaterial(Entity item) {
    if (ComponentMappers.item.has(item)) {
      ItemComponent details = ComponentMappers.item.get(item);

      return details.stoneMaterial;
    } else {
      return null;
    }
  }

  /**
   * Does the entity have the necessary items to craft this item.
   *
   * @param entity Who's inventory we checkin'
   * @param item   The item they want to craft
   * @return Yes/no
   */
  public boolean hasComponentsForItem(Entity entity, Entity item) {
    ItemComponent itemDetails = ComponentMappers.item.get(item);
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);

    if (inventory != null) {
      for (ItemComponent.RequiredComponent requiredComponent : itemDetails.requiredComponents) {
        ItemComponent requiredComponentDetails = ComponentMappers.item.get(requiredComponent.item);

        int count = 0;

        for (Entity inventoryItem : inventory.items) {
          ItemComponent inventoryItemDetails = ComponentMappers.item.get(inventoryItem);

          if (requiredComponentDetails.key.equals(inventoryItemDetails.key)) {
            count += 1;
          }
        }

        if (count < requiredComponent.amount) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Get a list of components from the entity's inventory for the given item.
   *
   * @param entity Who's inventory to check
   * @param item   The item who's components we're getting
   * @return A list of components
   */
  public ArrayList<Entity> getComponentsForItem(Entity entity, Entity item) {
    ItemComponent itemDetails = ComponentMappers.item.get(item);
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);
    ArrayList<Entity> components = new ArrayList<>();

    if (inventory != null) {
      for (ItemComponent.RequiredComponent requiredComponent : itemDetails.requiredComponents) {
        ItemComponent requiredComponentDetails = ComponentMappers.item.get(requiredComponent.item);

        for (Entity inventoryItem : inventory.items) {
          ItemComponent inventoryItemDetails = ComponentMappers.item.get(inventoryItem);

          if (requiredComponentDetails.key.equals(inventoryItemDetails.key)) {
            components.add(inventoryItem);
          }
        }
      }
    }

    return components;
  }

  /**
   * Get a list of effects from a component.
   *
   * @param component The component to check
   * @return A list of effects
   */
  public ArrayList<Effect> effectsFromComponent(Entity component) {
    if (ComponentMappers.effects.has(component)) {
      EffectsComponent effects = ComponentMappers.effects.get(component);
      return effects.effects;
    } else {
      return null;
    }
  }

  /**
   * Get the quality for a crafted item.
   *
   * @param components List of components to look through
   * @return The most occurring quality among the components
   */
  public ItemComponent.Quality qualityFromComponents(ArrayList<Entity> components) {
    ArrayList<ItemComponent.Quality> qualities = new ArrayList<>();

    for (Entity component : components) {
      ItemComponent componentDetails = ComponentMappers.item.get(component);
      qualities.add(componentDetails.quality);
    }

    int max = 0;
    ItemComponent.Quality highest = null;

    for (ItemComponent.Quality quality : qualities) {
      int frequency = Collections.frequency(qualities, quality);

      if (max < frequency) {
        max = frequency;
        highest = quality;
      }
    }

    return highest;
  }

  /**
   * Get the stone material for a crafted item.
   *
   * @param components List of components to look through
   * @return The most occurring stone material type among the components
   */
  public ItemComponent.StoneMaterial materialFromComponents(ArrayList<Entity> components) {
    ArrayList<ItemComponent.StoneMaterial> materials = new ArrayList<>();

    for (Entity component : components) {
      ItemComponent componentDetails = ComponentMappers.item.get(component);

      if (componentDetails.stoneMaterial != null) {
        materials.add(componentDetails.stoneMaterial);
      }
    }

    int max = 0;
    ItemComponent.StoneMaterial highest = null;

    for (ItemComponent.StoneMaterial material : materials) {
      int frequency = Collections.frequency(materials, material);

      if (max < frequency) {
        max = frequency;
        highest = material;
      }
    }

    return highest;
  }

  /**
   * Add an item to an entity's inventory.
   *
   * @param entity Entity we wanna to give shit to
   * @param item   The item itself
   * @param log    Whether or not to log this action
   */
  public void addToInventory(Entity entity, Entity item, boolean log) {
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (inventory != null) {
      item.remove(PositionComponent.class);
      inventory.items.add(item);

      EquipmentComponent equipment = ComponentMappers.equipment.get(entity);
      ItemComponent itemDetails = ComponentMappers.item.get(item);

      if (log) {
        WorldManager.log.add(
            "inventory.pickedUp", WorldManager.itemHelpers.getName(entity, item)
        );
      }

      if (itemDetails.twoHanded && WorldManager.entityHelpers.hasDefect(entity, "One arm")) {
        return;
      }

      if (Objects.equals(itemDetails.type, "weapon")) {
        if (!itemDetails.twoHanded || !WorldManager.entityHelpers.hasDefect(entity, "One arm")) {
          if (equipment.slots.get("right hand") == null) {
            hold(entity, item);

            if (log) {
              WorldManager.log.add(
                  "inventory.holding", WorldManager.itemHelpers.getName(entity, item)
              );
            }
          }
        }
      }

      if (Objects.equals(itemDetails.type, "armor")) {
        if (equipment.slots.get(itemDetails.location) == null) {
          wear(entity, item);
        }

        if (log) {
          WorldManager.log.add(
              "inventory.wearing", WorldManager.itemHelpers.getName(entity, item)
          );
        }
      }

      if (getTotalWeight(entity) > attributes.strength * 5) {
        entity.add(new EncumberedComponent());

        if (ComponentMappers.player.has(entity)) {
          WorldManager.log.add("effects.encumbered.started");
        }
      }
    }
  }

  /**
   * Remove an item from inventory.
   *
   * @param entity Who's inventory
   * @param item   The item
   */
  private void removeFromInventory(Entity entity, Entity item) {
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);
    inventory.items.remove(item);

    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (getTotalWeight(entity) <= attributes.strength * 5
        && ComponentMappers.encumbered.has(entity)) {
      entity.remove(EncumberedComponent.class);

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add("effects.encumbered.stopped");
      }
    }
  }

  /**
   * Remove multiple items from inventory.
   *
   * @param entity Who's inventory
   * @param items  A list of items
   */
  public void removeMultipleFromInventory(Entity entity, ArrayList<Entity> items) {
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);
    inventory.items.removeAll(items);

    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (getTotalWeight(entity) <= attributes.strength * 5
        && ComponentMappers.encumbered.has(entity)) {
      entity.remove(EncumberedComponent.class);

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add("effects.encumbered.stopped");
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

    if (ComponentMappers.limb.has(item)) {
      LimbComponent limb = ComponentMappers.limb.get(item);

      if (!limb.deboned) {
        limb.deboned = true;

        WorldManager.log.add("inventory.deboned", itemDetails.name);
      }
    }

    equipment.slots.put(itemDetails.location, item);

    if (ComponentMappers.effects.has(item)) {
      EffectsComponent effects = ComponentMappers.effects.get(item);

      for (Effect effect : effects.effects) {
        if (effect.trigger == Effect.Trigger.WEAR) {
          effect.act(entity, entity);
        }
      }
    }

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

      WorldManager.log.add("inventory.ate", itemDetails.name);

      EffectsComponent itemEffects = ComponentMappers.effects.get(item);

      if (itemEffects != null) {
        for (Effect effect : itemEffects.effects) {
          if (effect.trigger == Effect.Trigger.CONSUME) {
            effect.act(entity, entity);
          }
        }
      }

      if (Objects.equals(itemDetails.type, "corpse")
          || Objects.equals(itemDetails.type, "limb")) {
        if (WorldManager.entityHelpers.hasTrait(entity, "Carnivore")) {
          if (MathUtils.random() > 0.5) {
            WorldManager.entityHelpers.raiseHealth(entity, MathUtils.random(5, 20));
          }
        } else {
          if (MathUtils.random() > 0.75) {
            entity.add(new SickComponent(MathUtils.random(5, 20)));

            if (ComponentMappers.player.has(entity)) {
              WorldManager.log.add("effects.sick.started", "You", "are");
            } else {
              AttributesComponent attributes = ComponentMappers.attributes.get(entity);

              WorldManager.log.add("effects.sick.started", attributes.name, "is");
            }
          }
        }

        AttributesComponent.Type type = ComponentMappers.corpse.has(item)
            ? ComponentMappers.corpse.get(item).type
            : ComponentMappers.limb.get(item).type;

        if (ComponentMappers.god.get(WorldManager.god).likes.contains("Eating animals you kill")
            && type == AttributesComponent.Type.ANIMAL) {
          AttributesComponent attributes = ComponentMappers.attributes.get(entity);

          int amount = MathUtils.random(1, 5);

          if (attributes.divineFavor <= (attributes.divineFavor + amount)) {
            attributes.divineFavor += amount;

            WorldManager.log.add(
                "attributes.divineFavor.increased",
                ComponentMappers.god.get(WorldManager.god).name
            );
          }
        }
      }

      PlayerComponent player = ComponentMappers.player.get(entity);

      if (player != null && !player.identifiedItems.contains(itemDetails.name, true)) {
        player.identifiedItems.add(itemDetails.name);
      }

      drop(entity, item, true);
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
      EffectsComponent applyingItemEffects = ComponentMappers.effects.get(applyingItem);
      EffectsComponent targetItemEffects = ComponentMappers.effects.get(targetItem);

      if (targetItemEffects == null) {
        targetItem.add(new EffectsComponent());
      }

      if (targetItemEffects != null) {
        for (Effect effect : applyingItemEffects.effects) {
          if (effect.trigger == Effect.Trigger.APPLY) {
            targetItemEffects.effects.add(effect);
          }
        }
      }

      drop(entity, applyingItem, true);
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

    if (ComponentMappers.effects.has(item)) {
      EffectsComponent effects = ComponentMappers.effects.get(item);

      for (Effect effect : effects.effects) {
        if (effect.trigger == Effect.Trigger.WEAR) {
          effect.revoke(entity, entity);
        }
      }
    }
  }

  /**
   * Drop an item to the ground.
   *
   * @param entity   Whose shit we're dropping
   * @param item     What we're dropping
   * @param position Where we gonna drop it
   */
  public void drop(Entity entity, Entity item, Vector2 position, boolean destroy) {
    if (ComponentMappers.inventory.has(entity)) {
      if (isEquipped(entity, item)) {
        remove(entity, item);
      }

      removeFromInventory(entity, item);

      WorldManager.entityHelpers.updatePosition(item, position.x, position.y);

      if (ComponentMappers.effects.has(item)) {
        for (Effect effect : ComponentMappers.effects.get(item).effects) {
          if (effect.trigger == Effect.Trigger.DROP) {
            effect.act(item, item);
          }
        }
      }

      if (!destroy && ComponentMappers.player.has(entity)) {
        WorldManager.log.add("inventory.dropped", WorldManager.itemHelpers.getName(entity, item));
      }

      if (destroy) {
        removeFromInventory(entity, item);
        WorldManager.world.removeEntity(item);
      }
    }
  }

  /**
   * This seems mostly unnecessary. Drop it at their feet.
   *
   * @param entity Whose feet we're dropping shit on
   * @param item   What we're dropping
   */
  public void drop(Entity entity, Entity item, boolean destroy) {
    if (ComponentMappers.inventory.has(entity)) {
      drop(entity, item, ComponentMappers.position.get(entity).pos, destroy);
    }
  }

  /**
   * Check if an item is equipped.
   *
   * @param entity The entity whose equipment we're checking
   * @param item   The item we want to check
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
   * Get total weight of all items carried.
   *
   * @param entity The entity we're getting the inventory weight of
   * @return Weight
   */
  public float getTotalWeight(Entity entity) {
    InventoryComponent inventory = ComponentMappers.inventory.get(entity);

    float total = 0;

    for (Entity item : inventory.items) {
      total += ComponentMappers.item.get(item).weight;
    }

    return total;
  }

  /**
   * Iterate through inventory, return whatever item has the `throwing` flag set to true.
   *
   * @param entity Entity whose throwing actions
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
   * @param entity The entity who's equipped the item
   * @param item   The item we want to check
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
   * Find out if an entity has the ammunition it needs.
   *
   * @param entity Who needs to know
   * @param type   Type of ammunition they're looking for
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
   * Return the first item of ammunition type given.
   *
   * @param entity Entity whose inventory we're looking at
   * @param type   Type of ammunition we're looking for
   * @return An ammunition item
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
