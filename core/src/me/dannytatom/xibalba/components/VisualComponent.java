package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;

public class VisualComponent implements Component {
  public Sprite sprite = null;

  public VisualComponent(Sprite sprite, Vector2 position) {
    this(sprite, position, Color.WHITE, 1f);
  }

  public VisualComponent(Sprite sprite, Vector2 position, Color color) {
    this(sprite, position, color, 1f);
  }

  /**
   * VisualComponent...
   *
   * @param sprite   All
   * @param position this
   * @param color    obvious
   * @param alpha    srsly
   */
  public VisualComponent(Sprite sprite, Vector2 position, Color color, float alpha) {
    this.sprite = sprite;
    this.sprite.setPosition(position.x * Main.SPRITE_WIDTH, position.y * Main.SPRITE_HEIGHT);
    this.sprite.setColor(color);
    this.sprite.setAlpha(alpha);
  }
}
