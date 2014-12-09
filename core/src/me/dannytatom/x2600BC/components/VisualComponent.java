package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class VisualComponent extends Component {
    public Sprite sprite;

    public VisualComponent(Texture texture) {
        this.sprite = new Sprite(texture);
    }
}
