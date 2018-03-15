package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import java.util.Objects;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.Map;
import me.dannytatom.xibalba.world.MapCell;
import me.dannytatom.xibalba.world.ShadowCaster;
import me.dannytatom.xibalba.world.WorldManager;

public class EntityHelpers {
  private final ShadowCaster caster;

  public EntityHelpers() {
    caster = new ShadowCaster();
  }

  /**
   * Should this entity skip it's turn? </p> They should skip a turn if they're crippled and the
   * turn turnCounter isn't 0 or if they're stuck.
   *
   * @param entity The entity to check
   * @return If they should
   */
  public boolean shouldSkipTurn(Entity entity) {
    return (ComponentMappers.encumbered.has(entity)
        && ComponentMappers.encumbered.get(entity).turnCounter != 0)
        || (ComponentMappers.crippled.has(entity)
        && ComponentMappers.crippled.get(entity).turnCounter != 0)
        || (ComponentMappers.stuck.has(entity));
  }

  public boolean hasTrait(Entity entity, String trait) {
    return ComponentMappers.traits.get(entity).traits.contains(trait, false);
  }

  public boolean hasDefect(Entity entity, String defect) {
    return ComponentMappers.defects.get(entity).defects.contains(defect, false);
  }

  /**
   * Checks if an entity is hidden or not.
   *
   * @param entity Entity to check
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
   * @return Whether or not they are
   */
  public boolean isNearPlayer(Entity entity) {
    return isNear(entity, WorldManager.player);
  }

  /**
   * Whether or not the target is near the entity.
   *
   * @param entity Who's checkin'
   * @param target Who we checkin' for
   * @return Yes/no
   */
  public boolean isNear(Entity entity, Entity target) {
    PositionComponent entityPosition = ComponentMappers.position.get(entity);
    PositionComponent targetPosition = ComponentMappers.position.get(target);

    return (entityPosition.pos.x == targetPosition.pos.x - 1
        || entityPosition.pos.x == targetPosition.pos.x
        || entityPosition.pos.x == targetPosition.pos.x + 1)
        && (entityPosition.pos.y == targetPosition.pos.y - 1
        || entityPosition.pos.y == targetPosition.pos.y
        || entityPosition.pos.y == targetPosition.pos.y + 1);
  }

  /**
   * Update an entity's senses.
   *
   * @param entity The entity
   */
  public void updateSenses(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    PositionComponent position = ComponentMappers.position.get(entity);

    float[][] fovMap = WorldManager.mapHelpers.createFovMap();

    attributes.hearingMap = caster.calculateFov(
        fovMap, (int) position.pos.x, (int) position.pos.y, attributes.hearing
    );

    if (!Main.debug.fieldOfViewEnabled && ComponentMappers.player.has(entity)) {
      Map map = WorldManager.world.getCurrentMap();
      float[][] visionMap = new float[map.width][map.height];

      for (int x = 0; x < map.width; x++) {
        for (int y = 0; y < map.height; y++) {
          visionMap[x][y] = 1;
        }
      }

      attributes.visionMap = visionMap;
    } else {
      attributes.visionMap = caster.calculateFov(
          fovMap, (int) position.pos.x, (int) position.pos.y, attributes.vision
      );
    }
  }

  /**
   * Check if an entity can see another entity.
   *
   * @param looker Who looking
   * @param target Who they looking for
   * @return Yes/no
   */
  public boolean canSee(Entity looker, Entity target) {
    if (target == null) {
      return false;
    }

    AttributesComponent attributes = ComponentMappers.attributes.get(looker);
    PositionComponent targetPosition = ComponentMappers.position.get(target);

    int cellX = (int) targetPosition.pos.x;
    int cellY = (int) targetPosition.pos.y;

    return attributes.visionMap[cellX][cellY] > 0;
  }

  /**
   * Check if an entity can hear another entity.
   *
   * @param listener Who listening
   * @param target   Who they listening for
   * @return Yes/no
   */
  public boolean canHear(Entity listener, Entity target) {
    AttributesComponent attributes = ComponentMappers.attributes.get(listener);
    PositionComponent targetPosition = ComponentMappers.position.get(target);

    return attributes.hearingMap[(int) targetPosition.pos.x][(int) targetPosition.pos.y] > 0;
  }

  public boolean canSense(Entity entity, Entity target) {
    return canSee(entity, target) || canHear(entity, target);
  }

  /**
   * Are there any enemies in sight.
   *
   * @param entity Who's looking
   * @return Yes/no
   */
  public boolean enemyInSight(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    float[][] vision = attributes.visionMap;

    for (int x = 0; x < vision.length; x++) {
      for (int y = 0; y < vision[x].length; y++) {
        Entity enemy = WorldManager.mapHelpers.getEnemyAt(x, y);

        if (enemy != null && canSee(entity, enemy)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean isAquatic(Entity entity) {
    BrainComponent brain = ComponentMappers.brain.get(entity);
    return brain != null && brain.dna.contains(BrainComponent.Dna.AQUATIC, false);
  }

  /**
   * Entity's toughness + armor defense.
   *
   * @param entity Who we're getting defense of
   * @return Their combined defense
   */
  int getCombinedDefense(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    return MathUtils.random(1, attributes.toughness) + getArmorDefense(entity);
  }

  /**
   * Just armor defense.
   *
   * @param entity Who we're getting defense of
   * @return Their armor defense
   */
  public int getArmorDefense(Entity entity) {
    EquipmentComponent equipment = ComponentMappers.equipment.get(entity);

    int defense = 0;

    if (equipment != null) {
      for (java.util.Map.Entry<String, Entity> entry : equipment.slots.entrySet()) {
        String slot = entry.getKey();
        Entity item = entry.getValue();

        if (item != null) {
          ItemComponent itemDetails = ComponentMappers.item.get(item);

          if (Objects.equals(itemDetails.type, "armor") && itemDetails.location.equals(slot)) {
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
   * @param entity The entity
   * @param cellX  x position
   * @param cellY  y position
   */
  public void updatePosition(Entity entity, float cellX, float cellY) {
    if (!ComponentMappers.position.has(entity)) {
      entity.add(new PositionComponent((int) cellX, (int) cellY));
    } else {
      ComponentMappers.position.get(entity).pos.set(cellX, cellY);
    }
  }

  /**
   * Update sprite position (called after turn is over, after all tweens etc), and update the sprite
   * color based on cell they're in.
   *
   * @param entity The entity
   * @param cellX  x position
   * @param cellY  y position
   */
  public void updateSprite(Entity entity, float cellX, float cellY) {
    VisualComponent visual = ComponentMappers.visual.get(entity);
    visual.sprite.setPosition(cellX * Main.SPRITE_WIDTH, cellY * Main.SPRITE_HEIGHT);

    MapCell cell = WorldManager.mapHelpers.getCell(cellX, cellY);

    if (cell.isWater()) {
      Color tinted = visual.color.cpy().lerp(cell.sprite.getColor(), .5f);

      if (visual.sprite.getColor() != tinted) {
        visual.sprite.setColor(tinted);
      }
    } else {
      if (visual.sprite.getColor() != visual.color) {
        visual.sprite.setColor(visual.color);
      }
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
    } else {
      attributes.health = attributes.maxHealth;
    }

    if (ComponentMappers.player.has(entity)) {
      WorldManager.log.add("stats.healthRaised", "You", amount);

      ComponentMappers.player.get(entity).totalDamageHealed += amount;
    } else {
      WorldManager.log.add("stats.healthRaised", attributes.name, amount);
    }
  }

  /**
   * Take some damage.
   *
   * @param entity Who gettin' hurt
   * @param amount How much
   */
  public void takeDamage(Entity entity, int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    attributes.health -= amount;

    if (ComponentMappers.player.has(entity)) {
      ComponentMappers.player.get(entity).totalDamageReceived += amount;
    }
  }

  /**
   * Throw up a little bit.
   *
   * @param entity Who's vomiting
   * @param damage How much damage they take when they vomit
   */
  public void vomit(Entity entity, int damage) {
    takeDamage(entity, damage);

    PositionComponent position = ComponentMappers.position.get(entity);
    WorldManager.mapHelpers.makeFloorVomit(position.pos);
  }
}
