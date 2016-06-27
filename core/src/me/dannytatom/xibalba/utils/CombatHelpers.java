package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;

import java.util.Objects;

// How combat works.
//
// - Each skill is 4, 6, 8, 10, or 12
// - You roll your skill and a 6, highest result is used
// - If the result is the highest it can be, roll that number again and add to it
// - If result is 4 or over, you hit
// - If result is 8 or over (critical), add a 6 roll to the damage
// - Damage is a static number + the 6 roll if you have it
// - Roll that, add together, they take damage equal to result - their defense

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
   * Melee combat logic.
   *
   * @param starter Who started the fight
   * @param target  Who's getting fought
   */
  public void melee(Entity starter, Entity target) {
    String skill;
    String verb;

    int damage = starter.getComponent(AttributesComponent.class).damage;

    Entity heldWeapon = null;

    if (starter.getComponent(EquipmentComponent.class) != null) {
      heldWeapon = main.equipmentHelpers.getPrimaryWeapon(starter);
    }

    if (heldWeapon == null) {
      skill = "unarmed";
      verb = "hit";
    } else {
      ItemComponent ic = heldWeapon.getComponent(ItemComponent.class);

      skill = ic.skill;
      damage += ic.attributes.get("hitDamage");
      verb = ic.verbs.get(MathUtils.random(0, ic.verbs.size() - 1));
    }

    int result = rollHit(main.skillHelpers.getSkillValue(starter, skill));

    applyDamage(result, damage, skill, starter, target, verb);
  }

  /**
   * Ranged combat logic.
   *
   * @param starter Who started the fight
   * @param target  Who's getting fought
   * @param item    What they're being hit with
   */
  public void range(Entity starter, Entity target, Entity item, String skill) {
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);

    int result = rollHit(main.skillHelpers.getSkillValue(starter, skill));
    int damage = Objects.equals(skill, "throwing")
        ? itemComponent.attributes.get("throwDamage")
        : itemComponent.attributes.get("hitDamage");

    applyDamage(
        result, damage, skill, starter, target, "hit"
    );
  }

  private int rollHit(int skillLevel) {
    int skillRoll = skillLevel == 0 ? 0 : MathUtils.random(1, skillLevel);
    int sixRoll = MathUtils.random(1, 6);
    int result;

    if (skillRoll > sixRoll) {
      if (skillRoll == skillLevel) {
        result = skillRoll + MathUtils.random(1, skillLevel);
      } else {
        result = skillRoll;
      }
    } else {
      if (sixRoll == 6) {
        result = sixRoll + MathUtils.random(1, 6);
      } else {
        result = sixRoll;
      }
    }

    return result;
  }

  private void applyDamage(int result, int baseDamage, String skill,
                           Entity starter, Entity target, String verb) {
    AttributesComponent starterAttributes = ComponentMappers.attributes.get(starter);
    AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

    boolean starterIsPlayer = starter.getComponent(PlayerComponent.class) != null;

    String action = (starterIsPlayer ? "You" : starterAttributes.name) + " ";

    if (result >= 4) {
      int critical = 0;

      if (result >= 8) {
        critical = MathUtils.random(1, 6);
      }

      int damage = baseDamage + critical;
      int defense = targetAttributes.defense;

      if (target.getComponent(EquipmentComponent.class) != null) {
        defense = main.equipmentHelpers.getCombinedDefense(target);
      }

      if (damage > defense) {
        targetAttributes.health -= damage - targetAttributes.defense;

        if (starterIsPlayer) {
          action = "[GREEN]" + action;
        } else {
          action = "[RED]" + action;
        }

        action += verb + " " + targetAttributes.name + " for " + damage + " damage";
      } else {
        action += "hit " + targetAttributes.name + " but did no damage";
      }

      main.skillHelpers.levelSkill(starter, skill, 20);

      if (critical > 0) {
        Vector2 splatterSpace = main.mapHelpers.getEmptySpaceNearEntity(
            target.getComponent(PositionComponent.class).pos
        );

        if (splatterSpace != null) {
          TextureAtlas atlas = main.assets.get("sprites/main.atlas");
          Entity remains = new Entity();
          remains.add(new DecorationComponent());
          remains.add(new VisualComponent(atlas.createSprite("Level/Cave/FX/BloodSplatter-1")));
          remains.add(new PositionComponent(main.currentMapIndex, splatterSpace));

          main.engine.addEntity(remains);
        }
      }
    } else {
      action += "missed " + targetAttributes.name;
    }

    main.log.add(action);

    if (targetAttributes.health <= 0) {
      TextureAtlas atlas = main.assets.get("sprites/main.atlas");
      Entity remains = new Entity();
      remains.add(new DecorationComponent());
      remains.add(new PositionComponent(
          main.currentMapIndex, target.getComponent(PositionComponent.class).pos
      ));
      remains.add(new VisualComponent(
          atlas.createSprite("Level/Cave/Environment/Object/Remains-1")
      ));

      main.engine.addEntity(remains);
      main.engine.removeEntity(target);

      if (starterIsPlayer) {
        main.log.add("[GREEN]You killed " + targetAttributes.name + "!");
      } else {
        main.log.add("[RED]You have been killed by " + starterAttributes.name);
      }
    }
  }
}
