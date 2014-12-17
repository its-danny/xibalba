package me.dannytatom.x2600BC.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent extends Component {
  public int energy;
  public int speed;
  public int health;
  public int damage;

  /**
   * Holds entity attributes.
   *
   * @param speed  The entity's speed
   * @param health The entity's health
   * @param damage The entity's damage
   */
  public AttributesComponent(int speed, int health, int damage) {
    this.energy = speed;
    this.speed = speed;
    this.health = health;
    this.damage = damage;
  }
}
