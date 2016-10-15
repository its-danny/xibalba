package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent implements Component {
  public final String description;
  public int speed;
  public int maxOxygen;
  public final String name;
  public int maxVision;
  public int maxHealth;
  public int toughness;
  public int vision;
  public int hearing;
  public int strength;
  public int agility;
  public int health;
  public int oxygen;
  public int energy;
  public float[][] visionMap;
  public float[][] hearingMap;

  /**
   * Holds entity attributes.
   *
   * @param name        Name
   * @param description Entity description
   * @param speed       How much energy is gotten back each turn
   * @param maxVision   How many cells they see around them
   * @param toughness   How much strength they can soak up
   * @param strength    How much strength they do
   */
  public AttributesComponent(String name, String description,
                             int speed, int maxVision, int hearing,
                             int toughness, int strength, int agility) {
    this.name = name;
    this.description = description;
    this.energy = speed;
    this.speed = speed;
    this.toughness = toughness;
    this.strength = strength;
    this.agility = agility;

    this.maxVision = maxVision;
    this.maxHealth = toughness * 10;
    this.maxOxygen = toughness * 4;
    this.vision = maxVision;
    this.hearing = hearing;
    this.health = maxHealth;
    this.oxygen = maxOxygen;
  }
}
