package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.ShadowCaster;
import me.dannytatom.xibalba.world.WorldManager;

import java.util.Objects;

public class EntityHelpers {
  /**
   * EntityHelpers constructor, clearly.
   */
  public EntityHelpers() {

  }

  public boolean shouldSkipTurn(Entity entity) {
    return (ComponentMappers.crippled.has(entity)
        && ComponentMappers.crippled.get(entity).turnCounter != 0)
        || (ComponentMappers.stuck.has(entity));
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
   * Find if an entity is near the player.
   *
   * @param entity The entity to check
   *
   * @return Whether or not they are
   */
  public boolean isNearPlayer(Entity entity) {
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);
    PositionComponent entityPosition = ComponentMappers.position.get(entity);

    return (entityPosition.pos.x == playerPosition.pos.x - 1
        || entityPosition.pos.x == playerPosition.pos.x
        || entityPosition.pos.x == playerPosition.pos.x + 1)
        && (entityPosition.pos.y == playerPosition.pos.y - 1
        || entityPosition.pos.y == playerPosition.pos.y
        || entityPosition.pos.y == playerPosition.pos.y + 1);
  }

  public boolean playerAlive() {
    return ComponentMappers.attributes.get(WorldManager.player).health > 0;
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
   * Entity's toughness + armor defense.
   *
   * @param entity Who we're getting defense of
   *
   * @return Their combined defense
   */
  public int getCombinedDefense(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    return MathUtils.random(1, attributes.toughness) + getArmorDefense(entity);
  }

  /**
   * Just armor defense.
   *
   * @param entity Who we're getting defense of
   *
   * @return Their armor defense
   */
  public int getArmorDefense(Entity entity) {
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);

    int defense = 0;

    if (equipment != null) {
      for (Entity item : equipment.slots.values()) {
        if (item != null) {
          ItemComponent itemDetails = ComponentMappers.item.get(item);

          if (Objects.equals(itemDetails.type, "armor")) {
            defense += itemDetails.attributes.get("defense");
          }
        }
      }
    }

    return defense;
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
    position.pos.set(newPosition);
  }

  public void updateSpritePosition(Entity entity, Vector2 position) {
    VisualComponent visual = ComponentMappers.visual.get(entity);
    visual.sprite.setPosition(
        position.x * Main.SPRITE_WIDTH, position.y * Main.SPRITE_HEIGHT
    );
  }

  /**
   * Apply an effect to an entity.
   *
   * @param entity The entity
   * @param effect The effect. This is a string like "raiseHealth:5" where the part before colon is
   *               the method on EffectsHelpers, and the part after is the parameters (split by
   *               commas)
   */
  void applyEffect(Entity entity, String effect) {
    String[] split = effect.split(":");
    String name = split[0];
    String[] params = split[1].split(",");

    switch (name) {
      case "raiseHealth":
        raiseHealth(entity, Integer.parseInt(params[0]));
        break;
      case "raiseStrength":
        raiseStrength(entity, Integer.parseInt(params[0]));
        break;
      case "takeDamage":
        takeDamage(entity, Integer.parseInt(params[0]));
        break;
      default:
    }
  }

  /**
   * Raise health.
   *
   * @param entity Entity whose health we're raising
   * @param amount How much
   */
  private void raiseHealth(Entity entity, int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (attributes.health + amount < attributes.maxHealth) {
      attributes.health += amount;

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add("You gain " + amount + " health");

        ComponentMappers.player.get(entity).totalDamageHealed += amount;
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
  private void raiseStrength(Entity entity, int amount) {
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

  public void takeDamage(Entity entity, int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    attributes.health -= amount;

    if (ComponentMappers.player.has(entity)) {
      ComponentMappers.player.get(entity).totalDamageReceived += amount;
    }
  }
}
