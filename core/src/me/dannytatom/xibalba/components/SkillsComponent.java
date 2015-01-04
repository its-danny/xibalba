package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class SkillsComponent extends Component {
  public final int unarmed = 4;
  public final int throwing = 0;
  public final int slashing = 0;
  public final int stabbing = 0;
  public int unarmedCounter = 0;
  public int throwingCounter = 0;
  public int slashingCounter = 0;
  public int stabbingCounter = 0;

  public SkillsComponent() {

  }
}
