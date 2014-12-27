package me.dannytatom.xibalba.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.xibalba.components.MovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.ai.AttackComponent;
import me.dannytatom.xibalba.map.Map;

public class AttackSystem extends IteratingSystem {

  public AttackSystem(Map map) {
    super(Family.all(AttackComponent.class, PositionComponent.class,
        MovementComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {

  }
}
