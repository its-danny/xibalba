package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.statuses.Crippled;

public class CrippledComponent implements Component {
  public final Crippled instance;

  public CrippledComponent() {
    this.instance = new Crippled();
  }
}
