package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
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
    JSONToMob json = (new Json()).fromJson(JSONToMob.class,
        Gdx.files.internal("data/mobs/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(new BrainComponent());
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(assets.get(json.visual.get("spritePath"))));
    entity.add(new SkillsComponent());
    entity.add(new AttributesComponent(
        json.name,
        json.attributes.get("speed"),
        json.attributes.get("vision"),
        json.attributes.get("maxHealth"),
        json.attributes.get("toughness"),
        json.attributes.get("damage")
    ));

    return entity;
  }

  public Entity spawnItem(String type) {
    Entity entity = new Entity();
    entity.add((new Json()).fromJson(ItemComponent.class,
        Gdx.files.internal("data/items/" + type + ".json")));

    ItemComponent item = entity.getComponent(ItemComponent.class);

    item.attributes.put("damage",
        MathUtils.random(item.attributes.get("damage"), item.attributes.get("damage") + 10));

    return entity;
  }
}
