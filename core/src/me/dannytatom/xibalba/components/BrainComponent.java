package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import me.dannytatom.xibalba.brain.DefaultBrain;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class BrainComponent implements Component {
  public final StateMachine<Entity, DefaultBrain> stateMachine;
  public List<GridCell> path;
  public Entity target;

  public BrainComponent(Entity entity) {
    stateMachine = new DefaultStateMachine<>(entity, DefaultBrain.IDLE);
  }
}
