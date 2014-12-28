package me.dannytatom.xibalba.components.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class TargetComponent extends Component {
  public final Vector2 pos;
  public List<GridCell> path;

  public TargetComponent(Vector2 target) {
    this.pos = target;
    this.path = null;
  }
}
