package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent implements Component {
  public final String name;
  public final String description;
  public final int speed;
  public final int vision;
  public final int toughness;
  public final int maxHealth;
  public int strength;
  public int health;
  public int energy;

  /**
   * Holds entity attributes.
   *
   * @param name        Name
   * @param description Entity description
   * @param speed       How much energy is gotten back each turn
   * @param vision      How many cells they see around them
   * @param toughness   How much strength they can soak up
   * @param strength    How much strength they do
   */
  public AttributesComponent(String name, String description,
                             int speed, int vision, int toughness, int strength) {
    this.name = name;
    this.description = description;
    this.energy = speed;
    this.speed = speed;
    this.vision = vision;
    this.toughness = toughness;
    this.strength = strength;

    this.maxHealth = toughness * 100;
    this.health = maxHealth;
  }
}
