package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.utils.yaml.AbilityData;
import me.dannytatom.xibalba.world.WorldManager;

public class AbilityHelpers {
  public AbilityHelpers() {

  }

  /**
   * Do an abilityData.
   *
   * @param entity      The entity who wants to do something.
   * @param abilityData The abilityData they want to do
   */
  public void doAbility(Entity entity, AbilityData abilityData) {
    if (abilityData.counter == abilityData.recharge) {
      String[] split = abilityData.effect.split(":");
      String name = split[0];
      String[] params = split[1].split(",");

      switch (name) {
        case "healSelf":
          WorldManager.entityHelpers.raiseHealth(entity, Integer.parseInt(params[0]));
          break;
        default:
      }

      abilityData.counter = 0;
    }
  }
}
