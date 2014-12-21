package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class MovementComponent extends Component {
  public List<GridCell> path;
  public Vector2 position;

  public MovementComponent() {
    this.path = null;
    this.position = null;
  }
}
