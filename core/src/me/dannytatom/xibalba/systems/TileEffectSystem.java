package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.statuses.BurningComponent;
import me.dannytatom.xibalba.components.statuses.DrowningComponent;
import me.dannytatom.xibalba.components.statuses.StuckComponent;
import me.dannytatom.xibalba.components.statuses.WetComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class TileEffectSystem extends UsesEnergySystem {
  public TileEffectSystem() {
    super(Family.all(PositionComponent.class, AttributesComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (WorldManager.mapHelpers.getCell(position.pos.x, position.pos.y).isWater()) {
      entity.add(new WetComponent());
    }

    if (WorldManager.mapHelpers.getCell(position.pos.x, position.pos.y).isDeepWater()) {
      if (!WorldManager.entityHelpers.isAquatic(entity)) {
        if (attributes.oxygen >= 2) {
          attributes.oxygen -= 2;
        }

        if (attributes.oxygen == 0 && !ComponentMappers.drowning.has(entity)) {
          entity.add(new DrowningComponent());
        }
      }
    }

    if (WorldManager.mapHelpers.getCell(position.pos.x, position.pos.y).onFire) {
      entity.add(new BurningComponent(5));
    }

    Entity trap = WorldManager.mapHelpers.getTrapAt(position.pos);

    if (trap != null) {
      if (ComponentMappers.spiderWeb.has(trap)) {
        entity.add(new StuckComponent());
        WorldManager.world.removeEntity(trap);
      }
    }
  }
}
