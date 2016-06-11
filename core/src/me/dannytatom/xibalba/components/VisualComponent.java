package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class VisualComponent implements Component {
    public Sprite sprite = null;
    public Animation animation = null;
    public float elapsedTime = 0;

    /**
     * Holds visual data.
     *
     * @param sprite       The sprite object
     * @param textureAtlas Texture atlas
     */
    public VisualComponent(Sprite sprite, TextureAtlas textureAtlas) {
        if (sprite != null) {
            this.sprite = sprite;
        } else {
            this.animation = new Animation(.5f, textureAtlas.getRegions());
        }
    }
}
