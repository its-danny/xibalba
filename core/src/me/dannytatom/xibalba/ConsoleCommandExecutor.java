package me.dannytatom.xibalba;

import com.strongjoshua.console.CommandExecutor;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class ConsoleCommandExecutor extends CommandExecutor {
  public void debugUI(Boolean on) {
    if (on) {
      Main.debug.debugUIEnabled = true;
      console.log("[GREEN]Debug UI: ON");
    } else {
      Main.debug.debugUIEnabled = false;
      console.log("[RED]Debug UI: OFF");
    }
  }

  public void setHealth(int amount) {
    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);
    attributes.health = amount;

    console.log("[GREEN] Health now at " + attributes.health);
  }

  public void setHealthMax() {
    AttributesComponent attributes = ComponentMappers.attributes.get(WorldManager.player);
    attributes.health = attributes.maxHealth;

    console.log("[GREEN] Health now at " + attributes.health);
  }

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

  public void weather(Boolean on) {
    if (on) {
      Main.debug.weatherEnabled = true;
      console.log("[GREEN]Weather: ON");
    } else {
      Main.debug.weatherEnabled = false;
      console.log("[RED]Weather: OFF");
    }
  }
}
