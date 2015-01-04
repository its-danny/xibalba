package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.CombatHelpers;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MeleeSystem extends ActionSystem {
  private final CombatHelpers combatHelpers;

  public MeleeSystem(CombatHelpers combatHelpers) {
    super(Family.all(MeleeComponent.class).get());

    this.combatHelpers = combatHelpers;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MeleeComponent melee = ComponentMappers.melee.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (melee.target != null) {
      combatHelpers.melee(entity, melee.target);
      attributes.energy -= MeleeComponent.COST;
    }

    entity.remove(MeleeComponent.class);
  }
}
