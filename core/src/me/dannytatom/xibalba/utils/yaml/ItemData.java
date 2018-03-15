package me.dannytatom.xibalba.utils.yaml;

import java.util.ArrayList;
import java.util.HashMap;

import me.dannytatom.xibalba.effects.Effect;

public class ItemData {
  public String type;
  public String weaponType;
  public String ammunitionType;
  public boolean hasStoneMaterial;
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
  public ArrayList<Effect> effects;
  public ArrayList<String> verbs;
  public HashMap<String, Integer> light;

  public ArrayList<Integer> craftedRange;
  public ArrayList<ItemRequiredComponentData> requiredComponents;
}
