package me.dannytatom.xibalba.utils.yaml;

public class AbilityData {
  public String name;
  public Type type;
  public boolean targetRequired = false;
  public String description;
  public int recharge;
  public int counter;
  public String effect;

  public enum Type {
    PASSIVE, ACTIVE
  }
}
