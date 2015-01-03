package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.ActionLog;
import me.dannytatom.xibalba.components.SkillsComponent;

import java.lang.reflect.Field;

public class SkillHelpers {
  private final ActionLog actionLog;

  public SkillHelpers(ActionLog actionLog) {
    this.actionLog = actionLog;
  }

  public int getSkill(Entity entity, String skill) {
    SkillsComponent skills = entity.getComponent(SkillsComponent.class);
    Field field;
    int value = 0;

    try {
      field = skills.getClass().getField(skill);
      value = (int) field.get(skills);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }

    return value;
  }

  public void levelSkill(Entity entity, String skill, int amount) {
    SkillsComponent skills = entity.getComponent(SkillsComponent.class);
    Field skillField;
    Field counterField;

    try {
      skillField = skills.getClass().getField(skill);
      counterField = skills.getClass().getField(skill + "Counter");

      counterField.set(skills, (int) counterField.get(skills) + amount);

      int skillLevel = (int) skillField.get(skills);
      int expNeeded = skillLevel == 0 ? 40 : ((skillLevel + 2) * 10);

      if ((int) counterField.get(skills) >= expNeeded && skillLevel < 12) {
        skillField.set(skills, skillLevel == 0 ? 4 : skillLevel + 2);
        counterField.set(skills, 0);

        actionLog.add("You feel better at " + skill);
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
