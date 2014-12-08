package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent extends Component {
    public int x;
    public int y;

    public boolean moveN = false;
    public boolean moveE = false;
    public boolean moveS = false;
    public boolean moveW = false;

    public PositionComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
