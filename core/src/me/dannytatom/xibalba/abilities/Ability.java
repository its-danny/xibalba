package me.dannytatom.xibalba.abilities;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.effects.Effect;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class Ability {
  public Type type;
  public String name;
  public String description;
  public boolean targetRequired;
  public AttributesComponent.Type targetType;
  public ArrayList<Effect> effects;
  public int recharge;
  public int counter;

  /**
   * Do the ability.
   *
   * @param caster Who is using the ability
   * @param target Who the ability is acting on
   */
  public void act(Entity caster, Entity target) {
    for (Effect effect : effects) {
      if (this.counter == this.recharge) {
        if (this.targetRequired) {
          if (target == null) {
            WorldManager.log.add("effects.requiresTarget", this.name);

            return;
          }

          if (this.targetType != null
              && ComponentMappers.attributes.get(target).type != this.targetType) {
            WorldManager.log.add("effects.failed", this.name);

            return;
          }
        }

        effect.act(caster, target);

        this.counter = 0;
        WorldManager.executeTurn = true;
      } else {
        WorldManager.log.add("effects.failed", this.name);
      }
    }
  }

  public enum Type {
    ACTIVE, PASSIVE
  }
}
