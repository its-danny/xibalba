package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.map.ShadowCaster;

import java.util.Objects;

public class EntityHelpers {
  private final Main main;

  private final ShadowCaster caster;

  /**
   * Helpers for dealing with entities.
   *
   * @param main Instance of Main class
   */
  public EntityHelpers(Main main) {
    this.main = main;

    caster = new ShadowCaster();
  }

  /**
   * Spawn the player somewhere.
   *
   * @param player   The player
   * @param position Vector2 of where to spawn them
   */
  public void spawnPlayer(Entity player, int map, Vector2 position) {
    Array<String> sprites = new Array<>();
    sprites.addAll("Level/Cave/Character/Ikal-1");
    sprites.addAll("Level/Cave/Character/Iktan-1");
    sprites.addAll("Level/Cave/Character/Itzel-1");
    sprites.addAll("Level/Cave/Character/Yatzil-1");

    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    player.add(new PlayerComponent());
    player.add(new PositionComponent(map, position));
    player.add(new VisualComponent(
        atlas.createSprite(sprites.random()))
    );
    player.add(new SkillsComponent());
    player.add(new InventoryComponent());
    player.add(new EquipmentComponent());
  }

  /**
   * Spawn exit entity.
   *
   * @param position Where to spawn it
   *
   * @return The exit entity
   */
  public Entity spawnExit(int map, Vector2 position) {
    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    Entity entity = new Entity();
    entity.add(new PositionComponent(map, position));
    entity.add(new VisualComponent(atlas.createSprite("Level/Cave/FX/Bolt-1")));
    entity.add(new ExitComponent());

    return entity;
  }

  /**
   * Spawn an enemy somewhere.
   *
   * @param type     What type of enemy to spawn
   * @param position Vector2 of where to spawn them
   *
   * @return The enemy
   */
  public Entity spawnEnemy(String type, int map, Vector2 position) {
    JsonToEnemy json = (new Json()).fromJson(JsonToEnemy.class,
        Gdx.files.internal("data/enemies/" + type + ".json"));
    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    Entity entity = new Entity();

    entity.add(new EnemyComponent());
    entity.add(new BrainComponent());
    entity.add(new PositionComponent(map, position));
    entity.add(new VisualComponent(atlas.createSprite(json.visual.get("spritePath"))));
    entity.add(new SkillsComponent());
    entity.add(new AttributesComponent(
        json.name,
        json.description,
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
   * <p>TODO: This is terrible, fix it pls. See TODO in ItemComponent.
   *
   * @param type     What type of item to spawn
   * @param position Vector2 of where to spawn it
   *
   * @return The item
   */
  public Entity spawnItem(String type, int map, Vector2 position) {
    TextureAtlas atlas = main.assets.get("sprites/main.atlas");
    ItemComponent itemComponent = (new Json()).fromJson(ItemComponent.class,
        Gdx.files.internal("data/items/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(itemComponent);
    entity.add(new PositionComponent(map, position));
    entity.add(new VisualComponent(
        atlas.createSprite(itemComponent.visual.get("sprites").random())
    ));

    if (itemComponent.attributes != null && !Objects.equals(itemComponent.type, "ammunition")) {
      if (itemComponent.attributes.get("hitDamage") != null) {
        itemComponent.attributes.put("hitDamage",
            MathUtils.random(itemComponent.attributes.get("hitDamage"),
                itemComponent.attributes.get("hitDamage") + 10));
      }

      if (itemComponent.attributes.get("throwDamage") != null) {
        itemComponent.attributes.put("throwDamage",
            MathUtils.random(itemComponent.attributes.get("throwDamage"),
                itemComponent.attributes.get("throwDamage") + 10));
      }
    }

    return entity;
  }

  /**
   * Spawn a random filler decoration.
   *
   * @param position Where to spawn it
   *
   * @return The decoration entity
   */
  public Entity spawnRandomDecoration(int map, Vector2 position) {
    Array<String> types = new Array<>();
    types.add("Level/Cave/Environment/Object/Mushroom-1");
    types.add("Level/Cave/Environment/Object/Mushroom-2");
    types.add("Level/Cave/Environment/Object/Rock-1");
    types.add("Level/Cave/Environment/Object/Rock-2");
    types.add("Level/Cave/Environment/Object/Rock-3");
    types.add("Level/Cave/Environment/Object/Rock-4");
    types.add("Level/Cave/Environment/Object/Vase-1");

    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    Entity decoration = new Entity();

    decoration.add(new DecorationComponent());
    decoration.add(new PositionComponent(map, position));
    decoration.add(new VisualComponent(
        atlas.createSprite(types.random())
    ));

    return decoration;
  }

  public boolean isEnemy(Entity entity) {
    return entity != null && entity.getComponent(EnemyComponent.class) != null;
  }

  public boolean isItem(Entity entity) {
    return entity != null && entity.getComponent(ItemComponent.class) != null;
  }

  public boolean isExit(Entity entity) {
    return entity != null && entity.getComponent(ExitComponent.class) != null;
  }

  /**
   * Checks if an entity is hidden or not.
   *
   * @param entity Entity to check
   *
   * @return Whether or not it's visible
   */
  public boolean isVisible(Entity entity) {
    PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

    return positionComponent != null
        && positionComponent.map == main.currentMapIndex
        && !main.mapHelpers.getCell(positionComponent.pos.x, positionComponent.pos.y).hidden;
  }

  /**
   * Check if entity is near the player.
   *
   * @param entity Who we checking
   *
   * @return Whether we're near the player or not
   */
  public boolean isNearPlayer(Entity entity) {
    PositionComponent entityPosition = entity.getComponent(PositionComponent.class);
    PositionComponent playerPosition = main.player.getComponent(PositionComponent.class);

    return entityPosition.map == playerPosition.map
        && entityPosition.pos.x <= playerPosition.pos.x + 1
        && entityPosition.pos.x >= playerPosition.pos.x - 1
        && entityPosition.pos.y <= playerPosition.pos.y + 1
        && entityPosition.pos.y >= playerPosition.pos.y - 1;
  }

  /**
   * Uses light map to determine if they can see the player.
   *
   * @param entity   ho we checking
   * @param distance Radius to use
   *
   * @return Can they see the player?
   */
  public boolean canSeePlayer(Entity entity, int distance) {
    PositionComponent entityPosition = entity.getComponent(PositionComponent.class);
    PositionComponent playerPosition = main.player.getComponent(PositionComponent.class);

    if (entityPosition.map != playerPosition.map) {
      return false;
    }

    ShadowCaster caster = new ShadowCaster();
    float[][] lightMap = caster.calculateFov(main.mapHelpers.createFovMap(),
        (int) entityPosition.pos.x, (int) entityPosition.pos.y, distance);

    return lightMap[(int) playerPosition.pos.x][(int) playerPosition.pos.y] > 0;
  }

  /**
   * Checks if an entity is visible to the player or not.
   *
   * @param entity Entity to check
   *
   * @return Whether or not it's visible to the player
   */
  public boolean isVisibleToPlayer(Entity entity) {
    PositionComponent enemyPosition = entity.getComponent(PositionComponent.class);
    PositionComponent playerPosition = main.player.getComponent(PositionComponent.class);
    AttributesComponent playerAttributes = main.player.getComponent(AttributesComponent.class);

    if (enemyPosition.map != playerPosition.map) {
      return false;
    }

    float[][] lightMap = caster.calculateFov(
        main.mapHelpers.createFovMap(),
        (int) playerPosition.pos.x, (int) playerPosition.pos.y,
        playerAttributes.vision
    );

    return lightMap[(int) enemyPosition.pos.x][(int) enemyPosition.pos.y] > 0;
  }

  /**
   * Attempt to get the entity at the given position, returns null if nobody is there.
   *
   * @param position The entity's position
   *
   * @return The entity
   */
  public Entity getEntityAt(Vector2 position) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(
            Family.all(PositionComponent.class).exclude(DecorationComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

      if (positionComponent.map == main.currentMapIndex
          && positionComponent.pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get enemy from a location.
   *
   * @param position Where the enemy is
   *
   * @return The enemy
   */
  public Entity getEnemyAt(Vector2 position) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

      if (positionComponent.map == main.currentMapIndex
          && positionComponent.pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get item from a location.
   *
   * @param position Where the item is
   *
   * @return The item
   */
  public Entity getItemAt(Vector2 position) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(ItemComponent.class, PositionComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

      if (positionComponent.map == main.currentMapIndex
          && positionComponent.pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }
}
