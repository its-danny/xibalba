package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
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

      AttributesComponent attributes = ComponentMappers.attributes.get(entity);
      Entity target = null;

      if (ComponentMappers.player.has(entity)) {
        target = ComponentMappers.player.get(entity).lastHitEntity;
      } else if (ComponentMappers.brain.get(entity) != null) {
        target = ComponentMappers.brain.get(entity).target;
      }

      switch (name) {
        case "charmEnemy":
          if (abilityData.targetRequired && target == null) {
            WorldManager.log.add("effects.requiresTarget", abilityData.name);

            return;
          }

          if (abilityData.targetRequired
              && abilityData.targetType != ComponentMappers.attributes.get(target).type) {
            WorldManager.log.add("effects.failed", abilityData.name);


          }

          if (MathUtils.random((attributes.divineFavor / 100), 1) == 1) {
            WorldManager.entityHelpers.charm(entity, target, Integer.parseInt(params[0]));
          } else {
            if (ComponentMappers.player.has(entity)) {
              WorldManager.log.add("effects.failed", abilityData.name);
            }
          }

          break;
        default:
      }

      abilityData.counter = 0;
    }
  }
}
