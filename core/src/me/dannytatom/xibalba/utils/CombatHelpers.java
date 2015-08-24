package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.effects.DamageEffectComponent;

import java.util.Objects;

// How combat works.
//
// - Each skill is 4, 6, 8, 10, or 12
// - You roll your skill and a 6, highest result is used
// - If the result is the highest it can be, roll that number again and add to it
// - If result is 4 or over, you hit
// - If result is 8 or over (critical), add a 6 roll to the damage
// - Damage is a static number + the 6 roll if you have it
// - Roll that, add together, they take damage equal to result - their toughness

public class CombatHelpers {
  private final Main main;
  private final Engine engine;

  /**
   * Initialize action log.
   *
   * @param main   Instance of the main class
   * @param engine Ashley engine
   */
  public CombatHelpers(Main main, Engine engine) {
    this.main = main;
    this.engine = engine;
  }

  /**
   * Melee combat logic.
   *
   * @param starter Who started the fight
   * @param target  Who's getting fought
   */
  public void melee(Entity starter, Entity target) {
    Entity wielded = main.equipmentHelpers.getWeapon();
    String skill;
    String verb;

    int damage = starter.getComponent(AttributesComponent.class).damage;

    if (wielded == null) {
      skill = "unarmed";
      verb = "hit";
    } else {
      ItemComponent ic = wielded.getComponent(ItemComponent.class);

      skill = ic.skill;
      damage += ic.attributes.get("damage");
      verb = ic.verbs.get(MathUtils.random(0, ic.verbs.size() - 1));
    }

    int result = rollHit(main.skillHelpers.getSkill(starter, skill));

    applyDamage(result, damage, skill, starter, target, verb);
  }

  /**
   * Ranged combat logic.
   *
   * @param starter Who started the fight
   * @param target  Who's getting fought
   * @param item    What they're being hit with
   */
  public void range(Entity starter, Entity target, Entity item) {
    ItemComponent itemComponent = item.getComponent(ItemComponent.class);

    int result = rollHit(main.skillHelpers.getSkill(starter, "throwing"));

    applyDamage(result, itemComponent.attributes.get("damage"), "throwing", starter, target, "hit");
  }

  /**
   * Effect logic.
   *
   * @param effect The effect, so we know what to do
   * @param target Who's getting hit by it
   */
  public void effect(Entity effect, Entity target) {
    DamageEffectComponent damageEffectComponent = effect.getComponent(DamageEffectComponent.class);
    Entity starter = damageEffectComponent.starter;

    int result = rollHit(main.skillHelpers.getSkill(starter, "throwing"));

    applyDamage(
        result, damageEffectComponent.damage, "throwing",
        starter, target, damageEffectComponent.type
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

  private void applyDamage(int result, int baseDamage, String skill, Entity starter, Entity target, String verb) {
    AttributesComponent starterAttributes = ComponentMappers.attributes.get(starter);
    AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

    String action = starterAttributes.name + " ";

    if (result >= 4) {
      int critical = 0;

      if (result >= 8) {
        critical = MathUtils.random(1, 6);
      }

      int damage = baseDamage + critical + main.equipmentHelpers.getWeaponDamage();

      if (damage > targetAttributes.toughness) {
        targetAttributes.health -= damage - targetAttributes.toughness;

        action += verb + " " + targetAttributes.name + " for " + damage + " damage";
      } else {
        action += "hit " + targetAttributes.name + " but did no damage";
      }

      main.skillHelpers.levelSkill(starter, skill, 20);
    } else {
      action += "missed " + targetAttributes.name;
    }

    // If we're using bombs or something that doesn't have verbs,
    // we use the type as an indicator of what to log.
    //
    // TODO: Pretty hacky, change this
    if (Objects.equals(verb, "poison")) {
      action = targetAttributes.name + " was hurt by the cloud of poison";
    }

    main.log.add(action);

    if (targetAttributes.health <= 0) {
      engine.removeEntity(target);
      main.log.add(starterAttributes.name + " killed " + targetAttributes.name + "!");
    }
  }
}
