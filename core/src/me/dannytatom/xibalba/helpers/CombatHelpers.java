package me.dannytatom.xibalba.helpers;

import aurelienribon.tweenengine.Tween;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.components.items.ItemEffectsComponent;
import me.dannytatom.xibalba.components.items.WeaponComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.components.statuses.CrippledComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.SpriteAccessor;
import me.dannytatom.xibalba.world.WorldManager;

import java.util.Map;
import java.util.Objects;

public class CombatHelpers {
  public CombatHelpers() {

  }

  /**
   * Add MeleeComponent to player.
   *
   * @param enemy    Who ya hitting
   * @param bodyPart Where ya hitting them at
   */
  public void preparePlayerForMelee(Entity enemy, String bodyPart) {
    Gdx.app.log("CombatHelpers", "Preparing for melee");

    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);

    if (attributes.energy >= MeleeComponent.COST) {
      WorldManager.player.add(new MeleeComponent(enemy, bodyPart));
    }
  }

  /**
   * Add RangeComponent for throwing.
   *
   * @param position Where ya throwing
   * @param bodyPart Where you trying to hit em
   */
  public void preparePlayerForThrowing(Vector2 position, String bodyPart) {
    Gdx.app.log("CombatHelpers", "Preparing for throwing");

    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);

    if (attributes.energy >= RangeComponent.COST) {
      Entity item = WorldManager.itemHelpers.getThrowing(WorldManager.player);

      WorldManager.player.add(new RangeComponent(position, item, "throwing", bodyPart));
    }
  }

  /**
   * Add RangeComponent for range weapons.
   *
   * @param position Where ya throwing
   * @param bodyPart Where you trying to hit em
   */
  public void preparePlayerForRanged(Vector2 position, String bodyPart) {
    Gdx.app.log("CombatHelpers", "Preparing for ranged");

    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);

    if (attributes.energy >= RangeComponent.COST) {
      Entity primaryWeapon = WorldManager.itemHelpers.getRightHand(WorldManager.player);
      WeaponComponent weapon = ComponentMappers.weapon.get(primaryWeapon);

      Entity item = WorldManager.itemHelpers.getAmmunitionOfType(
          WorldManager.player, weapon.ammunitionType
      );
      ItemComponent itemDetails = ComponentMappers.item.get(item);

      WorldManager.player.add(
          new RangeComponent(position, item, itemDetails.skill, bodyPart)
      );
    }
  }

  /**
   * Entity's toughness + armor defense.
   *
   * @param entity Who we're getting defense of
   *
   * @return Their combined defense
   */
  private int getCombinedDefense(Entity entity) {
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
   * Melee combat logic.
   *
   * @param starter Who started the fight
   * @param target  Who's getting fought
   */
  public void melee(Entity starter, Entity target, String bodyPart) {
    Gdx.app.log("CombatHelpers", "Starting melee hit");

    Entity item = null;

    if (ComponentMappers.equipment.has(starter)) {
      item = WorldManager.itemHelpers.getRightHand(starter);
    }

    String skillName;
    String verb;

    if (item == null) {
      skillName = "unarmed";
      verb = "hit";
    } else {
      ItemComponent itemDetails = ComponentMappers.item.get(item);

      skillName = itemDetails.skill;
      verb = itemDetails.verbs.get(MathUtils.random(0, itemDetails.verbs.size - 1));
    }

    Gdx.app.log("Skill", skillName);

    SkillsComponent skills = ComponentMappers.skills.get(starter);

    int skillLevel = skills.levels.get(skillName);
    BodyComponent body = ComponentMappers.body.get(target);

    int hit = determineHit(skillLevel, body.parts.get(bodyPart));
    Gdx.app.log("CombatHelpers", "Hit roll: " + hit);

    AttributesComponent starterAttributes = ComponentMappers.attributes.get(starter);

    if (hit >= 4) {
      if (ComponentMappers.player.has(target)) {
        Main.cameraShake.shake(.4f, .1f);
      }

      if (item == null) {
        Main.soundManager.unarmed();
      } else {
        String skill = ComponentMappers.item.get(item).skill;

        switch (skill) {
          case "slashing":
            Main.soundManager.slashing();
            break;
          case "piercing":
            Main.soundManager.piercing();
            break;
          case "bashing":
            Main.soundManager.bashing();
            break;
          default:
            Main.soundManager.unarmed();
            break;
        }
      }

      doHitAnimation(starter, target);

      int strengthRoll = MathUtils.random(1, starterAttributes.strength);
      int weaponRoll = 0;
      int critRoll = 0;

      if (item != null) {
        ItemComponent weaponItem = ComponentMappers.item.get(item);
        weaponRoll = MathUtils.random(1, weaponItem.attributes.get("hitDamage"));
      }

      int skillLevelAmount = 20;

      if (hit >= 8) {
        critRoll = MathUtils.random(1, 6);
        skillLevelAmount = 40;

        Gdx.app.log("CombatHelpers", "Crit roll: " + critRoll);
      }

      int damage = strengthRoll + weaponRoll + critRoll;

      Gdx.app.log("CombatHelpers", "Starting damage: " + damage);

      applyDamage(starter, target, item, damage, verb, bodyPart);

      levelSkill(starter, skillName, skillLevelAmount);
    } else {
      AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

      WorldManager.log.add(
          starterAttributes.name + " tried to hit " + targetAttributes.name + " but missed"
      );
    }
  }

  /**
   * Ranged combat logic.
   *
   * @param starter Who started the fight
   * @param target  Who's getting fought
   * @param item    What they're being hit with
   */
  public void range(Entity starter, Entity target, String bodyPart, Entity item, String skill) {
    Gdx.app.log("CombatHelpers", "Starting range hit");
    Gdx.app.log("Skill", skill);

    SkillsComponent skills = ComponentMappers.skills.get(starter);
    ItemComponent itemDetails = ComponentMappers.item.get(item);

    String verb;

    if (Objects.equals(skill, "throwing")) {
      verb = "hit";
    } else {
      ItemComponent firingWeapon =
          ComponentMappers.item.get(WorldManager.itemHelpers.getRightHand(starter));
      verb = firingWeapon.verbs.get(MathUtils.random(0, firingWeapon.verbs.size - 1));
    }

    int skillLevel = skills.levels.get(skill);
    BodyComponent body = ComponentMappers.body.get(target);

    int hit = determineHit(skillLevel, body.parts.get(bodyPart));
    Gdx.app.log("CombatHelpers", "Hit roll: " + hit);

    AttributesComponent starterAttributes = ComponentMappers.attributes.get(starter);

    if (hit >= 4) {
      if (ComponentMappers.player.has(target)) {
        Main.cameraShake.shake(.4f, .1f);
      }

      doHitAnimation(starter, target);

      int weaponRoll;
      int critRoll = 0;

      if (Objects.equals(skill, "throwing")) {
        weaponRoll = MathUtils.random(1, itemDetails.attributes.get("throwDamage"));
      } else if (Objects.equals(skill, "archery")) {
        weaponRoll = MathUtils.random(1, itemDetails.attributes.get("shotDamage"));
      } else {
        weaponRoll = MathUtils.random(1, itemDetails.attributes.get("hitDamage"));
      }

      int skillLevelAmount = 20;

      if (hit >= 8) {
        critRoll = MathUtils.random(1, 6);
        skillLevelAmount = 40;

        Gdx.app.log("CombatHelpers", "Crit roll: " + critRoll);
      }

      int damage = weaponRoll + critRoll;

      Gdx.app.log("CombatHelpers", "Starting damage: " + damage);

      applyDamage(starter, target, item, damage, verb, bodyPart);

      levelSkill(starter, skill, skillLevelAmount);
    } else {
      AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

      WorldManager.log.add(
          starterAttributes.name + " tried to hit " + targetAttributes.name + " but missed"
      );
    }
  }

  private int determineHit(int skillLevel, int bodyPart) {
    // Roll your skill and a 6
    int skillRoll = MathUtils.random(1, skillLevel == 0 ? 4 : skillLevel);
    int extraRoll = MathUtils.random(1, 6);

    // Hit roll is whichever of the 2 is highest
    int hitRoll = extraRoll > skillRoll ? extraRoll : skillLevel;

    // Roll target body part
    int bodyPartRoll = MathUtils.random(1, bodyPart);

    // Return the hit roll if it's above the body part roll
    if (hitRoll > bodyPartRoll) {
      return hitRoll;
    } else {
      return 0;
    }
  }

  private void applyDamage(Entity starter, Entity target, Entity item, int damage,
                           String verb, String bodyPart) {
    AttributesComponent starterAttributes = ComponentMappers.attributes.get(starter);
    AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

    // Apply weapon effects
    if (item != null) {
      ItemComponent itemDetails = ComponentMappers.item.get(item);
      ItemEffectsComponent itemEffects = ComponentMappers.itemEffects.get(item);

      if (itemEffects != null) {
        for (Map.Entry<String, String> entry : itemEffects.effects.entrySet()) {
          String event = entry.getKey();
          String action = entry.getValue();

          if (Objects.equals(event, "onHit")) {
            WorldManager.entityHelpers.applyEffect(target, action);
          }
        }
      }

      PlayerComponent player = ComponentMappers.player.get(starter);

      if (player != null && !player.identifiedItems.contains(itemDetails.name, true)) {
        player.identifiedItems.add(itemDetails.name);
      }
    }

    int totalDamage = damage - getCombinedDefense(target);

    Gdx.app.log("CombatHelpers", "Final damage after factoring in target defense: " + totalDamage);

    if (totalDamage > 0) {
      BodyComponent targetBody = ComponentMappers.body.get(target);

      targetAttributes.health -= totalDamage;
      targetBody.damage.put(bodyPart, targetBody.damage.get(bodyPart) + totalDamage);

      WorldManager.log.add(
          starterAttributes.name + " " + verb + " "
              + targetAttributes.name + " in the " + bodyPart + " for " + totalDamage + " damage"
      );

      // If you've done damage to the body part equal to or more than a third their health,
      // apply statuses effect.
      //
      // TODO: Probably change this to make more sense
      if (targetBody.damage.get(bodyPart) > (targetAttributes.maxHealth / 3)) {
        if (bodyPart.contains("leg") && !ComponentMappers.crippled.has(target)) {
          target.add(new CrippledComponent());
          WorldManager.log.add("[RED]" + targetAttributes.name + " is crippled");
        } else if (bodyPart.contains("body") && !ComponentMappers.bleeding.has(target)) {
          target.add(new BleedingComponent());
          WorldManager.log.add("[RED]" + targetAttributes.name + " is bleeding");
        }
      }
    } else {
      WorldManager.log.add(
          starterAttributes.name + " " + verb + " "
              + targetAttributes.name + " in the " + bodyPart + " but did no damage"
      );
    }

    if (targetAttributes.health <= 0) {
      PositionComponent position = ComponentMappers.position.get(target);

      WorldManager.world.addEntity(WorldManager.entityFactory.createRemains(position.pos));
      WorldManager.world.removeEntity(target);

      if (ComponentMappers.player.has(starter)) {
        WorldManager.log.add("[GREEN]You killed " + targetAttributes.name + "!");
      } else {
        WorldManager.log.add("[RED]You have been killed by " + starterAttributes.name);
      }
    }
  }

  /**
   * JsonToLevel an entity's skill.
   *
   * @param entity Who we're leveling
   * @param skill  The skill we're leveling
   * @param amount How much we're giving 'em
   */
  private void levelSkill(Entity entity, String skill, int amount) {
    SkillsComponent skills = ComponentMappers.skills.get(entity);

    skills.counters.put(skill, skills.counters.get(skill) + amount);

    int skillLevel = skills.levels.get(skill);
    int expNeeded = skillLevel == 0 ? 40 : ((skillLevel + 2) * 10);

    if (skills.counters.get(skill) >= expNeeded && skillLevel < 12) {
      skills.levels.put(skill, skillLevel == 0 ? 4 : skillLevel + 2);
      skills.counters.put(skill, 0);

      if (ComponentMappers.player.has(entity)) {
        WorldManager.log.add("[YELLOW]You feel better at " + skill);
      }

      if (MathUtils.random() > .25) {
        AttributesComponent attributes = ComponentMappers.attributes.get(entity);

        switch (skills.associations.get(skill)) {
          case "strength":
            if (attributes.strength < 12) {
              attributes.strength = attributes.strength == 0 ? 4 : attributes.strength + 2;
            }

            break;
          case "toughness":
            if (attributes.toughness < 12) {
              attributes.toughness = attributes.toughness == 0 ? 4 : attributes.toughness + 2;
            }

            break;
          default:
        }

        if (ComponentMappers.player.has(entity)) {
          WorldManager.log.add("[YELLOW]Your " + skills.associations.get(skill) + " has risen");
        }
      }
    }
  }

  private void doHitAnimation(Entity starter, Entity target) {
    VisualComponent targetVisual = ComponentMappers.visual.get(target);
    Tween.to(targetVisual.sprite, SpriteAccessor.ALPHA, .05f).target(.5f)
        .repeatYoyo(1, 0f).start(Main.tweenManager);

    PositionComponent starterPosition = ComponentMappers.position.get(starter);
    PositionComponent targetPosition = ComponentMappers.position.get(target);
    float bumpX;
    float bumpY;

    if (starterPosition.pos.x < targetPosition.pos.x) {
      bumpX = targetVisual.sprite.getX() + (Main.SPRITE_WIDTH / 4);
    } else if (starterPosition.pos.x > targetPosition.pos.x) {
      bumpX = targetVisual.sprite.getX() - (Main.SPRITE_WIDTH / 4);
    } else {
      bumpX = targetVisual.sprite.getX();
    }

    if (starterPosition.pos.y < targetPosition.pos.y) {
      bumpY = targetVisual.sprite.getY() + (Main.SPRITE_HEIGHT / 4);
    } else if (starterPosition.pos.y > targetPosition.pos.y) {
      bumpY = targetVisual.sprite.getY() - (Main.SPRITE_HEIGHT / 4);
    } else {
      bumpY = targetVisual.sprite.getY();
    }

    Tween.to(targetVisual.sprite, SpriteAccessor.XY, .05f).target(bumpX, bumpY)
        .repeatYoyo(1, 0f).start(Main.tweenManager);
  }
}
