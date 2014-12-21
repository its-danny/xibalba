package me.dannytatom.x2600BC.utils;

import com.badlogic.ashley.core.ComponentMapper;
import me.dannytatom.x2600BC.components.AttributesComponent;
import me.dannytatom.x2600BC.components.MovementComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.components.VisualComponent;
import me.dannytatom.x2600BC.components.ai.TargetComponent;
import me.dannytatom.x2600BC.components.ai.WanderComponent;

public final class ComponentMappers {
  public static final ComponentMapper<PositionComponent> position =
      ComponentMapper.getFor(PositionComponent.class);

  public static final ComponentMapper<MovementComponent> movement =
      ComponentMapper.getFor(MovementComponent.class);

  public static final ComponentMapper<VisualComponent> visual =
      ComponentMapper.getFor(VisualComponent.class);

  public static final ComponentMapper<AttributesComponent> attributes =
      ComponentMapper.getFor(AttributesComponent.class);

  public static final ComponentMapper<WanderComponent> wander =
      ComponentMapper.getFor(WanderComponent.class);

  public static final ComponentMapper<TargetComponent> target =
      ComponentMapper.getFor(TargetComponent.class);
}
