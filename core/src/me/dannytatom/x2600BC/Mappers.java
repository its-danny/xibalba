package me.dannytatom.x2600BC;

import com.badlogic.ashley.core.ComponentMapper;
import me.dannytatom.x2600BC.components.AttributesComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.components.VisualComponent;

public final class Mappers {
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VisualComponent> visual = ComponentMapper.getFor(VisualComponent.class);
    public static final ComponentMapper<AttributesComponent> attributes = ComponentMapper.getFor(AttributesComponent.class);
}
