package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class RangeSystem extends ActionSystem {
  private final Main main;

  /**
   * Handles range combat.
   *
   * @param main Instance of the main class, needed for helpers*
   * @param map  Map we're on
   */
  public RangeSystem(Main main) {
    super(Family.all(RangeComponent.class).get());

    this.main = main;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    RangeComponent range = ComponentMappers.range.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    Entity item = main.inventoryHelpers.getThrowingItem(entity);

    if (item != null) {
      Entity enemy = main.getCurrentMap().getEnemyAt(range.target);

      if (enemy != null) {
        main.combatHelpers.range(entity, enemy, item);
      }

      main.inventoryHelpers.dropItem(entity, item, range.target);

      attributes.energy -= RangeComponent.COST;
    }

    entity.remove(RangeComponent.class);
  }
}
