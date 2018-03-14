package me.dannytatom.xibalba.utils.yaml;

import me.dannytatom.xibalba.components.AttributesComponent;

public class AbilityData {
  public boolean targetRequired;
  public String name;
  public Type type;
  public AttributesComponent.Type targetType;
  public String description;
  public int recharge;
  public int counter;
  public String effect;

  public enum Type {
    PASSIVE, ACTIVE
  }
}
