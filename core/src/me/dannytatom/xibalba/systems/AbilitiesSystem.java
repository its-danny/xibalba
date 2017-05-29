package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.components.AbilitiesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.yaml.AbilityData;

public class AbilitiesSystem extends UsesEnergySystem {
  public AbilitiesSystem() {
    super(Family.all(AbilitiesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    Array<AbilityData> abilities = ComponentMappers.abilities.get(entity).abilities;

    for (int i = 0; i < abilities.size; i++) {
      AbilityData abilityData = abilities.get(i);

      if (abilityData.counter < abilityData.recharge) {
        abilityData.counter += 1;
      }
    }
  }
}
