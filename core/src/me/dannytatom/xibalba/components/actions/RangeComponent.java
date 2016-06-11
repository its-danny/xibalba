package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class RangeComponent implements Component {
    public static final int COST = 100;

    public final Vector2 target;

    public RangeComponent(Vector2 target) {
        this.target = target;
    }
}
