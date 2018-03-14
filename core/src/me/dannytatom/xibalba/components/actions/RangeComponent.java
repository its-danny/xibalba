package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class RangeComponent implements Component {
  public static final int COST = 100;

  public final Vector2 position;
  public final Entity item;
  public final String skill;
  public final String bodyPart;
  public final boolean isFocused;

  /**
   * Component for doing a ranged attack.
   *
   * @param position Target position
   * @param item     Item we're using
   * @param skill    What skill is associated with that item
   * @param bodyPart Where we're aiming
   */
  public RangeComponent(Vector2 position, Entity item,
                        String skill, String bodyPart, boolean isFocused) {
    this.position = position;
    this.item = item;
    this.skill = skill;
    this.bodyPart = bodyPart;
    this.isFocused = isFocused;
  }
}
