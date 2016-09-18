package me.dannytatom.xibalba.components.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class BrainComponent implements Component {
  public final Array<Personality> personalities;
  public State state;
  public List<GridCell> path = null;

  public BrainComponent(Array<Personality> personalities) {
    this.state = State.WAITING;
    this.personalities = personalities;
  }

  public enum State {
    WAITING, WANDERING, TARGETING, ATTACKING
  }

  public enum Personality {
    AGGRESSIVE, // Will target and attack on sight
    PACKS,      // Travels in packs
    SAFE,       // Will flee if health gets below 50%
    SOLO,       // Travels alone
    STEALTHY    // Stays in shadows or out of sight when not in combat
  }
}
