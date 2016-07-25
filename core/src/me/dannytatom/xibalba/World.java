package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.map.ShadowCaster;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.ArrayList;
import java.util.HashMap;

public class World {
  public final ArrayList<Map> maps;
  public final HashMap<Integer, Array<Entity>> entities;
  private final ShadowCaster caster;
  public long seed;
  public int currentMapIndex = 0;

  /**
   * Instantiate some shit.
   */
  public World() {
    maps = new ArrayList<>();
    entities = new HashMap<>();
    caster = new ShadowCaster();
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

    WorldManager.entityHelpers.updatePosition(
        WorldManager.player, WorldManager.mapHelpers.getEntrancePosition()
    );

    PositionComponent position = ComponentMappers.position.get(WorldManager.player);
    updateLighting(position.pos.x, position.pos.y);
  }

  /**
   * Go down a level.
   */
  public void goDown() {
    entities.get(currentMapIndex + 1).add(WorldManager.player);
    entities.get(currentMapIndex).removeValue(WorldManager.player, true);

    currentMapIndex += 1;

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
        WorldManager.player, WorldManager.mapHelpers.getEntrancePosition()
    );

    WorldManager.state = WorldManager.State.PLAYING;
  }

  /**
   * Update lighting based on position.
   *
   * @param positionX X cell at center of lighting
   * @param positionY Y cell at center of lighting
   */
  public void updateLighting(float positionX, float positionY) {
    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);

    Map map = getCurrentMap();

    map.lightMap = caster.calculateFov(
        WorldManager.mapHelpers.createFovMap(),
        (int) positionX, (int) positionY, attributes.vision
    );

    for (int x = 0; x < map.lightMap.length; x++) {
      for (int y = 0; y < map.lightMap[0].length; y++) {
        Cell cell = WorldManager.mapHelpers.getCell(x, y);
        float alpha = map.lightMap[x][y] <= 0.15f ? 0.15f : map.lightMap[x][y];
        cell.sprite.setAlpha(alpha);

        ArrayList<Entity> entities = WorldManager.entityHelpers.getEntitiesAt(new Vector2(x, y));

        for (Entity entity : entities) {
          ComponentMappers.visual.get(entity).sprite.setAlpha(alpha);
        }
      }
    }
  }
}
