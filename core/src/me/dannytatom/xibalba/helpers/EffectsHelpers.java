package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.WorldManager;

public class EffectsHelpers {
  public EffectsHelpers() {

  }

  /**
   * Apply an effect to an entity.
   *
   * @param entity The entity
   * @param effect The effect. This is a string like "raiseHealth:5" where the part before colon is
   *               the method on EffectsHelpers, and the part after is the parameters (split by
   *               commas)
   */
  public void applyEffect(Entity entity, String effect) {
    String[] split = effect.split(":");
    String name = split[0];
    String[] params = split[1].split(",");

    switch (name) {
      case "raiseHealth":
        WorldManager.entityHelpers.raiseHealth(entity, Integer.parseInt(params[0]));
        break;
      case "raiseStrength":
        WorldManager.entityHelpers.raiseStrength(entity, Integer.parseInt(params[0]));
        break;
      default:
    }
  }
}
