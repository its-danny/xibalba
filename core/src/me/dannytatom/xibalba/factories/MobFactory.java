package me.dannytatom.xibalba.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.utils.Blueprint;

public class MobFactory {
  private final AssetManager assets;

  public MobFactory(AssetManager assets) {
    this.assets = assets;
  }

  /**
   * Handles mob spawning.
   *
   * @param type     what to spawn
   * @param position where to spawn it
   * @return the newly made entity
   */
  public Entity spawn(String type, Vector2 position) {
    Blueprint blueprint = (new Json()).fromJson(Blueprint.class,
        Gdx.files.internal("blueprints/mobs/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(new BrainComponent());
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(assets.get(blueprint.visual.get("spritePath"))));
    entity.add(new SkillsComponent());
    entity.add(new AttributesComponent(
        blueprint.attributes.get("speed"),
        blueprint.attributes.get("vision"),
        blueprint.attributes.get("maxHealth"),
        blueprint.attributes.get("toughness"),
        blueprint.attributes.get("damage")
    ));

    return entity;
  }
}
