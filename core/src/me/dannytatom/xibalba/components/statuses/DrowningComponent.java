package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.statuses.Drowning;

public class DrowningComponent implements Component {
  public final Drowning instance;

  public DrowningComponent() {
    this.instance = new Drowning();
  }
}
