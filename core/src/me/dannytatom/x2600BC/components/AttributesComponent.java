package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class AttributesComponent extends Component {
    public int speed;
    public ArrayList<String> actions;

    public AttributesComponent(int speed) {
        this.speed = speed;
        this.actions = new ArrayList<>();
    }
}
