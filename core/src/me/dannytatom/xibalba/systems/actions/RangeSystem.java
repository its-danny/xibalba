package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.CombatHelpers;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;

public class RangeSystem extends ActionSystem {
  private final EntityHelpers entityHelpers;
  private final CombatHelpers combatHelpers;
  private final InventoryHelpers inventoryHelpers;

  public RangeSystem(EntityHelpers entityHelpers, CombatHelpers combatHelpers, InventoryHelpers inventoryHelpers) {
    super(Family.all(RangeComponent.class).get());

    this.entityHelpers = entityHelpers;
    this.combatHelpers = combatHelpers;
    this.inventoryHelpers = inventoryHelpers;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    RangeComponent range = ComponentMappers.range.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (range.target != null) {
      if (entityHelpers.isEnemy(range.target)) {
        combatHelpers.fight(entity, range.target, true);
      }

      inventoryHelpers.dropItem(range.target.getComponent(PositionComponent.class).pos);
      attributes.energy -= RangeComponent.COST;
    }

    entity.remove(RangeComponent.class);
  }
}
