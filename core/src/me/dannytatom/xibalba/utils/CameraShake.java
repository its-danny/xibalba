package me.dannytatom.xibalba.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;

import java.util.Random;

public class CameraShake {
  public float time;
  private Random random;
  private float currentTime;
  private float power;
  private float currentPower;

  public CameraShake() {
    time = 0;
    currentTime = 0;
    power = 0;
    currentPower = 0;
  }

  public void shake(float power, float time) {
    random = new Random();

    this.power = power;
    this.time = time;
    this.currentTime = 0;
  }

  public void update(float delta, Camera camera, Vector2 position) {
    if (currentTime <= time) {
      currentPower = power * ((time - currentTime) / time);
      float x = (random.nextFloat() - 0.5f) * 2 * currentPower;
      float y = (random.nextFloat() - 0.5f) * 2 * currentPower;

      camera.translate(-x, -y, 0);

      currentTime += delta;
    } else {
      camera.position.set(
          position.x * Main.SPRITE_WIDTH,
          position.y * Main.SPRITE_HEIGHT, 0
      );
    }
  }
}