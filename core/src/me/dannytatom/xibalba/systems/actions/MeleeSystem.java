package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class MeleeSystem extends IteratingSystem {
  private final Engine engine;
  private final Map map;

  public MeleeSystem(Engine engine, Map map) {
    super(Family.all(MeleeComponent.class).get());

    this.engine = engine;
    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MeleeComponent melee = ComponentMappers.melee.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    Entity target = map.getEntityAt(melee.target);

    if (target != null) {
      AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);

      targetAttributes.health -= attributes.damage;

      if (targetAttributes.health <= 0) {
        engine.removeEntity(target);
      }

      attributes.energy -= MeleeComponent.COST;
    }

    entity.remove(MeleeComponent.class);
  }
}
