package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.ComponentMapper;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;

public final class ComponentMappers {
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

    public static final ComponentMapper<TargetComponent> target =
            ComponentMapper.getFor(TargetComponent.class);

    public static final ComponentMapper<MeleeComponent> melee =
            ComponentMapper.getFor(MeleeComponent.class);

    public static final ComponentMapper<RangeComponent> range =
            ComponentMapper.getFor(RangeComponent.class);
}
