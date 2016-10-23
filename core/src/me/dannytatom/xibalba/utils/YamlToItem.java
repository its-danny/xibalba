package me.dannytatom.xibalba.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class YamlToItem {
  public String type;
  public String weaponType;
  public String ammunitionType;
  public String armorType;
  public String name;
  public String description;
  public float weight;
  public HashMap<String, String> visual;

  public boolean twoHanded;
  public String skill;
  public String location;
  public String ammunition;

  public HashMap<String, Integer> attributes;
  public ArrayList<String> actions;
  public HashMap<String, String> effects;
  public ArrayList<String> verbs;
}
