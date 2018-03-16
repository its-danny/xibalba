package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

import me.dannytatom.xibalba.components.LightComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MapLight {
  private final int mapIndex;
  private final ShadowCaster caster;
  private final Family family;
  public float[][] lightMap;
  public Color[][] colorMap;
  private float counter = 0;

  /**
   * Handles light sources on the map.
   *
   * @param mapIndex The map
   */
  public MapLight(int mapIndex) {
    this.mapIndex = mapIndex;
    this.caster = new ShadowCaster();
    this.family = Family.all(LightComponent.class, PositionComponent.class).get();

    Map map = WorldManager.world.getMap(mapIndex);
    this.lightMap = new float[map.width][map.height];
    for (float[] row : lightMap) {
      Arrays.fill(row, 0f);
    }

    this.colorMap = new Color[map.width][map.height];
    for (Color[] row : colorMap) {
      Arrays.fill(row, Color.BLACK);
    }
  }

  public boolean hasLights() {
    return WorldManager.engine.getEntitiesFor(family).size() > 0;
  }

  /**
   * Update light sources.
   *
   * @param delta Time since last frame
   */
  public void update(float delta) {
    counter += delta;

    float[][] fovMap = WorldManager.mapHelpers.createFovMapFor(mapIndex);
    ImmutableArray<Entity> lightSources = WorldManager.engine.getEntitiesFor(family);

    if (counter >= .10f) {
      for (Entity lightSource : lightSources) {
        LightComponent light = ComponentMappers.light.get(lightSource);
        PositionComponent position = ComponentMappers.position.get(lightSource);

        if (position == null) {
          if (WorldManager.itemHelpers.isEquipped(WorldManager.player, lightSource)) {
            position = ComponentMappers.position.get(WorldManager.player);
          } else {
            continue;
          }
        }

        float radius = light.radius;

        if (light.flickers) {
          radius = MathUtils.random(light.radius - 1, light.radius + 1);
        }

        float[][] map = caster.calculateFov(
            fovMap, (int) position.pos.x, (int) position.pos.y, radius
        );

        int colorIndex = MathUtils.random(0, light.colors.size() - 1);

        for (int x = 0; x < map.length; x++) {
          for (int y = 0; y < map[x].length; y++) {
            lightMap[x][y] += map[x][y];
            colorMap[x][y] = light.colors.get(colorIndex);
          }
        }
      }

      counter = 0;
    }
  }
}
