package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.components.*;
import me.dannytatom.xibalba.components.ai.BrainComponent;

public class EntityHelpers {
  private final Engine engine;
  private final AssetManager assets;

  public EntityHelpers(Engine engine, AssetManager assets) {
    this.engine = engine;
    this.assets = assets;
  }

  public Entity spawnPlayer(Vector2 position) {
    Entity player = new Entity();

    player.add(new PlayerComponent());
    player.add(new PositionComponent(position));
    player.add(new VisualComponent(assets.get("sprites/player.png")));
    player.add(new SkillsComponent());
    player.add(new AttributesComponent("Necahual", 100, 15, 50, 5, 5));
    player.add(new InventoryComponent());

    return player;
  }

  public Entity spawnEnemy(String type, Vector2 position) {
    JSONToEnemy json = (new Json()).fromJson(JSONToEnemy.class,
        Gdx.files.internal("data/enemies/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(new EnemyComponent());
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

  public Entity spawnItem(String type, Vector2 position) {
    Entity entity = new Entity();

    entity.add((new Json()).fromJson(ItemComponent.class, Gdx.files.internal("data/items/" + type + ".json")));
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(assets.get("sprites/" + type + ".png")));

    ItemComponent item = entity.getComponent(ItemComponent.class);

    item.attributes.put("damage",
        MathUtils.random(item.attributes.get("damage"), item.attributes.get("damage") + 10));

    return entity;
  }

  public Entity getPlayer() {
    return engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
  }

  public boolean isPlayer(Entity entity) {
    return entity != null && entity.getComponent(PlayerComponent.class) != null;
  }

  public boolean isEnemy(Entity entity) {
    return entity != null && entity.getComponent(EnemyComponent.class) != null;
  }

  public boolean isItem(Entity entity) {
    return entity != null && entity.getComponent(ItemComponent.class) != null;
  }

  public int getDamage(Entity entity) {
    InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
    int damage = 0;

    damage += entity.getComponent(AttributesComponent.class).damage;

    if (inventory != null) {
      for (int i = 0; i < inventory.items.size(); i++) {
        ItemComponent it = inventory.items.get(i).getComponent(ItemComponent.class);

        if (it.equipped) {
          damage += it.attributes.get("damage");
        }
      }
    }

    return damage;
  }
}
