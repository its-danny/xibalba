package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.ArrayList;
import java.util.HashMap;

public class World {
  public final ArrayList<Map> maps;
  public final HashMap<Integer, Array<Entity>> entities;
  public int currentMapIndex = 0;

  /**
   * Instantiate some shit.
   */
  public World() {
    maps = new ArrayList<>();
    entities = new HashMap<>();
  }

  public Map getCurrentMap() {
    return maps.get(currentMapIndex);
  }

  public Map getMap(int index) {
    return maps.get(index);
  }

  public void addEntity(Entity entity) {
    entities.get(WorldManager.world.currentMapIndex).add(entity);
    WorldManager.engine.addEntity(entity);
  }

  public void removeEntity(Entity entity) {
    entities.get(WorldManager.world.currentMapIndex).removeValue(entity, true);
    WorldManager.engine.removeEntity(entity);
  }

  /**
   * Setup starting level.
   */
  public void setup() {
    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);
    }
  }

  /**
   * Go down a level.
   */
  public void goDown() {
    entities.get(currentMapIndex + 1).add(WorldManager.player);
    entities.get(currentMapIndex).removeValue(WorldManager.player, true);

    currentMapIndex += 1;

    PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);
    if (currentMapIndex > playerDetails.lowestDepth) {
      playerDetails.lowestDepth = currentMapIndex;
    }

    WorldManager.engine.removeAllEntities();
    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);
    }

    WorldManager.player.remove(MouseMovementComponent.class);
    WorldManager.entityHelpers.updatePosition(
        WorldManager.player, WorldManager.mapHelpers.getEntrancePosition()
    );

    WorldManager.state = WorldManager.State.PLAYING;
  }

  /**
   * Go up a level.
   */
  public void goUp() {
    entities.get(currentMapIndex - 1).add(WorldManager.player);
    entities.get(currentMapIndex).removeValue(WorldManager.player, true);

    currentMapIndex -= 1;

    WorldManager.engine.removeAllEntities();
    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);
    }

    WorldManager.player.remove(MouseMovementComponent.class);
    WorldManager.entityHelpers.updatePosition(
        WorldManager.player, WorldManager.mapHelpers.getExitPosition()
    );

    WorldManager.state = WorldManager.State.PLAYING;
  }
}
