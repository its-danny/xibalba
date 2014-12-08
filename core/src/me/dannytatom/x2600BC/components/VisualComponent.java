package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class VisualComponent extends Component {
    public Sprite sprite;

    public VisualComponent(String path) {
        this.sprite = new Sprite(new Texture(path));
    }
}
