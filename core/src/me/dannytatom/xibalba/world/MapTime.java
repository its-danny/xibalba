package me.dannytatom.xibalba.world;

import java.util.Objects;

public class MapTime {
  public Time time;
  public float shadow = 0;
  private int counter = 0;

  /**
   * Handles day/night cycle in the forest.
   */
  MapTime() {
    time = Time.DAWN;
    shadow = .15f;
  }

  /**
   * Update time depending on how many turns have been made.
   * Every 25 turns we move to the next time cycle: Dawn > Day > Dusk > Night
   */
  public void update() {
    if (Objects.equals(WorldManager.world.getCurrentMap().type, "forest")) {
      if (counter >= 0 && counter < 25) {
        time = Time.DAWN;
        shadow = .15f;
        counter += 1;
      } else if (counter >= 25 && counter < 50) {
        time = Time.DAY;
        shadow = .20f;
        counter += 1;
      } else if (counter >= 50 && counter < 75) {
        time = Time.DUSK;
        shadow = .15f;
        counter += 1;
      } else if (counter >= 75 && counter < 100) {
        time = Time.NIGHT;
        shadow = .10f;
        counter += 1;
      } else {
        counter = 0;
      }
    } else {
      counter = 0;
      time = Time.DAWN;
      shadow = .15f;
    }
  }

  public enum Time {
    DAWN, DAY, DUSK, NIGHT
  }
}
