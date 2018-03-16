package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent implements Component {
  public final String description;
  public final String name;
  public final Type type;
  public float divineFavor;
  public int speed;
  public int maxOxygen;
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
   * @param type        Entity type
   * @param speed       How much energy is gotten back each turn
   * @param maxVision   How many cells they see around them
   * @param toughness   How much strength they can soak up
   * @param strength    How much strength they do
   */
  public AttributesComponent(String name, String description, Type type,
                             int speed, int maxVision, int hearing,
                             int toughness, int strength, int agility) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.energy = speed;
    this.speed = speed;
    this.toughness = toughness;
    this.strength = strength;
    this.agility = agility;

    this.maxHealth = toughness * 10;
    this.maxOxygen = toughness * 4;
    this.vision = maxVision;
    this.hearing = hearing;
    this.health = maxHealth;
    this.oxygen = maxOxygen;
    this.divineFavor = 0.1f;
  }

  public enum Type {
    HUMAN, ANIMAL
  }
}
