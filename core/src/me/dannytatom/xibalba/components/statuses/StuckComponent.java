package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class StuckComponent implements Component {
  public final int life = 10;
  public int counter = 0;

  public StuckComponent() {

  }
}
