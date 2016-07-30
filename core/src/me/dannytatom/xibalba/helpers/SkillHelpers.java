package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class SkillHelpers {
  public SkillHelpers() {

  }

  /**
   * Level an entity's skill.
   *
   * @param entity Who we're leveling
   * @param skill  The skill we're leveling
   * @param amount How much we're giving 'em
   */
  public void levelSkill(Entity entity, String skill, int amount) {
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
    }
  }
}
