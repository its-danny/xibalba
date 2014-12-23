package me.dannytatom.xibalba.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.components.*;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.utils.Blueprint;

public class MobFactory {
  private final AssetManager assets;

  public MobFactory(AssetManager assets) {
    this.assets = assets;
  }

  /**
   * Handles mob spawning.
   *
   * @param x x pos to spawn
   * @param y y pos to spawn
   * @return the newly made entity
   */
  public Entity spawn(String type, Vector2 position) {
    Blueprint blueprint = (new Json()).fromJson(Blueprint.class,
        Gdx.files.internal("blueprints/mobs/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(new BrainComponent());
    entity.add(new WanderComponent());
    entity.add(new PositionComponent(position));
    entity.add(new MovementComponent());
    entity.add(new VisualComponent(assets.get(blueprint.visual.get("spritePath"))));
    entity.add(new AttributesComponent(
        blueprint.attributes.get("speed"),
        blueprint.attributes.get("vision")
    ));

    return entity;
  }
}
