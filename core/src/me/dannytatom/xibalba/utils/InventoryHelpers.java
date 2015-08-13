package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class InventoryHelpers {
  private static final ArrayList<String> letters = new ArrayList<>(
      Arrays.asList("a", "c", "f", "g", "i", "m", "o", "p", "r", "v", "w", "x")
  );

  private final Main main;

  public InventoryHelpers(Main main) {
    this.main = main;
  }

  /**
   * Find item from keyCode.
   *
   * @param keycode They key pressed
   * @return Either the item or null
   */
  public Entity findItem(int keycode) {
    for (Entity item : main.player.getComponent(InventoryComponent.class).items) {
      if (Objects.equals(item.getComponent(ItemComponent.class).identifier.toUpperCase(),
          Input.Keys.toString(keycode))) {
        return item;
      }
    }

    return null;
  }

  /**
   * Set the item's lookingAt property to true.
   *
   * @param entity The item
   */
  public void showItem(Entity entity) {
    for (Entity item : main.player.getComponent(InventoryComponent.class).items) {
      item.getComponent(ItemComponent.class).lookingAt = false;
    }

    entity.getComponent(ItemComponent.class).lookingAt = true;
  }

  /**
   * Hide all items by setting their lookingAt attributes to false.
   */
  public void hideItems() {
    for (Entity item : main.player.getComponent(InventoryComponent.class).items) {
      item.getComponent(ItemComponent.class).lookingAt = false;
    }
  }

  /**
   * Toggle item visibility.
   *
   * @param entity The item to toggle
   */
  public void toggleItem(Entity entity) {
    if (entity.getComponent(ItemComponent.class).lookingAt) {
      hideItems();
    } else {
      showItem(entity);
    }
  }

  /**
   * If inventory not full, assign the item a letter,
   * remove it's visual components, and attach it.
   *
   * @param thing The item to add
   * @return True if added, false if inventory full
   */
  public boolean addItem(Entity thing) {
    if (main.player.getComponent(InventoryComponent.class).items.size() == 10) {
      return false;
    }

    int rand = MathUtils.random(0, letters.size() - 1);

    thing.remove(PositionComponent.class);

    main.player.getComponent(InventoryComponent.class).items.add(thing);
    thing.getComponent(ItemComponent.class).identifier = letters.get(rand);
    letters.remove(rand);

    return true;
  }

  /**
   * Remove an item from inventory.
   */
  public void removeItem() {
    Entity item = getShowing();

    if (item != null) {
      main.player.getComponent(InventoryComponent.class).items.remove(item);
    }
  }

  /**
   * Drop an item at the given location (usually entity position).
   *
   * @param position The cell to drop it to
   */
  public void dropItem(Vector2 position) {
    Vector2 pos = position;
    Entity item = getShowing();

    if (pos == null) {
      pos = main.player.getComponent(PositionComponent.class).pos;
    }

    if (item != null) {
      letters.add(item.getComponent(ItemComponent.class).identifier);

      item.getComponent(ItemComponent.class).equipped = false;
      item.getComponent(ItemComponent.class).lookingAt = false;
      item.getComponent(ItemComponent.class).identifier = null;
      item.add(new PositionComponent(pos));

      main.player.getComponent(InventoryComponent.class).items.remove(item);
    }
  }

  /**
   * Wield an item then stop looking at it.
   */
  public void wieldItem() {
    Entity entity = getShowing();

    if (entity != null) {
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (item.actions.get("canWield")) {
        ArrayList<Entity> others = main.player.getComponent(InventoryComponent.class).items;

        for (Entity other : others) {
          other.getComponent(ItemComponent.class).equipped = false;
        }

        item.equipped = true;
        item.lookingAt = false;
      }
    }
  }

  /**
   * Get a list of wielded items.
   *
   * @return Array of items or null if nothing is wielded.
   */
  public Entity getWieldedItem() {
    ArrayList<Entity> items = main.player.getComponent(InventoryComponent.class).items;

    for (Entity entity : items) {
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (item.actions.get("canWield") && item.equipped) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get showing item.
   *
   * @return The item they're looking at, null if nothing
   */
  public Entity getShowing() {
    ArrayList<Entity> items = main.player.getComponent(InventoryComponent.class).items;

    for (Entity entity : items) {
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (item.lookingAt) {
        return entity;
      }
    }

    return null;
  }
}
