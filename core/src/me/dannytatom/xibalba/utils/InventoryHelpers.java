package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;

import java.util.ArrayList;

public class InventoryHelpers {
  public InventoryHelpers() {

  }

  public boolean addItem(Entity entity, Entity item) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);

      if (inventoryComponent.items.size() == 10) {
        return false;
      } else {
        item.remove(PositionComponent.class);
        inventoryComponent.items.add(item);
      }
    }

    return true;
  }

  public void removeItem(Entity entity, Entity item) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);

      if (item != null) {
        inventoryComponent.items.remove(item);
      }
    }
  }

  public void dropItem(Entity entity, Entity item, Vector2 position) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);

      item.add(new PositionComponent(position));
      inventoryComponent.items.remove(item);
    }
  }

  public void dropItem(Entity entity, Entity item) {
    if (entity.getComponent(InventoryComponent.class) != null) {
      dropItem(entity, item, entity.getComponent(PositionComponent.class).pos);
    }
  }

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
}
