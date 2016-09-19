package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent implements Component {
  public final String description;
  public final int speed;
  public final int toughness;
  public final int maxVision;
  public final int maxHealth;
  public final int maxOxygen;
  public String name;
  public int vision;
  public int strength;
  public int health;
  public int oxygen;
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
    this.toughness = toughness;
    this.strength = strength;

    this.maxVision = vision;
    this.maxHealth = toughness * 10;
    this.maxOxygen = toughness * 4;
    this.vision = vision;
    this.health = maxHealth;
    this.oxygen = maxOxygen;
  }
}
