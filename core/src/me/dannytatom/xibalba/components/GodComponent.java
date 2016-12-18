package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class GodComponent implements Component {
  public final String name;
  public final String description;

  public GodComponent(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
