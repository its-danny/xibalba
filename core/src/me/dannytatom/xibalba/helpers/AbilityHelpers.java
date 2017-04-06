package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.utils.YamlToAbility;
import me.dannytatom.xibalba.world.WorldManager;

public class AbilityHelpers {
  public AbilityHelpers() {

  }

  /**
   * Do an ability.
   *
   * @param entity  The entity who wants to do something.
   * @param ability The ability they want to do
   */
  public void doAbility(Entity entity, YamlToAbility ability) {
    if (ability.counter == ability.recharge) {
      String[] split = ability.effect.split(":");
      String name = split[0];
      String[] params = split[1].split(",");

      switch (name) {
        case "healSelf":
          WorldManager.entityHelpers.raiseHealth(entity, Integer.parseInt(params[0]));
          break;
        default:
      }

      ability.counter = 0;
    }
  }
}
