package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class LimbComponent implements Component {
  public final AttributesComponent.Type type;
  public boolean deboned = false;

  public LimbComponent(AttributesComponent.Type type) {
    this.type = type;
  }
}
