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
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.EntranceComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.map.ShadowCaster;

import java.util.HashMap;
import java.util.Objects;

public class EntityHelpers {
  private final ShadowCaster caster;

  public EntityHelpers() {
    caster = new ShadowCaster();
  }

  /**
   * Spawn the player somewhere.
   *
   * @param player   The player
   * @param mapIndex Map to spawn em on
   */
  public void spawnPlayer(Entity player, int mapIndex) {
    Array<String> sprites = new Array<>();
    sprites.addAll("Level/Cave/Character/Ikal-1");
    sprites.addAll("Level/Cave/Character/Iktan-1");
    sprites.addAll("Level/Cave/Character/Itzel-1");
    sprites.addAll("Level/Cave/Character/Yatzil-1");

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");

    player.add(new PlayerComponent());
    player.add(new PositionComponent(mapIndex, WorldManager.mapHelpers.getEntrancePosition()));
    player.add(new VisualComponent(
        atlas.createSprite(sprites.random()))
    );
    player.add(new InventoryComponent());
    player.add(new EquipmentComponent());
    player.add(new SkillsComponent());

    HashMap<String, Integer> bodyParts = new HashMap<>();
    bodyParts.put("head", 10);
    bodyParts.put("body", 8);
    bodyParts.put("left arm", 10);
    bodyParts.put("right arm", 10);
    bodyParts.put("left leg", 10);
    bodyParts.put("right leg", 10);
    player.add(new BodyComponent(bodyParts));
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
    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");

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
        json.attributes.get("toughness"),
        json.attributes.get("strength")
    ));

    HashMap<String, Integer> bodyParts = new HashMap<>();
    for (java.util.Map.Entry<String, Integer> part : json.bodyParts.entrySet()) {
      bodyParts.put(part.getKey(), part.getValue());
    }
    entity.add(new BodyComponent(bodyParts));

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
    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");
    ItemComponent itemDetails = (new Json()).fromJson(ItemComponent.class,
        Gdx.files.internal("data/items/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(itemDetails);
    entity.add(new PositionComponent(map, position));
    entity.add(new VisualComponent(
        atlas.createSprite(itemDetails.visual.get("sprites").random())
    ));

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

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");

    Entity decoration = new Entity();

    decoration.add(new DecorationComponent());
    decoration.add(new PositionComponent(map, position));
    decoration.add(new VisualComponent(
        atlas.createSprite(types.random())
    ));

    return decoration;
  }

  /**
   * Spawn entrance entity.
   *
   * @param mapIndex Map to spawn it on
   *
   * @return The entrance entity
   */
  public Entity spawnEntrance(int mapIndex) {
    Map map = WorldManager.world.getMap(mapIndex);

    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.width - 1);
      cellY = MathUtils.random(0, map.height - 1);
    } while (WorldManager.mapHelpers.isBlocked(mapIndex, new Vector2(cellX, cellY))
        && WorldManager.mapHelpers.getWallNeighbours(mapIndex, cellX, cellY) >= 4);

    Entity entity = new Entity();
    entity.add(new EntranceComponent());
    entity.add(new PositionComponent(mapIndex, new Vector2(cellX, cellY)));

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");
    entity.add(new VisualComponent(
        atlas.createSprite("Level/Cave/Environment/Interact/Ladder-Up-1")
    ));

    return entity;
  }

  /**
   * Spawn exit entity.
   *
   * @param mapIndex Map to spawn it on
   *
   * @return The exit entity
   */
  public Entity spawnExit(int mapIndex) {
    Map map = WorldManager.world.getMap(mapIndex);

    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.width - 1);
      cellY = MathUtils.random(0, map.height - 1);
    } while (WorldManager.mapHelpers.isBlocked(mapIndex, new Vector2(cellX, cellY))
        && WorldManager.mapHelpers.getWallNeighbours(mapIndex, cellX, cellY) >= 4);

    Entity entity = new Entity();
    entity.add(new ExitComponent());
    entity.add(new PositionComponent(mapIndex, new Vector2(cellX, cellY)));

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");
    entity.add(new VisualComponent(
        atlas.createSprite("Level/Cave/Environment/Interact/Ladder-Down-1")
    ));

    return entity;
  }

  public boolean isEnemy(Entity entity) {
    return entity != null && ComponentMappers.enemy.has(entity);
  }

  public boolean isItem(Entity entity) {
    return entity != null && ComponentMappers.item.has(entity);
  }

  public boolean isEntrance(Entity entity) {
    return entity != null && ComponentMappers.entrance.has(entity);
  }

  public boolean isExit(Entity entity) {
    return entity != null && ComponentMappers.exit.has(entity);
  }

  boolean isCrippled(Entity entity) {
    return entity != null && ComponentMappers.crippled.has(entity);
  }

  boolean isBleeding(Entity entity) {
    return entity != null && ComponentMappers.bleeding.has(entity);
  }

  public boolean skipTurn(Entity entity) {
    return isCrippled(entity) && !ComponentMappers.crippled.get(entity).instance.canAct();
  }

  /**
   * Checks if an entity is hidden or not.
   *
   * @param entity Entity to check
   *
   * @return Whether or not it's visible
   */
  public boolean isVisible(Entity entity) {
    PositionComponent entityPosition = ComponentMappers.position.get(entity);

    return entityPosition != null
        && entityPosition.map == WorldManager.world.currentMapIndex
        && !WorldManager.mapHelpers.getCell(entityPosition.pos.x, entityPosition.pos.y).hidden;
  }

  /**
   * Check if entity is near the player.
   *
   * @param entity Who we checking
   *
   * @return Whether we're near the player or not
   */
  public boolean isNearPlayer(Entity entity) {
    PositionComponent entityPosition = ComponentMappers.position.get(entity);
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

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
    PositionComponent entityPosition = ComponentMappers.position.get(entity);
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

    if (entityPosition.map != playerPosition.map) {
      return false;
    }

    ShadowCaster caster = new ShadowCaster();
    float[][] lightMap = caster.calculateFov(WorldManager.mapHelpers.createFovMap(),
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
    PositionComponent entityPosition = ComponentMappers.position.get(entity);
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);
    AttributesComponent playerAttributes = ComponentMappers.attributes.get(WorldManager.player);

    if (entityPosition == null || entityPosition.map != playerPosition.map) {
      return false;
    }

    float[][] lightMap = caster.calculateFov(
        WorldManager.mapHelpers.createFovMap(),
        (int) playerPosition.pos.x, (int) playerPosition.pos.y,
        playerAttributes.vision
    );

    return lightMap[(int) entityPosition.pos.x][(int) entityPosition.pos.y] > 0;
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
        WorldManager.engine.getEntitiesFor(
            Family.all(PositionComponent.class).exclude(DecorationComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition.map == WorldManager.world.currentMapIndex
          && entityPosition.pos.epsilonEquals(position, 0.00001f)) {
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
        WorldManager.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition.map == WorldManager.world.currentMapIndex
          && entityPosition.pos.epsilonEquals(position, 0.00001f)) {
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
        WorldManager.engine.getEntitiesFor(
            Family.all(ItemComponent.class, PositionComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition.map == WorldManager.world.currentMapIndex
          && entityPosition.pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  public boolean itemIsIdentified(Entity entity, Entity item) {
    PlayerComponent player = ComponentMappers.player.get(entity);
    ItemComponent details = ComponentMappers.item.get(item);

    return player == null
        || !(Objects.equals(details.type, "plant")
        && !player.identifiedItems.contains(details.name, false));
  }

  public String getItemName(Entity entity, Entity item) {
    ItemComponent details = ComponentMappers.item.get(item);

    if (itemIsIdentified(entity, item)) {
      return details.name;
    } else {
      return "???";
    }
  }

  public void raiseHealth(Entity entity, int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (attributes.health + amount < attributes.maxHealth) {
      attributes.health += amount;

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add("You gain " + amount + " health");
      } else {
        WorldManager.log.add(attributes.name + " gained " + amount + " health");
      }
    }
  }

  public void raiseStrength(Entity entity, int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (attributes.strength < 12) {
      attributes.strength += amount;

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add("Your strength has improved to " + attributes.strength + "d");
      } else {
        WorldManager.log.add(
            attributes.name + " strength has improved to " + attributes.strength + "d"
        );
      }
    }
  }
}
