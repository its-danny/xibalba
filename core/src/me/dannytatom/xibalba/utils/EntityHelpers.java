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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EntityHelpers {
  private final Array<String> decorationTypes;

  /**
   * EntityHelpers constructor, clearly.
   */
  public EntityHelpers() {
    decorationTypes = new Array<>();
    decorationTypes.add("Mushroom-1");
    decorationTypes.add("Mushroom-2");
    decorationTypes.add("Rock-1");
    decorationTypes.add("Rock-2");
    decorationTypes.add("Rock-3");
    decorationTypes.add("Rock-4");
    decorationTypes.add("Vase-1");
  }

  /**
   * Setup player entity.
   *
   * @param player The player
   */
  public void setupPlayer(Entity player) {
    Array<String> sprites = new Array<>();
    sprites.addAll("Level/Cave/Character/Ikal-1");
    sprites.addAll("Level/Cave/Character/Iktan-1");
    sprites.addAll("Level/Cave/Character/Itzel-1");
    sprites.addAll("Level/Cave/Character/Yatzil-1");

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");
    Vector2 position = WorldManager.mapHelpers.getEntrancePosition();

    player.add(new PlayerComponent());
    player.add(new PositionComponent(position));
    player.add(new VisualComponent(
        atlas.createSprite(sprites.random()), position
    ));
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
   * Create an enemy.
   *
   * @param type     What type of enemy to create
   * @param position Vector2 of their position
   *
   * @return The enemy
   */
  public Entity createEnemy(String type, Vector2 position) {
    JsonToEnemy json = (new Json()).fromJson(JsonToEnemy.class,
        Gdx.files.internal("data/enemies/" + type + ".json"));
    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");

    Entity entity = new Entity();

    entity.add(new EnemyComponent());
    entity.add(new BrainComponent());
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(atlas.createSprite(json.visual.get("spritePath")), position));
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
   * Create an item.
   *
   * <p>TODO: This is terrible, fix it pls. See TODO in ItemComponent.
   *
   * @param type     What type of item to create
   * @param position Vector2 of their position
   *
   * @return The item
   */
  public Entity createItem(String type, Vector2 position) {
    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");
    ItemComponent itemDetails = (new Json()).fromJson(ItemComponent.class,
        Gdx.files.internal("data/items/" + type + ".json"));

    Entity entity = new Entity();

    entity.add(itemDetails);
    entity.add(new PositionComponent(position));
    entity.add(new VisualComponent(
        atlas.createSprite(itemDetails.visual.get("sprites").random()), position
    ));

    return entity;
  }

  /**
   * Create a random decoration.
   *
   * @param position Vector2 of their position
   *
   * @return The decoration entity
   */
  public Entity createRandomDecoration(Vector2 position) {
    String type = decorationTypes.random();
    Entity entity = new Entity();

    if (Objects.equals(type, "Vase-1")) {
      entity.add(new DecorationComponent(true));
    } else {
      entity.add(new DecorationComponent(false));
    }

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");

    entity.add(new PositionComponent(position));
    entity.add(
        new VisualComponent(atlas.createSprite("Level/Cave/Environment/Object/" + type), position)
    );

    return entity;
  }

  /**
   * Create entrance entity.
   *
   * @param mapIndex Map to place it on
   *
   * @return The entrance entity
   */
  public Entity createEntrance(int mapIndex) {
    Map map = WorldManager.world.getMap(mapIndex);

    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.width - 1);
      cellY = MathUtils.random(0, map.height - 1);
    }
    while (WorldManager.mapHelpers.isBlocked(mapIndex, new Vector2(cellX, cellY))
        && WorldManager.mapHelpers.getWallNeighbours(mapIndex, cellX, cellY) >= 4);

    Vector2 position = new Vector2(cellX, cellY);
    Entity entity = new Entity();
    entity.add(new EntranceComponent());
    entity.add(new PositionComponent(position));

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");
    entity.add(new VisualComponent(
        atlas.createSprite("Level/Cave/Environment/Interact/Ladder-Up-1"), position
    ));

    return entity;
  }

  /**
   * Create exit entity.
   *
   * @param mapIndex Map to place it on
   *
   * @return The exit entity
   */
  public Entity createExit(int mapIndex) {
    Map map = WorldManager.world.getMap(mapIndex);

    int cellX;
    int cellY;

    do {
      cellX = MathUtils.random(0, map.width - 1);
      cellY = MathUtils.random(0, map.height - 1);
    }
    while (WorldManager.mapHelpers.isBlocked(mapIndex, new Vector2(cellX, cellY))
        && WorldManager.mapHelpers.getWallNeighbours(mapIndex, cellX, cellY) >= 4);

    Vector2 position = new Vector2(cellX, cellY);
    Entity entity = new Entity();
    entity.add(new ExitComponent());
    entity.add(new PositionComponent(position));

    TextureAtlas atlas = Main.assets.get("sprites/main.atlas");
    entity.add(new VisualComponent(
        atlas.createSprite("Level/Cave/Environment/Interact/Ladder-Down-1"), position
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

    return entityPosition.pos.x <= playerPosition.pos.x + 1
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

    ShadowCaster caster = new ShadowCaster();
    float[][] lightMap = caster.calculateFov(WorldManager.mapHelpers.createFovMap(),
        (int) entityPosition.pos.x, (int) entityPosition.pos.y, distance);

    return lightMap[(int) playerPosition.pos.x][(int) playerPosition.pos.y] > 0;
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

      if (entityPosition.pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Get all entities at a given position.
   *
   * @param position Where we're searching
   *
   * @return ArrayList of entities
   */
  public ArrayList<Entity> getEntitiesAt(Vector2 position) {
    ArrayList<Entity> list = new ArrayList<>();

    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(PositionComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent entityPosition = ComponentMappers.position.get(entity);

      if (entityPosition != null && entityPosition.pos.epsilonEquals(position, 0.00001f)) {
        list.add(entity);
      }
    }

    return list;
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

      if (entityPosition.pos.epsilonEquals(position, 0.00001f)) {
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

      if (entityPosition.pos.epsilonEquals(position, 0.00001f)) {
        return entity;
      }
    }

    return null;
  }

  /**
   * Whether or not an item is identified.
   *
   * @param entity The entity looking
   * @param item   The item itself
   *
   * @return If it is indeed identified
   */
  public boolean itemIsIdentified(Entity entity, Entity item) {
    PlayerComponent player = ComponentMappers.player.get(entity);
    ItemComponent details = ComponentMappers.item.get(item);

    return player == null
        || !(Objects.equals(details.type, "plant")
        && !player.identifiedItems.contains(details.name, false));
  }

  /**
   * Get item name.
   *
   * @param entity The entity looking
   * @param item   The item itself
   *
   * @return The item name if identified, ??? otherwise
   */
  public String getItemName(Entity entity, Entity item) {
    ItemComponent details = ComponentMappers.item.get(item);

    if (itemIsIdentified(entity, item)) {
      return details.name;
    } else {
      return "???";
    }
  }

  /**
   * Raise health.
   *
   * @param entity Entity whose health we're raising
   * @param amount How much
   */
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

  /**
   * Raise strength.
   *
   * @param entity Entity whose strength we're raising
   * @param amount How much
   */
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

  /**
   * Update an entity's position.
   *
   * @param entity The entity
   * @param newPosition Where they're going
   */
  public void updatePosition(Entity entity, Vector2 newPosition) {
    if (!ComponentMappers.position.has(entity)) {
      entity.add(new PositionComponent());
    }

    PositionComponent position = ComponentMappers.position.get(entity);
    VisualComponent visual = ComponentMappers.visual.get(entity);

    position.pos.set(newPosition);
    visual.sprite.setPosition(
        position.pos.x * Main.SPRITE_WIDTH, position.pos.y * Main.SPRITE_HEIGHT
    );
  }
}
