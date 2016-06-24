package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class AttributesComponent implements Component {
  public final int speed;
  public final int vision;
  public final int maxHealth;
  public final int defense;
  public final int damage;
  public final String name;
  public final String description;
  public int health;
  public int energy;

  /**
   * Holds entity attributes.
   *
   * @param name        Name
   * @param description Entity description
   * @param speed       How much energy is gotten back each turn
   * @param vision      How many cells they see around them
   * @param maxHealth   Max health
   * @param defense     How much damage they can soak up
   * @param damage      How much damage they do
   */
  public AttributesComponent(String name, String description, int speed, int vision,
                             int maxHealth, int defense, int damage) {
    this.energy = speed;
    this.name = name;
    this.description = description;
    this.speed = speed;
    this.vision = vision;
    this.maxHealth = maxHealth;
    this.health = maxHealth;
    this.defense = defense;
    this.damage = damage;
  }
}
