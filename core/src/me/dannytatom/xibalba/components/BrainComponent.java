package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.brain.EntityState;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class BrainComponent implements Component {
  public Array<Personality> personalities;
  public StateMachine<Entity, EntityState> stateMachine;
  public List<GridCell> path;
  public Entity target;

  public BrainComponent(Entity entity, Array<Personality> personalities) {
    this.personalities = personalities;

    stateMachine = new DefaultStateMachine<>(entity, EntityState.IDLE);
  }

  public enum Personality {
    AGGRESSIVE, // Will target and attack on sight
    PASSIVE,    // Doesn't really care about anything
    PACKS,      // Travels in packs
    SAFE,       // Will flee if health gets below 50%
    SOLO,       // Travels alone
    STEALTHY,   // Stays in shadows or out of sight when not in combat
    FLYING,     // They fly over water and traps and such
    AQUATIC,    // They stay in water
  }
}
