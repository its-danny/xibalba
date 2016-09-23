package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.ShadowCaster;
import me.dannytatom.xibalba.world.WorldManager;

import java.util.ArrayList;
import java.util.Objects;

public class EntityHelpers {
  /**
   * EntityHelpers constructor, clearly.
   */
  public EntityHelpers() {

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

  public boolean isCrippled(Entity entity) {
    return entity != null && ComponentMappers.crippled.has(entity);
  }

  public boolean isBleeding(Entity entity) {
    return entity != null && ComponentMappers.bleeding.has(entity);
  }

  public boolean isDrowning(Entity entity) {
    return entity != null && ComponentMappers.drowning.has(entity);
  }

  public boolean skipTurn(Entity entity) {
    return isCrippled(entity) && ComponentMappers.crippled.get(entity).turnCounter != 0;
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
        && !WorldManager.mapHelpers.getCell(entityPosition.pos.x, entityPosition.pos.y).hidden
        && !WorldManager.mapHelpers.getCell(entityPosition.pos.x, entityPosition.pos.y).forgotten;
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
   * Uses light world to determine if they can see the player.
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
        || !(Objects.equals(details.type, "consumable")
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

  public void dealDamage(Entity entity, int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    attributes.health -= amount;

    WorldManager.log.add("You lose " + amount + " health");
  }

  /**
   * Update an entity's position.
   *
   * @param entity      The entity
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
