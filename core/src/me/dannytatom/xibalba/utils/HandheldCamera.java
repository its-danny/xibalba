package me.dannytatom.xibalba.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import me.dannytatom.xibalba.Main;

public class HandheldCamera {
  private final float lerp;

  public HandheldCamera() {
    lerp = 2f;
  }

  /**
   * Called each frame to handle the shaking.
   *
   * @param delta    Time since last frame
   * @param camera   Camera to move
   * @param position Position to move towards
   */
  public void update(float delta, Camera camera, Vector2 position) {
    camera.position.x += (position.x * Main.SPRITE_WIDTH - camera.position.x) * lerp * delta;
    camera.position.y += (position.y * Main.SPRITE_HEIGHT - camera.position.y) * lerp * delta;
  }
}
