package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.RainDropComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MapWeather {
  private final ImmutableArray<Entity> rainDrops;
  private final Sprite falling;
  private final Sprite splash;
  private final Sprite fading;
  private float animCounter = 0;

  /**
   * Rainfall in the forest.
   *
   * <p>Generate 250 rain drops in random positions on the map.
   *
   * @param mapIndex The map we're working on
   */
  public MapWeather(int mapIndex) {
    for (int i = 0; i < 250; i++) {
      Entity drop = WorldManager.entityFactory.createRainDrop();
      WorldManager.world.entities.get(mapIndex).add(drop);
    }

    rainDrops = WorldManager.engine.getEntitiesFor(Family.all(RainDropComponent.class).get());

    falling = Main.asciiAtlas.createSprite("1502");
    falling.setColor(Colors.get("CYAN"));
    splash = Main.asciiAtlas.createSprite("0700");
    splash.setColor(Colors.get("CYAN"));
    fading = Main.asciiAtlas.createSprite("0900");
  }

  /**
   * Update weather things.
   *
   * @param delta Time since last frame
   */
  public void update(float delta) {
    animCounter += delta;

    if (animCounter >= .10f) {
      animCounter = 0;

      for (Entity drop : rainDrops) {
        RainDropComponent stats = ComponentMappers.rainDrop.get(drop);
        PositionComponent position = ComponentMappers.position.get(drop);
        VisualComponent visual = ComponentMappers.visual.get(drop);

        if (stats.life == 1) {
          Vector2 newPosition = WorldManager.mapHelpers.getRandomOpenPositionOnLand();

          visual.sprite.set(falling);
          WorldManager.entityHelpers.updatePosition(drop, newPosition.x, newPosition.y);
          WorldManager.entityHelpers.updateSprite(drop, newPosition.x, newPosition.y);

          stats.life += 1;
        } else if (stats.life >= 2 && stats.life <= 4) {
          Vector2 newPosition = new Vector2(position.pos.x - 1, position.pos.y - 1);

          if (WorldManager.mapHelpers.isBlocked(newPosition)) {
            stats.life = 5;
          } else {
            WorldManager.entityHelpers.updatePosition(drop, newPosition.x, newPosition.y);
            WorldManager.entityHelpers.updateSprite(drop, newPosition.x, newPosition.y);

            stats.life += 1;
          }
        } else if (stats.life == 5) {
          visual.sprite.set(splash);
          WorldManager.entityHelpers.updateSprite(drop, position.pos.x, position.pos.y);

          stats.life += 1;
        } else if (stats.life == 6) {
          visual.sprite.set(fading);
          WorldManager.entityHelpers.updateSprite(drop, position.pos.x, position.pos.y);
          WorldManager.mapHelpers.makeFloorWet(position.pos);

          stats.life += 1;
        } else if (stats.life == 7) {
          stats.life = 1;
        }
      }
    }
  }
}
