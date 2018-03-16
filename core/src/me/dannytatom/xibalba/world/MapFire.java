package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.LightComponent;
import me.dannytatom.xibalba.components.PositionComponent;

public class MapFire {
  private Map map;
  private MapCell.Type[][] flooded;
  private int floodedCount = 0;
  private int lastX;
  private int lastY;
  private float animCounter = 0;

  /**
   * Handles a single fire on the map.
   *
   * @param mapIndex         The map
   * @param startingPosition The starting position
   */
  public MapFire(int mapIndex, Vector2 startingPosition) {
    map = WorldManager.world.getMap(mapIndex);
    flooded = new MapCell.Type[map.width][map.height];
    lastX = Math.round(startingPosition.x);
    lastY = Math.round(startingPosition.y);

    for (MapCell.Type[] row : flooded) {
      Arrays.fill(row, MapCell.Type.WALL);
    }

    flood(Math.round(startingPosition.x), Math.round(startingPosition.y));
  }

  /**
   * Update FIRES.
   *
   * @param delta Time since last frame
   * @param flood Keep spreading?
   */
  public void update(float delta, boolean flood) {
    animCounter += delta;

    if (floodedCount < MathUtils.random(100, 300)) {
      if (flood) {
        flood(lastX + MathUtils.random(0, 2), lastY);
        flood(lastX - MathUtils.random(0, 2), lastY);
        flood(lastX, lastY + MathUtils.random(0, 2));
        flood(lastX, lastY - MathUtils.random(0, 2));
      }
    }

    if (animCounter >= .5f) {
      animCounter = 0;

      for (int x = 0; x < flooded.length; x++) {
        for (int y = 0; y < flooded[0].length; y++) {
          if (flooded[x][y] == MapCell.Type.FLOOR) {
            MapCell cell = map.getCellMap()[x][y];
            cell.description = "fire";

            String spriteKey = MathUtils.random() > 0.5 ? "1405" : "1407";
            Sprite sprite = Main.asciiAtlas.createSprite(spriteKey);
            sprite.setColor(Colors.get("fire-" + MathUtils.random(1, 3)));
            cell.sprite.set(sprite);
            cell.sprite.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);

            if (!cell.onFire) {
              ArrayList<Color> fireColors = new ArrayList<>();
              fireColors.add(Colors.get("fire-1"));
              fireColors.add(Colors.get("fire-2"));
              fireColors.add(Colors.get("fire-3"));

              Entity fireLight = new Entity();
              fireLight.add(
                  new LightComponent(MathUtils.random(1, 3), true, fireColors)
              );
              fireLight.add(new PositionComponent(x, y));
              WorldManager.world.addEntity(fireLight);
            }

            cell.onFire = true;
          }
        }
      }
    }
  }

  private void flood(int cellX, int cellY) {
    if (map.geometry.length > cellX && map.geometry[cellX].length > cellY
        && map.geometry[cellX][cellY] == MapCell.Type.FLOOR
        && flooded[cellX][cellY] == MapCell.Type.WALL) {
      flooded[cellX][cellY] = MapCell.Type.FLOOR;
      floodedCount += 1;

      lastX = cellX;
      lastY = cellY;
    }
  }
}
