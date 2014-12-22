package me.dannytatom.xibalba.components.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class AttackComponent extends Component {
  public int x;
  public int y;

  public AttackComponent(Vector2 target) {
    this.x = (int) target.x;
    this.y = (int) target.y;
  }
}
