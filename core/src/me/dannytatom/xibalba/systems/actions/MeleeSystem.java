package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.ActionLog;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;
import me.dannytatom.xibalba.utils.SkillHelpers;

import java.util.ArrayList;
import java.util.Comparator;

public class MeleeSystem extends SortedIteratingSystem {
  private final ActionLog actionLog;
  private final Engine engine;
  private final EntityHelpers entityHelpers;
  private final InventoryHelpers inventoryHelpers;
  private final SkillHelpers skillHelpers;

  public MeleeSystem(Engine engine, ActionLog actionLog, EntityHelpers entityHelpers, InventoryHelpers inventoryHelpers, SkillHelpers skillHelpers) {
    super(Family.all(MeleeComponent.class).get(), new EnergyComparator());

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
   *
   * @param entity    Entity to process
   * @param deltaTime Delta
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MeleeComponent melee = ComponentMappers.melee.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (melee.target != null) {
      Entity wielded = inventoryHelpers.getWieldedItem();
      AttributesComponent targetAttributes = ComponentMappers.attributes.get(melee.target);
      String skillName;

      if (wielded != null) {
        skillName = wielded.getComponent(ItemComponent.class).skill;
      } else {
        skillName = "unarmed";
      }

      int skillLevel = skillHelpers.getSkill(entity, skillName);

      String name = entityHelpers.isPlayer(entity) ? "You" : attributes.name;
      String targetName = entityHelpers.isPlayer(melee.target) ? "You" : targetAttributes.name;
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

          action += verb +  " " + targetName + " for " + damage + " damage";
        } else {
          action += "hit " + targetName + " but did no damage";
        }
      } else {
        action += "missed " + targetName;
      }

      actionLog.add(action);

      if (targetAttributes.health <= 0) {
        engine.removeEntity(melee.target);
        actionLog.add(name + " killed " + targetName + "!");
        skillHelpers.levelSkill(entity, skillName, 10);
      }

      attributes.energy -= MeleeComponent.COST;
    }

    entity.remove(MeleeComponent.class);
  }

  private static class EnergyComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity e1, Entity e2) {
      AttributesComponent a1 = e1.getComponent(AttributesComponent.class);
      AttributesComponent a2 = e2.getComponent(AttributesComponent.class);

      if (a2.energy > a1.energy) {
        return 1;
      } else if (a1.energy > a2.energy) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
