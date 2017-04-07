package me.dannytatom.xibalba;

import com.strongjoshua.console.CommandExecutor;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class ConsoleCommandExecutor extends CommandExecutor {
  public void log(String message) {
    console.log(message);
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

  public void fieldOfView(Boolean show) {
    if (show) {
      Main.debug.fieldOfViewEnabled = true;
      console.log("[GREEN]Field of View: ON");
    } else {
      Main.debug.fieldOfViewEnabled = false;
      console.log("[RED]Field of View: OFF");
    }

    WorldManager.entityHelpers.updateSenses(WorldManager.player);
  }
}
