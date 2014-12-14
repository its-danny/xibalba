package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class AttributesComponent extends Component {
  public int speed;
  public int health;
  public int damage;
  public ArrayList<String> actions;

  /**
   * Holds entity attributes.
   *
   * @param speed  The entity's speed
   * @param health The entity's health
   * @param damage The entity's damage
   */
  public AttributesComponent(int speed, int health, int damage) {
    this.speed = speed;
    this.health = health;
    this.damage = damage;

    actions = new ArrayList<>();
  }
}
