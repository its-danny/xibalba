package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MeleeSystem extends ActionSystem {
  private final Main main;

  /**
   * Handles melee combat.
   *
   * @param main Instance of Main class
   */
  public MeleeSystem(Main main) {
    super(Family.all(MeleeComponent.class).get());

    this.main = main;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MeleeComponent melee = ComponentMappers.melee.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (melee.target != null) {
      main.combatHelpers.melee(entity, melee.target);
      attributes.energy -= MeleeComponent.COST;
    }

    entity.remove(MeleeComponent.class);
  }
}
