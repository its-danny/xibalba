package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.effects.DamageEffectComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.CombatHelpers;

public class EffectSystem extends IteratingSystem {
  private final Engine engine;
  private final Map map;
  private final CombatHelpers combatHelpers;

  public EffectSystem(Engine engine, Map map, CombatHelpers combatHelpers) {
    super(Family.all(DamageEffectComponent.class).get());

    this.engine = engine;
    this.map = map;
    this.combatHelpers = combatHelpers;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    DamageEffectComponent effect = entity.getComponent(DamageEffectComponent.class);
    Entity other = map.getMobAt(entity.getComponent(PositionComponent.class).pos);

    if (other != null) {
      if (effect.getClass() == DamageEffectComponent.class) {
        combatHelpers.effect(entity, other);
      }
    }

    effect.currentTurn += 1;

    if (effect.currentTurn == effect.turns + 1) {
      engine.removeEntity(entity);
    }
  }
}
