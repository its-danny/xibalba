package me.dannytatom.x2600BC.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.x2600BC.components.*;
import me.dannytatom.x2600BC.components.ai.WanderComponent;
import me.dannytatom.x2600BC.utils.Blueprint;

public class MobFactory {
  private AssetManager assets;

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
  public Entity spawn(String type, int x, int y) {
    Json json = new Json();
    Blueprint blueprint = json.fromJson(Blueprint.class,
        Gdx.files.internal("blueprints/mobs/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(new BrainComponent());
    entity.add(new WanderComponent());
    entity.add(new PositionComponent(x, y));
    entity.add(new MovementComponent());
    entity.add(new VisualComponent(assets.get(blueprint.visual.get("spritePath"))));
    entity.add(new AttributesComponent(
        blueprint.attributes.get("vision"),
        blueprint.attributes.get("speed")
    ));

    return entity;
  }
}
