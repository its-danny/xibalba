package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.components.statuses.CrippledComponent;

import java.util.Objects;

//
// How combat works
// ----------------
//
// Hitting
// - Target number is 4
// - Roll skill and a 6, highest result is used as your hit roll
//    - If you have no level in the skill, roll a 4
// - Roll body part you're attempting to hit
// - If you roll over their body part roll, continue w/ hit roll
// - If the hit roll result is the target number or higher, you hit
//
// Damage
// - Before damage, apply weapon effects if any
// - Melee damage is strength roll + the weapon's damage roll
// - Ranged damage is just the weapon's damage roll
//   - If thrown, use throwDamage, otherwise hitDamage
// - If your hit roll was double the target number, you critical (add a 6 roll to the damage)
// - Damage done to the enemy is your total damage over their defense
//   - This is done to their actual health and to the body part damage
// - Body part damage is checked, add statuses effects to entity if necessary
//

public class CombatHelpers {
  private final Main main;

  /**
   * Initialize action log.
   *
   * @param main Instance of the main class
   */
  public CombatHelpers(Main main) {
    this.main = main;
  }

  /**
   * Add MeleeComponent to player.
   *
   * @param enemy    Who ya hitting
   * @param bodyPart Where ya hitting them at
   */
  public void preparePlayerForMelee(Entity enemy, String bodyPart) {
    AttributesComponent attributes = ComponentMappers.attributes.get(main.player);

    if (attributes.energy >= MeleeComponent.COST) {
      main.player.add(new MeleeComponent(enemy, bodyPart));
    }
  }

  /**
   * Add RangeComponent for throwing.
   *
   * @param position Where ya throwing
   * @param bodyPart Where you trying to hit em
   */
  public void preparePlayerForThrowing(Vector2 position, String bodyPart) {
    AttributesComponent attributes = ComponentMappers.attributes.get(main.player);

    if (attributes.energy >= RangeComponent.COST) {
      Entity item = main.inventoryHelpers.getThrowingItem(main.player);

      main.player.add(new RangeComponent(position, item, "throwing", bodyPart));
    }
  }

  /**
   * Add RangeComponent for range weapons.
   *
   * @param position Where ya throwing
   * @param bodyPart Where you trying to hit em
   */
  public void preparePlayerForRanged(Vector2 position, String bodyPart) {
    AttributesComponent attributes = ComponentMappers.attributes.get(main.player);

    if (attributes.energy >= RangeComponent.COST) {
      Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);
      ItemComponent primaryWeaponDetails = ComponentMappers.item.get(primaryWeapon);

      Entity item = main.inventoryHelpers.getAmmunitionOfType(
          main.player, primaryWeaponDetails.ammunitionType
      );

      main.player.add(new RangeComponent(position, item, primaryWeaponDetails.skill, bodyPart));
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
    Entity weapon = null;

    if (ComponentMappers.equipment.has(starter)) {
      weapon = main.equipmentHelpers.getPrimaryWeapon(starter);
    }

    String skillName;
    String verb;

    if (weapon == null) {
      skillName = "unarmed";
      verb = "hit";
    } else {
      ItemComponent weaponItem = ComponentMappers.item.get(weapon);

      skillName = weaponItem.skill;
      verb = weaponItem.verbs.get(MathUtils.random(0, weaponItem.verbs.size() - 1));
    }

    SkillsComponent skills = ComponentMappers.skills.get(starter);

    int skillLevel = skills.levels.get(skillName);
    BodyComponent body = ComponentMappers.body.get(target);

    int hit = determineHit(skillLevel, body.parts.get(bodyPart));

    AttributesComponent starterAttributes = ComponentMappers.attributes.get(starter);

    if (hit >= 4) {
      int strengthRoll = MathUtils.random(1, starterAttributes.strength);
      int weaponRoll = 0;
      int critRoll = 0;

      if (weapon != null) {
        ItemComponent weaponItem = ComponentMappers.item.get(weapon);
        weaponRoll = MathUtils.random(1, weaponItem.attributes.get("hitDamage"));
      }

      int skillLevelAmount = 20;

      if (hit >= 8) {
        critRoll = MathUtils.random(1, 6);
        skillLevelAmount = 40;
      }

      int damage = strengthRoll + weaponRoll + critRoll;

      applyDamage(starter, target, weapon, damage, verb, bodyPart);

      main.skillHelpers.levelSkill(starter, skillName, skillLevelAmount);
    } else {
      AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

      main.log.add(
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
    SkillsComponent skills = ComponentMappers.skills.get(starter);
    ItemComponent itemDetails = ComponentMappers.item.get(item);

    String verb;

    if (Objects.equals(skill, "throwing")) {
      verb = "hit";
    } else {
      ItemComponent firingWeapon =
          ComponentMappers.item.get(main.equipmentHelpers.getPrimaryWeapon(starter));
      verb = firingWeapon.verbs.get(MathUtils.random(0, firingWeapon.verbs.size() - 1));
    }

    int skillLevel = skills.levels.get(skill);
    BodyComponent body = ComponentMappers.body.get(target);

    int hit = determineHit(skillLevel, body.parts.get(bodyPart));

    AttributesComponent starterAttributes = ComponentMappers.attributes.get(starter);

    if (hit >= 4) {
      int weaponRoll;
      int critRoll = 0;

      if (Objects.equals(skill, "throwing")) {
        weaponRoll = MathUtils.random(1, itemDetails.attributes.get("throwDamage"));
      } else {
        weaponRoll = MathUtils.random(1, itemDetails.attributes.get("hitDamage"));
      }

      int skillLevelAmount = 20;

      if (hit >= 8) {
        critRoll = MathUtils.random(1, 6);
        skillLevelAmount = 40;
      }

      int damage = weaponRoll + critRoll;

      applyDamage(starter, target, item, damage, verb, bodyPart);

      main.skillHelpers.levelSkill(starter, skill, skillLevelAmount);
    } else {
      AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

      main.log.add(
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

      if (itemDetails.attributes.get("raiseHealth") != null) {
        main.entityHelpers.raiseHealth(target, itemDetails.attributes.get("raiseHealth"));
        itemDetails.attributes.remove("raiseHealth");
      }

      if (itemDetails.attributes.get("raiseStrength") != null) {
        main.entityHelpers.raiseStrength(target, itemDetails.attributes.get("raiseStrength"));
        itemDetails.attributes.remove("raiseStrength");
      }
    }

    int totalDamage = damage - getCombinedDefense(target);

    if (totalDamage > 0) {
      BodyComponent targetBody = ComponentMappers.body.get(target);

      targetAttributes.health -= totalDamage;
      targetBody.damage.put(bodyPart, targetBody.damage.get(bodyPart) + totalDamage);

      main.log.add(
          starterAttributes.name + " " + verb + " "
              + targetAttributes.name + " in the " + bodyPart + " for " + totalDamage + " damage"
      );

      // If you've done damage to the body part equal to or more than a third their health,
      // apply statuses effect.
      //
      // TODO: Probably change this to make more sense
      if (targetBody.damage.get(bodyPart) > (targetAttributes.maxHealth / 3)) {
        if (bodyPart.contains("leg") && !main.entityHelpers.isCrippled(target)) {
          target.add(new CrippledComponent());
        } else if (bodyPart.contains("body") && !main.entityHelpers.isBleeding(target)) {
          target.add(new BleedingComponent());
        }
      }
    } else {
      main.log.add(
          starterAttributes.name + " " + verb + " "
              + targetAttributes.name + " in the " + bodyPart + " but did no damage"
      );
    }

    if (targetAttributes.health <= 0) {
      TextureAtlas atlas = main.assets.get("sprites/main.atlas");
      Entity remains = new Entity();
      remains.add(new DecorationComponent());
      remains.add(new PositionComponent(
          main.world.currentMapIndex, ComponentMappers.position.get(target).pos
      ));
      remains.add(new VisualComponent(
          atlas.createSprite("Level/Cave/Environment/Object/Remains-1")
      ));

      main.engine.addEntity(remains);
      main.engine.removeEntity(target);

      if (ComponentMappers.player.has(starter)) {
        main.log.add("[GREEN]You killed " + targetAttributes.name + "!");
      } else {
        main.log.add("[RED]You have been killed by " + starterAttributes.name);
      }
    }
  }
}
