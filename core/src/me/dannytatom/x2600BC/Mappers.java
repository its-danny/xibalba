package me.dannytatom.x2600BC;

import com.badlogic.ashley.core.ComponentMapper;
import me.dannytatom.x2600BC.components.*;

public final class Mappers {
  public static final ComponentMapper<BrainComponent> brain =
      ComponentMapper.getFor(BrainComponent.class);

  public static final ComponentMapper<PositionComponent> position =
      ComponentMapper.getFor(PositionComponent.class);

  public static final ComponentMapper<MovementComponent> movement =
      ComponentMapper.getFor(MovementComponent.class);

  public static final ComponentMapper<VisualComponent> visual =
      ComponentMapper.getFor(VisualComponent.class);

  public static final ComponentMapper<AttributesComponent> attributes =
      ComponentMapper.getFor(AttributesComponent.class);
}
