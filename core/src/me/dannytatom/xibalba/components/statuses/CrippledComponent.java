package me.dannytatom.xibalba.components.statuses;

import com.badlogic.ashley.core.Component;

public class CrippledComponent implements Component {
  public final int life = 4;
  public int counter = 0;
  public int turnCounter = 0;

  public CrippledComponent() {

  }
}
