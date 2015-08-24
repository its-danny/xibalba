package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.*;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.effects.DamageEffectComponent;

import java.util.Objects;

public class EntityHelpers {
  private final Main main;
  private final Engine engine;

  public EntityHelpers(Main main, Engine engine) {
    this.main = main;
    this.engine = engine;
  }

  /**
   * Spawn the player somewhere.
   *
   * @param player   The player
   * @param position Vector2 of where to spawn them
   * @return The player
   */
  public Entity spawnPlayer(Entity player, Vector2 position) {
    player.add(new PlayerComponent());
    player.add(new PositionComponent(position));
    player.add(new VisualComponent(null, main.assets.get("sprites/player.atlas")));
    player.add(new SkillsComponent());
    player.add(new InventoryComponent());
    player.add(new EquipmentComponent());

    return player;
  }

  /**
   * Spawn an enemy somewhere.
   *
   * @param type     What type of enemy to spawn
   * @param position Vector2 of where to spawn them
   * @return The enemy
   */
  public Entity spawnEnemy(String type, Vector2 position) {
    JsonToEnemy json = (new Json()).fromJson(JsonToEnemy.class,
        Gdx.files.internal("data/enemies/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(new EnemyComponent());
    entity.add(new BrainComponent());
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(null, main.assets.get("sprites/" + type + ".atlas")));
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

  /**
   * Spawn an item somewhere.
   *
   * @param type     What type of item to spawn
   * @param position Vector2 of where to spawn it
   * @return The item
   */
  public Entity spawnItem(String type, Vector2 position) {
    Entity entity = new Entity();

    entity.add(
        (new Json()).fromJson(ItemComponent.class,
            Gdx.files.internal("data/items/" + type + ".json"))
    );
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(new Sprite(
            (Texture) main.assets.get("sprites/" + type + ".png")), null)
    );

    ItemComponent item = entity.getComponent(ItemComponent.class);

    if (item.attributes != null) {
      item.attributes.put("damage",
          MathUtils.random(item.attributes.get("damage"), item.attributes.get("damage") + 10));
    }

    return entity;
  }

  /**
   * Spawn an effect somewhere.
   *
   * @param starter  Who set off the effect
   * @param position Vector2 of where to spawn the effect
   * @param item     The item that caused the effect
   */
  public void spawnEffect(Entity starter, Vector2 position, Entity item) {
    ItemComponent ic = item.getComponent(ItemComponent.class);

    for (int x = (int) position.x - ic.effectRange; x < position.x + ic.effectRange; x++) {
      for (int y = (int) position.y - ic.effectRange; y < position.y + ic.effectRange; y++) {
        Entity projectile = new Entity();
        projectile.add(new PositionComponent(new Vector2(x, y)));
        projectile.add(
            new VisualComponent(new Sprite((Texture) main.assets.get("sprites/poison.png")), null)
        );

        if (Objects.equals(ic.effect, "poison")) {
          projectile.add(
              new DamageEffectComponent(starter, "poison",
                  ic.effectTurns, ic.attributes.get("damage"))
          );
        }

        engine.addEntity(projectile);
      }
    }
  }

  public Entity getPlayer() {
    return engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
  }

  public boolean isEnemy(Entity entity) {
    return entity != null && entity.getComponent(EnemyComponent.class) != null;
  }

  public boolean isItem(Entity entity) {
    return entity != null && entity.getComponent(ItemComponent.class) != null;
  }
}
