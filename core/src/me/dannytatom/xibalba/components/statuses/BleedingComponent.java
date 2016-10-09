package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class BleedingComponent implements Component {
  public int life = 5;
  public int counter = 0;

  public BleedingComponent() {

  }
}
