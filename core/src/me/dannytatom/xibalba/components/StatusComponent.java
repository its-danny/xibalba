package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class StatusComponent implements Component {
  public boolean crippled = false;
  public int crippledLifeCounter = 0;
  public int crippledTurnCounter = 0;

  public StatusComponent() {

  }
}
