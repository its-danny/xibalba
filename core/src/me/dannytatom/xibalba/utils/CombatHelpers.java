package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.ActionLog;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.effects.DamageEffectComponent;

import java.util.ArrayList;

public class CombatHelpers {
  private final Engine engine;
  private final ActionLog actionLog;
  private final EntityHelpers entityHelpers;
  private final InventoryHelpers inventoryHelpers;
  private final SkillHelpers skillHelpers;

  public CombatHelpers(Engine engine, ActionLog actionLog, EntityHelpers entityHelpers, InventoryHelpers inventoryHelpers, SkillHelpers skillHelpers) {
    this.engine = engine;
    this.actionLog = actionLog;
    this.entityHelpers = entityHelpers;
    this.inventoryHelpers = inventoryHelpers;
    this.skillHelpers = skillHelpers;
  }

  /**
   * How combat works:
   * <p>
   * - Each skill is 4, 6, 8, 10, or 12
   * - You roll your skill and a 6, highest result is used
   * - If the result is the highest it can be, roll that number again and add to it
   * - If result is 4 or over, you hit
   * - If result is 8 or over (critical), add a 6 roll to the damage
   * - Damage is a static number + the 6 roll if you have it
   * - Roll that, add together, they take damage equal to result - their toughness
   */
  public void melee(Entity entity, Entity target) {
    AttributesComponent entityAttributes = ComponentMappers.attributes.get(entity);
    AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);
    Entity wielded = inventoryHelpers.getWieldedItem();
    String skillName;

    if (wielded != null) {
      skillName = wielded.getComponent(ItemComponent.class).skill;
    } else {
      skillName = "unarmed";
    }

    int skillLevel = skillHelpers.getSkill(entity, skillName);

    String name = entityHelpers.isPlayer(entity) ? "You" : entityAttributes.name;
    String targetName = entityHelpers.isPlayer(target) ? "You" : targetAttributes.name;
    String action = name + " ";

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

    if (result >= 4) {
      String verb = "hit";

      if (entityHelpers.isPlayer(entity) && wielded != null) {
        ArrayList<String> verbs = wielded.getComponent(ItemComponent.class).verbs;
        verb = verbs.get(MathUtils.random(0, verbs.size() - 1));
      }

      int critical = 0;

      if (result >= 8) {
        critical = MathUtils.random(1, 6);
      }

      int damage = entityHelpers.getDamage(entity) + critical;

      if (damage > targetAttributes.toughness) {
        targetAttributes.health -= damage - targetAttributes.toughness;

        action += verb + " " + targetName + " for " + damage + " damage";
      } else {
        action += "hit " + targetName + " but did no damage";
      }

      skillHelpers.levelSkill(entity, skillName, 4);
    } else {
      action += "missed " + targetName;
    }

    actionLog.add(action);

    if (targetAttributes.health <= 0) {
      engine.removeEntity(target);
      actionLog.add(name + " killed " + targetName + "!");
    }
  }

  public void range(Entity entity, Entity target, Entity item) {
    AttributesComponent entityAttributes = ComponentMappers.attributes.get(entity);
    AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

    int skillLevel = skillHelpers.getSkill(entity, "throwing");

    String name = entityHelpers.isPlayer(entity) ? "You" : entityAttributes.name;
    String targetName = entityHelpers.isPlayer(target) ? "You" : targetAttributes.name;
    String action = name + " ";

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

    if (result >= 4) {
      String verb = "hit";

      int critical = 0;

      if (result >= 8) {
        critical = MathUtils.random(1, 6);
      }

      int damage = item.getComponent(ItemComponent.class).attributes.get("damage") + critical;

      if (damage > targetAttributes.toughness) {
        targetAttributes.health -= damage - targetAttributes.toughness;

        action += verb + " " + targetName + " for " + damage + " damage";
      } else {
        action += "hit " + targetName + " but did no damage";
      }

      skillHelpers.levelSkill(entity, "throwing", 4);
    } else {
      action += "missed " + targetName;
    }

    actionLog.add(action);

    if (targetAttributes.health <= 0) {
      engine.removeEntity(target);
      actionLog.add(name + " killed " + targetName + "!");
    }
  }

  public void effect(Entity effect, Entity target) {
    DamageEffectComponent damageEffectComponent = effect.getComponent(DamageEffectComponent.class);
    Entity starter = damageEffectComponent.starter;
    AttributesComponent entityAttributes = ComponentMappers.attributes.get(starter);
    AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

    int skillLevel = skillHelpers.getSkill(starter, "throwing");

    String name = entityHelpers.isPlayer(starter) ? "You" : entityAttributes.name;
    String targetName = entityHelpers.isPlayer(target) ? "You" : targetAttributes.name;
    String action = name + " ";

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

    if (result >= 4) {
      String verb = "hit";

      int critical = 0;

      if (result >= 8) {
        critical = MathUtils.random(1, 6);
      }

      int damage = damageEffectComponent.damage + critical;

      if (damage > targetAttributes.toughness) {
        targetAttributes.health -= damage - targetAttributes.toughness;

        action += verb + " " + targetName + " for " + damage + " damage";
      } else {
        action += "hit " + targetName + " but did no damage";
      }

      skillHelpers.levelSkill(starter, "throwing", 4);
    } else {
      action += "missed " + targetName;
    }

    actionLog.add(action);

    if (targetAttributes.health <= 0) {
      engine.removeEntity(target);
      actionLog.add(name + " killed " + targetName + "!");
    }
  }
}
