package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.CombatHelpers;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;

import java.util.Objects;

public class RangeSystem extends ActionSystem {
  private final Engine engine;
  private final Map map;
  private final EntityHelpers entityHelpers;
  private final CombatHelpers combatHelpers;
  private final InventoryHelpers inventoryHelpers;

  public RangeSystem(Engine engine, Map map, EntityHelpers entityHelpers, CombatHelpers combatHelpers, InventoryHelpers inventoryHelpers) {
    super(Family.all(RangeComponent.class).get());

    this.engine = engine;
    this.map = map;
    this.entityHelpers = entityHelpers;
    this.combatHelpers = combatHelpers;
    this.inventoryHelpers = inventoryHelpers;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    RangeComponent range = ComponentMappers.range.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    Entity item = inventoryHelpers.getShowing();

    if (item != null) {
      ItemComponent ic = item.getComponent(ItemComponent.class);

      if (Objects.equals(ic.type, "weapon")) {
        Entity enemy = map.getEnemyAt(range.target);

        if (enemy != null) {
          combatHelpers.range(entity, enemy, item);
        }

        inventoryHelpers.dropItem(range.target);
      } else if (Objects.equals(ic.type, "projectile")) {
        entityHelpers.spawnEffect(entity, range.target, item);

        inventoryHelpers.removeItem();
        engine.removeEntity(item);
      }

      attributes.energy -= RangeComponent.COST;
    }

    entity.remove(RangeComponent.class);
  }
}
