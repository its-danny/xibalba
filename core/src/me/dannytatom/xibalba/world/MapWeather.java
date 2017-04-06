package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.RainDropComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MapWeather {
  private final int mapIndex;
  private final ImmutableArray<Entity> rainDrops;
  private final Sprite falling;
  private final Sprite splash;
  private final Sprite fading;
  private float animCounter = 0;
  private float windStartCounter = 10;
  private boolean windBlowing = false;
  private Vector2[][] windPath;
  private int windPathIndex;

  /**
   * Rainfall in the forest.
   * </p>
   * Generate 250 rain drops in random positions on the map.
   *
   * @param mapIndex The map we're working on
   */
  public MapWeather(int mapIndex) {
    this.mapIndex = mapIndex;

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
    windStartCounter += delta;

    if (windStartCounter >= 10 && !windBlowing) {
      windStartCounter = 0;
      windBlowing = true;

      Vector2 windStart = WorldManager.mapHelpers.getRandomOpenPosition(mapIndex);
      int windThickness = MathUtils.random(5, 10);
      int windDistance = MathUtils.random(10, 30);

      windPathIndex = 0;
      windPath = new Vector2[windDistance][windThickness];

      for (int x = 0; x < windDistance; x++) {
        for (int y = 0; y < windThickness; y++) {
          windPath[x][y] = new Vector2(windStart.x - x, windStart.y + y);
        }
      }
    }

    animCounter += delta;

    if (animCounter >= .10f) {
      animCounter = 0;

      if (windPathIndex > 0) {
        Vector2[] path = windPath[windPathIndex - 1];

        for (Vector2 position : path) {
          if (WorldManager.mapHelpers.cellExists(position)) {
            MapCell cell = WorldManager.mapHelpers.getCell(mapIndex, position);

            if (cell.isFloor()) {
              cell.sprite.setColor(cell.color);
            }
          }
        }
      }

      if (windBlowing) {
        Vector2[] path = windPath[windPathIndex];

        for (Vector2 position : path) {
          if (WorldManager.mapHelpers.cellExists(position)) {
            MapCell cell = WorldManager.mapHelpers.getCell(mapIndex, position);

            if (cell.isFloor()) {
              Color color = cell.sprite.getColor().cpy().lerp(Color.WHITE, .75f);

              cell.sprite.setColor(color);
              cell.sprite.setFlip(false, false);
            }
          }
        }

        if (windPathIndex == windPath.length - 1) {
          windBlowing = false;
        } else {
          windPathIndex++;
        }
      }

      for (Entity drop : rainDrops) {
        RainDropComponent stats = ComponentMappers.rainDrop.get(drop);
        PositionComponent position = ComponentMappers.position.get(drop);
        VisualComponent visual = ComponentMappers.visual.get(drop);

        if (stats.life == 1) {
          Vector2 newPosition = WorldManager.mapHelpers.getRandomOpenPosition();

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
