package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class MeleeComponent implements Component {
  public static final int COST = 100;

  public final Entity target;
  public final String bodyPart;

  public MeleeComponent(Entity target, String bodyPart) {
    this.target = target;
    this.bodyPart = bodyPart;
  }
}
