package me.dannytatom.xibalba;

import com.strongjoshua.console.CommandExecutor;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class ConsoleCommandExecutor extends CommandExecutor {
  /**
   * Toggle debug UI.
   *
   * @param on Toggle
   */
  public void debug(Boolean on) {
    if (on) {
      Main.debug.debugEnabled = true;
      console.log("[GREEN]Debug UI: ON");
    } else {
      Main.debug.debugEnabled = false;
      console.log("[RED]Debug UI: OFF");
    }
  }

  /**
   * Set player health.
   *
   * @param amount Target number
   */
  public void setHealth(int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);
    attributes.health = amount;

    console.log("[GREEN] Health now at " + attributes.health);
  }

  /**
   * Set player health to their max health.
   */
  public void setHealthMax() {
    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);
    attributes.health = attributes.maxHealth;

    console.log("[GREEN] Health now at " + attributes.health);
  }

  /**
   * Toggle field of view.
   *
   * @param on Toggle
   */
  public void fieldOfView(Boolean on) {
    if (on) {
      Main.debug.fieldOfViewEnabled = true;
      console.log("[GREEN]Field of View: ON");
    } else {
      Main.debug.fieldOfViewEnabled = false;
      console.log("[RED]Field of View: OFF");
    }

    WorldManager.entityHelpers.updateSenses(WorldManager.player);
  }

  /**
   * Toggle weather.
   *
   * @param on Toggle
   */
  public void weather(Boolean on) {
    if (on) {
      Main.debug.weatherEnabled = true;
      console.log("[GREEN]Weather: ON");
    } else {
      Main.debug.weatherEnabled = false;
      console.log("[RED]Weather: OFF");
    }
  }

  /**
   * Teleport to entrance.
   */
  public void goToEntrance() {
    ComponentMappers.position.get(WorldManager.player).pos.set(
        WorldManager.world.getCurrentMap().entrance
    );

    WorldManager.entityHelpers.updateSenses(WorldManager.player);
  }

  /**
   * Teleport to exit.
   */
  public void goToExit() {
    ComponentMappers.position.get(WorldManager.player).pos.set(
        WorldManager.world.getCurrentMap().exit
    );

    WorldManager.entityHelpers.updateSenses(WorldManager.player);
  }
}
