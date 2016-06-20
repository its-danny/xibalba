package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.effects.DamageEffectComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.map.ShadowCaster;

import java.util.Objects;

public class EntityHelpers {
  private final Main main;
  private final Engine engine;

  private final ShadowCaster caster;

  public EntityHelpers(Main main, Engine engine) {
    this.main = main;
    this.engine = engine;

    caster = new ShadowCaster();
  }

  /**
   * Spawn the player somewhere.
   *
   * @param player   The player
   * @param position Vector2 of where to spawn them
   */
  public void spawnPlayer(Entity player, Vector2 position) {
    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    player.add(new PlayerComponent());
    player.add(new PositionComponent(position));
    player.add(new VisualComponent(
        atlas.createSprite("Universal/Player/Player-Cloth/Player-Cloth-1"))
    );
    player.add(new SkillsComponent());
    player.add(new InventoryComponent());
    player.add(new EquipmentComponent());
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
    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    Entity entity = new Entity();

    entity.add(new EnemyComponent());
    entity.add(new BrainComponent());
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(atlas.createSprite(json.visual.get("spritePath"))));
    entity.add(new SkillsComponent());
    entity.add(new AttributesComponent(
        json.name,
        json.attributes.get("speed"),
        json.attributes.get("vision"),
        json.attributes.get("maxHealth"),
        json.attributes.get("defense"),
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
   * <p> TODO: This is terrible, fix it pls. See TODO in ItemComponent.
   */
  public Entity spawnItem(String type, Vector2 position) {
    TextureAtlas atlas = main.assets.get("sprites/main.atlas");
    ItemComponent itemComponent = (new Json()).fromJson(ItemComponent.class,
        Gdx.files.internal("data/items/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(itemComponent);
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(
        atlas.createSprite(itemComponent.visual.get("sprites").random())
    ));

    if (itemComponent.attributes != null) {
      itemComponent.attributes.put("hitDamage",
          MathUtils.random(itemComponent.attributes.get("hitDamage"),
              itemComponent.attributes.get("hitDamage") + 10));
      itemComponent.attributes.put("throwDamage",
          MathUtils.random(itemComponent.attributes.get("throwDamage"),
              itemComponent.attributes.get("throwDamage") + 10));
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
    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    for (int x = (int) position.x - ic.effectRange; x < position.x + ic.effectRange; x++) {
      for (int y = (int) position.y - ic.effectRange; y < position.y + ic.effectRange; y++) {
        Entity projectile = new Entity();
        projectile.add(new PositionComponent(new Vector2(x, y)));
        projectile.add(
            new VisualComponent(
                atlas.createSprite("Universal/UI/Effects/Poison/Effect-Tile-Poison-1")
            )
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

  public boolean isVisible(Entity entity, Map map) {
    PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

    return positionComponent != null
        && !map.getCell(entity.getComponent(PositionComponent.class).pos).hidden;
  }

  public boolean isVisibleToPlayer(Entity entity, Map map) {
    PositionComponent enemyPosition = entity.getComponent(PositionComponent.class);
    PositionComponent playerPosition = getPlayer().getComponent(PositionComponent.class);
    AttributesComponent playerAttributes = getPlayer().getComponent(AttributesComponent.class);

    float[][] lightMap = caster.calculateFov(
        map.createFovMap(),
        (int) playerPosition.pos.x, (int) playerPosition.pos.y,
        playerAttributes.vision
    );

    return lightMap[(int) enemyPosition.pos.x][(int) enemyPosition.pos.y] > 0;
  }
}
