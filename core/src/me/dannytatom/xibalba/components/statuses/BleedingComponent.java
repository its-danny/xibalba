package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.statuses.Bleeding;

public class BleedingComponent implements Component {
  public Bleeding instance;

  public BleedingComponent() {
    this.instance = new Bleeding();
  }
}
