package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class VisualComponent implements Component {
  public Sprite sprite = null;

  public VisualComponent(Sprite sprite) {
    this.sprite = sprite;
  }
}
