package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class InventoryHelpers {
  private static final ArrayList<String> letters = new ArrayList<>(
      Arrays.asList("a", "c", "f", "g", "i", "m", "o", "p", "r", "s", "t", "v", "w", "x")
  );
  private final Entity player;

  public InventoryHelpers(Entity player) {
    this.player = player;
  }

  public Entity findItem(int keycode) {
    for (Entity item : player.getComponent(InventoryComponent.class).items) {
      if (Objects.equals(item.getComponent(ItemComponent.class).identifier.toUpperCase(), Input.Keys.toString(keycode))) {
        return item;
      }
    }

    return null;
  }

  public void showItem(Entity entity) {
    for (Entity item : player.getComponent(InventoryComponent.class).items) {
      item.getComponent(ItemComponent.class).lookingAt = false;
    }

    entity.getComponent(ItemComponent.class).lookingAt = true;
  }

  public void closeInventory() {
    for (Entity item : player.getComponent(InventoryComponent.class).items) {
      item.getComponent(ItemComponent.class).lookingAt = false;
    }
  }

  Entity getShowing() {
    ArrayList<Entity> items = player.getComponent(InventoryComponent.class).items;
    Entity showing = null;

    for (Entity entity : items) {
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (item.lookingAt) {
        showing = entity;

        break;
      }
    }

    return showing;
  }

  public boolean addItem(Entity thing) {
    if (player.getComponent(InventoryComponent.class).items.size() == 10) {
      return false;
    }

    int rand = MathUtils.random(0, letters.size() - 1);

    thing.remove(PositionComponent.class);

    player.getComponent(InventoryComponent.class).items.add(thing);
    thing.getComponent(ItemComponent.class).identifier = letters.get(rand);
    letters.remove(rand);

    return true;
  }

  public void dropItem() {
    Entity item = getShowing();

    if (item != null) {
      letters.add(item.getComponent(ItemComponent.class).identifier);

      item.getComponent(ItemComponent.class).equipped = false;
      item.getComponent(ItemComponent.class).lookingAt = false;
      item.getComponent(ItemComponent.class).identifier = null;
      item.add(new PositionComponent(player.getComponent(PositionComponent.class).pos));

      player.getComponent(InventoryComponent.class).items.remove(item);
    }
  }

  public void wieldItem() {
    Entity entity = getShowing();

    if (entity != null) {
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (item.actions.get("canWield")) {
        ArrayList<Entity> others = player.getComponent(InventoryComponent.class).items;

        for (Entity other : others) {
          other.getComponent(ItemComponent.class).equipped = false;
        }

        item.equipped = true;
        item.lookingAt = false;
      }
    }
  }
}
