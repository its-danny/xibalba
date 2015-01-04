package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class RangeComponent extends Component {
  public static final int COST = 100;

  public final Vector2 target;
  public final Entity item;

  public RangeComponent(Vector2 target, Entity item) {
    this.target = target;
    this.item = item;
  }
}
