package me.dannytatom.xibalba.components.ai;

import com.badlogic.ashley.core.Component;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class BrainComponent extends Component {
  public State state;
  public List<GridCell> path = null;

  public enum State {
    WAITING, WANDERING, TARGETING, ATTACKING
  }

  public BrainComponent() {
    this.state = State.WAITING;
  }
}
