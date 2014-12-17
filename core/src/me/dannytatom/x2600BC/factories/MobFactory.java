package me.dannytatom.x2600BC.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import me.dannytatom.x2600BC.blueprints.SpiderMonkey;
import me.dannytatom.x2600BC.components.*;

public class MobFactory {
  AssetManager assets;

  public MobFactory(AssetManager assets) {
    this.assets = assets;
  }

  /**
   * Handles mob spawning.
   *
   * @param cellX x position to spawn
   * @param cellY y position to spawn
   * @return the newly made entity
   */
  public Entity spawn(String type, int cellX, int cellY) {
    Entity entity = new Entity();

    entity.add(new BrainComponent(3));
    entity.add(new WanderComponent());
    entity.add(new PositionComponent(cellX, cellY));
    entity.add(new MovementComponent());
    entity.add(new VisualComponent(assets.get(SpiderMonkey.visual.get("spritePath"))));
    entity.add(new AttributesComponent(
        SpiderMonkey.attributes.get("speed"),
        SpiderMonkey.attributes.get("health"),
        SpiderMonkey.attributes.get("damage")
    ));

    return entity;
  }
}
