package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.screens.DepthScreen;
import me.dannytatom.xibalba.screens.PlayScreen;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class World {
  public final ArrayList<Map> maps;
  public final HashMap<Integer, Array<Entity>> entities;
  public int currentMapIndex = 0;
  private Main main;

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
  public void setup(Main main) {
    this.main = main;

    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);

      if (ComponentMappers.attributes.has(entity)) {
        WorldManager.entityHelpers.updateSenses(entity);
      }
    }
  }

  private void changeDepth(int change) {
    Main.playScreen.dispose();
    main.setScreen(new DepthScreen());

    entities.get(currentMapIndex).removeValue(WorldManager.player, true);
    entities.get(currentMapIndex + change).add(WorldManager.player);

    currentMapIndex += change;

    PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);
    if (currentMapIndex > playerDetails.lowestDepth) {
      playerDetails.lowestDepth = currentMapIndex;
    }

    WorldManager.engine.removeAllEntities();

    for (Entity entity : entities.get(currentMapIndex)) {
      WorldManager.engine.addEntity(entity);

      if (ComponentMappers.attributes.has(entity)) {
        if (ComponentMappers.player.has(entity)) {
          entity.remove(MouseMovementComponent.class);

          Vector2 position = change > 0
              ? WorldManager.world.getCurrentMap().entrance
              : WorldManager.world.getCurrentMap().exit;

          WorldManager.entityHelpers.updatePosition(entity, position.x, position.y);
          WorldManager.entityHelpers.updateSprite(entity, position.x, position.y);
        }

        WorldManager.entityHelpers.updateSenses(entity);
      }
    }

    Main.playScreen = new PlayScreen(main);
    main.setScreen(Main.playScreen);
  }

  /**
   * Go down a level.
   */
  public void goDown() {
    changeDepth(1);
  }

  /**
   * Go up a level.
   */
  public void goUp() {
    changeDepth(-1);
  }
}
