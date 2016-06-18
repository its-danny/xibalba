package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class VisualComponent implements Component {
  public Sprite sprite = null;
  public Animation animation = null;
  public float elapsedTime = 0;

  public VisualComponent(Sprite sprite) {
    this.sprite = sprite;
  }

  public VisualComponent(TextureAtlas textureAtlas) {
    this.animation = new Animation(.5f, textureAtlas.getRegions());
  }
}
