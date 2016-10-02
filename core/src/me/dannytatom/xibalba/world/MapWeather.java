package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.RainDropComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class MapWeather {
  private ArrayList<Entity> rainDrops;
  private int maxDrops;
  private float counter = 0;

  private Sprite splash;
  private Sprite fading;

  public MapWeather() {
    rainDrops = new ArrayList<>();
    maxDrops = 400;

    splash = Main.asciiAtlas.createSprite("0900");
    splash.setColor(Colors.get("CYAN"));
    fading = Main.asciiAtlas.createSprite("0700");
  }

  public void update(float delta) {
    if (Objects.equals(WorldManager.world.getCurrentMap().type, "forest")) {
      counter += delta;

      if (rainDrops.size() < maxDrops) {
        int create = MathUtils.random(0, maxDrops - rainDrops.size());

        for (int i = 0; i < create; i++) {
          Entity drop = WorldManager.entityFactory.createRainDrop(WorldManager.mapHelpers.getRandomOpenPosition());
          WorldManager.world.addEntity(drop);
          rainDrops.add(drop);
        }
      }

      if (counter >= .10f) {
        counter = 0;

        Iterator<Entity> iterator = rainDrops.iterator();

        while (iterator.hasNext()) {
          Entity drop = iterator.next();

          RainDropComponent stats = ComponentMappers.rainDrop.get(drop);
          PositionComponent position = ComponentMappers.position.get(drop);
          VisualComponent visual = ComponentMappers.visual.get(drop);

          if (stats.life <= 3) {
            Vector2 newPosition = new Vector2(position.pos.x - 1, position.pos.y - 1);

            if (WorldManager.mapHelpers.isBlocked(newPosition)) {
              stats.life = 4;
            } else {
              WorldManager.entityHelpers.updatePosition(drop, newPosition);
              WorldManager.entityHelpers.updateSpritePosition(drop, newPosition);

              stats.life += 1;
            }
          } else if (stats.life == 4) {
            visual.sprite.set(splash);
            WorldManager.entityHelpers.updateSpritePosition(drop, position.pos);

            stats.life += 1;
          } else if (stats.life == 5) {
            visual.sprite.set(fading);
            WorldManager.entityHelpers.updateSpritePosition(drop, position.pos);
            WorldManager.mapHelpers.makeFloorWet(position.pos);

            stats.life += 1;
          } else if (stats.life == 6) {
            WorldManager.world.removeEntity(drop);
            iterator.remove();
          }
        }
      }
    } else {
      if (rainDrops.size() > 0) {
        rainDrops.isEmpty();
      }
    }
  }
}
