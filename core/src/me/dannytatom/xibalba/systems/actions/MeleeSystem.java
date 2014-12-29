package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MeleeSystem extends IteratingSystem {
  private final Engine engine;
  private final Map map;

  public MeleeSystem(Engine engine, Map map) {
    super(Family.all(MeleeComponent.class).get());

    this.engine = engine;
    this.map = map;
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
    Entity target = map.getEntityAt(melee.target);

    if (target != null) {
      SkillsComponent skills = ComponentMappers.skills.get(entity);
      AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

      int skillRoll = MathUtils.random(1, skills.unarmedCombat);
      int sixRoll = MathUtils.random(1, 6);
      int result;

      if (skillRoll > sixRoll) {
        if (skillRoll == skills.unarmedCombat) {
          result = skillRoll + MathUtils.random(1, skills.unarmedCombat);
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
        int critical = 0;

        if (result >= 8) {
          critical = MathUtils.random(1, 6);
        }

        int damage = attributes.damage + critical;

        if (damage > targetAttributes.toughness) {
          targetAttributes.health -= damage - targetAttributes.toughness;
        }
      }

      if (targetAttributes.health <= 0) {
        skills.unarmedCombatCounter += 10;

        engine.removeEntity(target);
      }

      if (skills.unarmedCombatCounter >= (skills.unarmedCombat * 10) && skills.unarmedCombat < 12) {
        skills.unarmedCombat += 2;

        Gdx.app.log("Unarmed Combat", "" + skills.unarmedCombat);
      }

      attributes.energy -= MeleeComponent.COST;
    }

    entity.remove(MeleeComponent.class);
  }
}
