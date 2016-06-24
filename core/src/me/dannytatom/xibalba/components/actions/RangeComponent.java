package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class RangeComponent implements Component {
  public static final int COST = 100;

  public final Vector2 target;
  public final Entity item;
  public final String skill;

  /**
   * Filling RangeComponent w/ data needed for RangeSystem.
   *
   * @param target Who's being attacked
   * @param item   The item we're attacking with
   * @param skill  The skill we're attacking with
   */
  public RangeComponent(Vector2 target, Entity item, String skill) {
    this.target = target;
    this.item = item;
    this.skill = skill;
  }
}
