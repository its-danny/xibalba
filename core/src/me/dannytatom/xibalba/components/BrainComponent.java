package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import me.dannytatom.xibalba.brain.Brain;

public class BrainComponent implements Component {
  public final StateMachine<Entity, Brain> stateMachine;

  public Array<Dna> dna;

  public float aggression;
  public float fear;
  public float fearThreshold;

  public Array<Vector2> path;
  public Entity target;

  /**
   * The brain.
   *
   * @param entity What entity has this brain
   */
  public BrainComponent(Entity entity) {
    stateMachine = new DefaultStateMachine<>(entity, Brain.SLEEP);

    dna = new Array<>();

    fear = 0f;
  }

  public enum Dna {
    TERRESTRIAL,
    AQUATIC
  }
}
