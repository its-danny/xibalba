package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.SkillsComponent;

public class SkillHelpers {
  private final Main main;

  public SkillHelpers(Main main) {
    this.main = main;
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

      if (ComponentMappers.player.get(entity) != null) {
        main.log.add("[YELLOW]You feel better at " + skill);
      }
    }
  }
}
