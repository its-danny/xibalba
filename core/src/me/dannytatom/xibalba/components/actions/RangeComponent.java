package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class RangeComponent extends Component {
  public static final int COST = 100;

  public final Entity target;

  public RangeComponent(Entity target) {
    this.target = target;
  }
}
