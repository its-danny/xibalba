package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.components.LightComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MapLight {
  public float[][] lightMap;
  public Color[][] colorMap;
  private int mapIndex;
  private ShadowCaster caster;
  private Family family;
  private float counter = 0;
  private int colorIndex = 0;

  public MapLight(int mapIndex) {
    this.mapIndex = mapIndex;
    this.caster = new ShadowCaster();
    this.family = Family.all(LightComponent.class).get();
  }

  public void update(float delta) {
    counter += delta;

    if (counter >= .10f) {
      counter = 0;

      float[][] fovMap = WorldManager.mapHelpers.createFovMapFor(mapIndex);
      ImmutableArray<Entity> lightSources = WorldManager.engine.getEntitiesFor(family);

      lightMap = new float[fovMap.length][fovMap[0].length];
      colorMap = new Color[fovMap.length][fovMap[0].length];

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

        colorIndex = MathUtils.random(0, light.colors.size() - 1);

        for (int x = 0; x < map.length; x++) {
          for (int y = 0; y < map[x].length; y++) {
            lightMap[x][y] += map[x][y];
            colorMap[x][y] = light.colors.get(colorIndex);
          }
        }
      }
    }
  }
}
