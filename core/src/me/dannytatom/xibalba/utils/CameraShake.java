package me.dannytatom.xibalba.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

import me.dannytatom.xibalba.Main;

public class CameraShake {
  public float time;
  private Random random;
  private float currentTime;
  private float power;
  private float currentPower;

  /**
   * https://carelesslabs.wordpress.com/2014/05/08/simple-screen-shake/comment-page-1/
   */
  public CameraShake() {
    time = 0;
    currentTime = 0;
    power = 0;
    currentPower = 0;
  }

  /**
   * Start shakin', boys.
   *
   * @param power Force of the shake
   * @param time  How long it should last
   */
  public void shake(float power, float time) {
    random = new Random();

    this.power = power;
    this.time = time;
    this.currentTime = 0;
  }

  /**
   * Called each frame to handle the shaking.
   *
   * @param delta    Time since last frame
   * @param camera   Camera to shake
   * @param position Position to snap back to afterwards
   */
  public void update(float delta, Camera camera, Vector2 position) {
    if (currentTime <= time) {
      currentPower = power * ((time - currentTime) / time);
      float posX = (random.nextFloat() - 0.5f) * 2 * currentPower;
      float posY = (random.nextFloat() - 0.5f) * 2 * currentPower;

      camera.translate(-posX, -posY, 0);

      currentTime += delta;
    } else {
      time = 0;
      currentTime = 0;

      camera.position.set(
          position.x * Main.SPRITE_WIDTH,
          position.y * Main.SPRITE_HEIGHT, 0
      );
    }
  }
}