package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class InventoryHelpers {
  public static ArrayList<String> letters = new ArrayList<>(
      Arrays.asList("a", "c", "d", "f", "g", "i", "m", "o", "p", "q", "r", "s", "t", "v", "w", "x")
  );
  private Entity player;

  public InventoryHelpers(Entity player) {
    this.player = player;
  }

  public void addItem(Entity thing) {
    int rand = MathUtils.random(0, letters.size() - 1);

    thing.remove(VisualComponent.class);
    thing.remove(PositionComponent.class);

    player.getComponent(InventoryComponent.class).items.add(thing);
    thing.getComponent(ItemComponent.class).identifier = letters.get(rand);
    letters.remove(rand);
  }

  public Entity findItem(int keycode) {
    for (Entity item : player.getComponent(InventoryComponent.class).items) {
      if (Objects.equals(item.getComponent(ItemComponent.class).identifier.toUpperCase(), Input.Keys.toString(keycode))) {
        return item;
      }
    }

    return null;
  }

  public void toggleShowItem(Entity entity) {
    if (entity.getComponent(ItemComponent.class).lookingAt) {
      entity.getComponent(ItemComponent.class).lookingAt = false;
    } else {

      for (Entity item : player.getComponent(InventoryComponent.class).items) {
        item.getComponent(ItemComponent.class).lookingAt = false;
      }

      entity.getComponent(ItemComponent.class).lookingAt = true;
    }
  }

  public void wieldItem() {
    ArrayList<Entity> items = player.getComponent(InventoryComponent.class).items;
    Entity showing = null;

    for (Entity entity : items) {
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (item.lookingAt && item.actions.get("canWield")) {
        showing = entity;

        for (Entity other : player.getComponent(InventoryComponent.class).items) {
          ItemComponent otherItem = other.getComponent(ItemComponent.class);

          if (Objects.equals(item.type, otherItem.type) && otherItem.equipped) {
            otherItem.equipped = false;
          }
        }

        break;
      }
    }

    if (showing != null) {
      showing.getComponent(ItemComponent.class).equipped = true;
    }
  }
}
