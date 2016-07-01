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

  public RangeComponent(Vector2 position, Entity item, String skill, String bodyPart) {
    this.position = position;
    this.item = item;
    this.skill = skill;
    this.bodyPart = bodyPart;
  }
}
