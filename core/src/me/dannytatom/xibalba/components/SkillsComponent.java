package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

@SuppressWarnings("CanBeFinal")
public class SkillsComponent implements Component {
  public int unarmed = 0;
  public int throwing = 0;
  public int slashing = 0;
  public int piercing = 0;
  public int bashing = 0;
  public int archery = 0;
  public int unarmedCounter = 0;
  public int throwingCounter = 0;
  public int slashingCounter = 0;
  public int piercingCounter = 0;
  public int bashingCounter = 0;
  public int archeryCounter = 0;

  public SkillsComponent() {

  }
}
