package me.dannytatom.xibalba.utils.yaml;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemData {
  public String type;
  public String weaponType;
  public String ammunitionType;
  public String armorType;
  public float lightRadius;
  public float weight;
  public HashMap<String, String> visual;

  public boolean twoHanded;
  public String skill;
  public String location;
  public String ammunition;

  public boolean lightFlickers;
  public ArrayList<String> lightColors;

  public HashMap<String, Integer> attributes;
  public ArrayList<String> actions;
  public HashMap<String, String> effects;
  public ArrayList<String> verbs;
  public HashMap<String, Integer> light;

  public boolean craftable;
  public ArrayList<Integer> craftedRange;
  public ArrayList<String> requiredComponents;
}
