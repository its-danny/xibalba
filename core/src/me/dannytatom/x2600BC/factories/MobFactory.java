package me.dannytatom.x2600BC.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.x2600BC.components.AttributesComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.components.VisualComponent;

public class MobFactory {
  static String[] TYPES = {
      "Spider Monkey", "Peccary", "Coatlmundi",
      "Rattlesnake", "Margay", "Ocelot", "Jaguar"
  };

  AssetManager assets;

  public MobFactory(AssetManager assets) {
    this.assets = assets;
  }

  /**
   * Handles mob spawning.
   *
   * @param x x position to spawn
   * @param y y position to spawn
   * @return the newly made entity
   */
  public Entity spawn(int x, int y) {
    Entity entity = new Entity();

    switch (TYPES[MathUtils.random(0, TYPES.length - 1)]) {
      default:
        entity.add(new PositionComponent(x, y));
        entity.add(new VisualComponent(assets.get("sprites/spider.png")));
        entity.add(new AttributesComponent(100, 50, 10));
    }

    return entity;
  }
}
