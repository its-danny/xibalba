package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent implements Component {
  public final int speed;
  public final int vision;
  public final int maxHealth;
  public final int toughness;
  public final int damage;
  public final String name;
  public int health;
  public int energy;

  /**
   * Holds entity attributes.
   *
   * @param name      Name
   * @param speed     How much energy is gotten back each turn
   * @param vision    How many cells they see around them
   * @param maxHealth Max health
   * @param toughness How much damage they can soak up
   * @param damage    How much damage they do
   */
  public AttributesComponent(String name, int speed, int vision, int maxHealth, int toughness, int damage) {
    this.energy = speed;
    this.name = name;
    this.speed = speed;
    this.vision = vision;
    this.maxHealth = maxHealth;
    this.health = maxHealth;
    this.toughness = toughness;
    this.damage = damage;
  }
}
