package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.components.*;
import me.dannytatom.xibalba.components.ai.BrainComponent;

public class EntityFactory {
  private final AssetManager assets;

  public EntityFactory(AssetManager assets) {
    this.assets = assets;
  }

  public Entity spawnPlayer(Vector2 position) {
    Entity player = new Entity();

    player.add(new PlayerComponent());
    player.add(new PositionComponent(position));
    player.add(new VisualComponent(assets.get("sprites/player.png")));
    player.add(new SkillsComponent());
    player.add(new AttributesComponent("Necahual", 100, 10, 50, 5, 15));

    return player;
  }

  public Entity spawnMob(String type, Vector2 position) {
    Blueprint blueprint = (new Json()).fromJson(Blueprint.class,
        Gdx.files.internal("blueprints/mobs/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(new BrainComponent());
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(assets.get(blueprint.visual.get("spritePath"))));
    entity.add(new SkillsComponent());
    entity.add(new AttributesComponent(
        blueprint.name,
        blueprint.attributes.get("speed"),
        blueprint.attributes.get("vision"),
        blueprint.attributes.get("maxHealth"),
        blueprint.attributes.get("toughness"),
        blueprint.attributes.get("damage")
    ));

    return entity;
  }
}
