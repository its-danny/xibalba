package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;

public class VisualComponent implements Component {
  public Sprite sprite = null;

  public VisualComponent(Sprite sprite, Vector2 position) {
    this(sprite, position, Color.WHITE);
  }

  public VisualComponent(Sprite sprite, Vector2 position, Color color) {
    this.sprite = sprite;
    this.sprite.setPosition(position.x * Main.SPRITE_WIDTH, position.y * Main.SPRITE_HEIGHT);
    this.sprite.setColor(color);
  }
}
