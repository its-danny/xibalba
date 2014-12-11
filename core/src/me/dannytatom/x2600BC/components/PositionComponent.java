package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent extends Component {
    public int x;
    public int y;
    public String moveDir;

    public PositionComponent(int x, int y) {
        this.x = x;
        this.y = y;
        moveDir = null;
    }
}
