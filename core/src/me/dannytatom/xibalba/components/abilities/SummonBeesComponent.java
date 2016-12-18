package me.dannytatom.xibalba.components.abilities;

import com.badlogic.ashley.core.Component;

public class SummonBeesComponent implements Component {
  public static final int rechargeRate = 3;
  public static final String name = "Summon Bees";
  public static final String description
      = "Summon a swarm of bees to fight by your side.";

  public SummonBeesComponent() {

  }
}
