package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import java.util.HashMap;

import me.dannytatom.xibalba.abilities.Ability;
import me.dannytatom.xibalba.components.AbilitiesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;


public class AbilitiesSystem extends UsesEnergySystem {
  public AbilitiesSystem() {
    super(Family.all(AbilitiesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    HashMap<String, Ability> abilities = ComponentMappers.abilities.get(entity).abilities;

    abilities.forEach((name, ability) -> {
      if (ability.counter < ability.recharge) {
        ability.counter += 1;
      }
    });
  }
}
