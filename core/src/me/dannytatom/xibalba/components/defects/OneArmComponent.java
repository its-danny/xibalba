package me.dannytatom.xibalba.components.defects;

import com.badlogic.ashley.core.Component;

public class OneArmComponent implements Component {
  public static final int reward = 2;
  public static final String name = "One arm";
  public static final String description
      = "Your left arm is missing. No dual wielding or 2 handed weapons. Max HP lowered by 20%.";

  public OneArmComponent() {

  }
}
