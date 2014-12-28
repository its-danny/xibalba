package me.dannytatom.xibalba.components.ai;

import com.badlogic.ashley.core.Component;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class WanderComponent extends Component {
  public List<GridCell> path;

  public WanderComponent() {
    this.path = null;
  }
}
