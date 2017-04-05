package me.dannytatom.xibalba.components.traits;

import com.badlogic.ashley.core.Component;

public class CarnivoreComponent implements Component {
  public static final int cost = 2;
  public static final String name = "Carnivore";
  public static final String description
      = "Eating raw corpses gives a chance to heal";

  public CarnivoreComponent() {

  }
}
