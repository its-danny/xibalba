package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import me.dannytatom.xibalba.Main;

public class VisualComponent implements Component {
  public Sprite sprite = null;
  public Color color = Color.WHITE;

  /**
   * Visual component, what's rendered.
   *
   * @param sprite   Sprite to render
   * @param position Where to render it
   */
  public VisualComponent(Sprite sprite, Vector2 position) {
    this(sprite, position, Color.WHITE, 1f);
  }

  /**
   * Visual component, what's rendered.
   *
   * @param sprite   Sprite to render
   * @param position Where to render it
   * @param color    The color to render
   */
  public VisualComponent(Sprite sprite, Vector2 position, Color color) {
    this(sprite, position, color, 1f);
  }

  /**
   * Visual component, what's rendered.
   *
   * @param sprite   Sprite to render
   * @param position Where to render it
   * @param color    The color to render
   * @param alpha    Alpha
   */
  public VisualComponent(Sprite sprite, Vector2 position, Color color, float alpha) {
    this.sprite = sprite;
    this.color = color;

    this.sprite.setPosition(position.x * Main.SPRITE_WIDTH, position.y * Main.SPRITE_HEIGHT);
    this.sprite.setColor(color);
    this.sprite.setAlpha(alpha);
  }
}
