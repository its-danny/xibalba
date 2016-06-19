package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.systems.ActionSystem;
import me.dannytatom.xibalba.utils.ComponentMappers;

import java.util.Objects;

public class RangeSystem extends ActionSystem {
  private final Main main;
  private final Engine engine;
  private final Map map;

  /**
   * Handles range combat.
   *
   * @param main   Instance of the main class, needed for helpers*
   * @param engine Ashley engine
   * @param map    Map we're on
   */
  public RangeSystem(Main main, Engine engine, Map map) {
    super(Family.all(RangeComponent.class).get());

    this.main = main;
    this.engine = engine;
    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    RangeComponent range = ComponentMappers.range.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    Entity item = main.inventoryHelpers.getThrowingItem(entity);

    if (item != null) {
      ItemComponent ic = item.getComponent(ItemComponent.class);

      if (Objects.equals(ic.type, "weapon")) {
        Entity enemy = map.getEnemyAt(range.target);

        if (enemy != null) {
          main.combatHelpers.range(entity, enemy, item);
        }

        main.inventoryHelpers.dropItem(entity, item, range.target);
      } else if (Objects.equals(ic.type, "projectile")) {
        main.entityHelpers.spawnEffect(entity, range.target, item);

        main.inventoryHelpers.removeItem(entity, item);
        engine.removeEntity(item);
      }

      attributes.energy -= RangeComponent.COST;
    }

    entity.remove(RangeComponent.class);
  }
}
