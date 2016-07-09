package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.ArrayList;
import java.util.HashMap;

public class World {
  public final ArrayList<Map> maps;
  public final HashMap<Integer, Array<Entity>> entities;
  public long seed;
  public int currentMapIndex = 0;

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

  public void setup() {
    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);
    }

    PositionComponent position = ComponentMappers.position.get(WorldManager.player);
    position.pos = WorldManager.mapHelpers.getEntrancePosition();
  }

  public void goDown() {
    entities.get(currentMapIndex + 1).add(WorldManager.player);
    entities.get(currentMapIndex).removeValue(WorldManager.player, true);

    currentMapIndex += 1;

    WorldManager.engine.removeAllEntities();
    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);
    }

    WorldManager.player.remove(MouseMovementComponent.class);
    PositionComponent position = ComponentMappers.position.get(WorldManager.player);
    position.pos = WorldManager.mapHelpers.getEntrancePosition();

    WorldManager.state = WorldManager.State.PLAYING;
  }

  public void goUp() {
    entities.get(currentMapIndex - 1).add(WorldManager.player);
    entities.get(currentMapIndex).removeValue(WorldManager.player, true);

    currentMapIndex -= 1;

    WorldManager.engine.removeAllEntities();
    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);
    }

    WorldManager.player.remove(MouseMovementComponent.class);
    PositionComponent position = ComponentMappers.position.get(WorldManager.player);
    position.pos = WorldManager.mapHelpers.getExitPosition();

    WorldManager.state = WorldManager.State.PLAYING;
  }
}
