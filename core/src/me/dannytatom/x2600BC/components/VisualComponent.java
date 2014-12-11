package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class VisualComponent extends Component {
  public Sprite sprite;

  /**
   * Holds entity's sprite.
   *
   * @param texture Texture to draw
   */
  public VisualComponent(Texture texture) {
    sprite = new Sprite(texture);
  }
}
