package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class InventoryHelpers {
  private static final ArrayList<String> letters = new ArrayList<>(
      Arrays.asList("a", "c", "d", "e", "f", "g", "i", "m", "o", "p", "r", "s", "v", "w", "x")
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
   * If inventory not full, assign the item a letter, remove it's visual components, and attach it.
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
  public void removeItem(Entity item) {
    if (item != null) {
      main.player.getComponent(InventoryComponent.class).items.remove(item);
    }
  }

  /**
   * Drop an item at the given location (usually entity position).
   *
   * @param position The cell to drop it to
   */
  public void dropItem(Entity item, Vector2 position) {
    letters.add(item.getComponent(ItemComponent.class).identifier);

    item.getComponent(ItemComponent.class).identifier = null;
    item.add(new PositionComponent(position));

    main.player.getComponent(InventoryComponent.class).items.remove(item);
  }

  public void dropItem(Entity item) {
    dropItem(item, main.player.getComponent(PositionComponent.class).pos);
  }

  /**
   * Wield an item.
   *
   * @param entity The item to unwield
   */
  public void wieldItem(Entity entity) {
    main.player.getComponent(EquipmentComponent.class).rightHand = entity;

    removeItem(entity);
  }

  /**
   * Unwield an item.
   *
   * @param entity The item to unwield
   */
  public void unwieldItem(Entity entity) {
    main.player.getComponent(EquipmentComponent.class).rightHand = null;

    addItem(entity);
  }

  /**
   * Get item the player is currently throwing.
   *
   * @return The entity
   */
  public Entity getThrowingItem() {
    ArrayList<Entity> items = main.player.getComponent(InventoryComponent.class).items;

    for (Entity entity : items) {
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (item.throwing) {
        return entity;
      }
    }

    return null;
  }
}
