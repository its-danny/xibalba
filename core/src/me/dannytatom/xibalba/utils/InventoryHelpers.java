package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;

import java.util.ArrayList;
import java.util.Objects;

public class InventoryHelpers {
  private final Main main;

  public InventoryHelpers(Main main) {
    this.main = main;
  }

  /**
   * Add an item to an entity's inventory.
   *
   * @param entity Entity we wanna to give shit to
   * @param item   The item itself
   */
  public void addItem(Entity entity, Entity item) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);

      if (inventoryComponent.items.size() < 10) {
        item.remove(PositionComponent.class);
        inventoryComponent.items.add(item);

        EquipmentComponent equipmentComponent = entity.getComponent(EquipmentComponent.class);
        ItemComponent itemComponent = item.getComponent(ItemComponent.class);

        main.log.add("You picked up a " + itemComponent.name);

        if (Objects.equals(itemComponent.type, "weapon")
            && equipmentComponent.slots.get("rightHand") == null) {
          main.equipmentHelpers.holdItem(entity, item);
          main.log.add("You are now holding a " + itemComponent.name);
        }
      }
    }
  }

  /**
   * It's like drop except we remove it 100%.
   *
   * @param entity Entity we want to take shit from
   * @param item   The shit we gonna take
   */
  public void removeItem(Entity entity, Entity item) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);

      if (item != null) {
        inventoryComponent.items.remove(item);
        main.engine.removeEntity(item);
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
  public void dropItem(Entity entity, Entity item, Vector2 position) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);

      if (main.equipmentHelpers.isEquipped(entity, item)) {
        main.equipmentHelpers.removeItem(entity, item);
      }

      item.add(new PositionComponent(main.world.currentMapIndex, position));
      inventoryComponent.items.remove(item);

      if (entity.getComponent(PlayerComponent.class) != null) {
        main.log.add("You dropped a " + item.getComponent(ItemComponent.class).name);
      }
    }
  }

  /**
   * This seems mostly unnecessary. Drop it at their feet.
   *
   * @param entity Whose feet we're dropping shit on
   * @param item   What we're dropping
   */
  public void dropItem(Entity entity, Entity item) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      dropItem(entity, item, entity.getComponent(PositionComponent.class).pos);
    }
  }

  /**
   * Iterate through inventory, return whatever item has the `throwing` flag set to true.
   *
   * @param entity Entity whose throwing things
   *
   * @return The item being thrown
   */
  public Entity getThrowingItem(Entity entity) {
    ArrayList<Entity> items = entity.getComponent(InventoryComponent.class).items;

    for (Entity item : items) {
      ItemComponent itemComponent = item.getComponent(ItemComponent.class);

      if (itemComponent.throwing) {
        return item;
      }
    }

    return null;
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
    ArrayList<Entity> items = entity.getComponent(InventoryComponent.class).items;

    for (Entity item : items) {
      ItemComponent itemComponent = item.getComponent(ItemComponent.class);

      if (Objects.equals(itemComponent.type, type)) {
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
   *
   * @return Some (or, well, 1) ammunition
   */
  public Entity getAmmunitionOfType(Entity entity, String type) {
    ArrayList<Entity> items = entity.getComponent(InventoryComponent.class).items;

    for (Entity item : items) {
      ItemComponent itemComponent = item.getComponent(ItemComponent.class);

      if (Objects.equals(itemComponent.type, type)) {
        return item;
      }
    }

    return null;
  }
}
