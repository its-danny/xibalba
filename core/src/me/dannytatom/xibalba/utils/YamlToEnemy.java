package me.dannytatom.xibalba.utils;

import java.util.HashMap;
import java.util.TreeMap;

public class YamlToEnemy {
  public String name;
  public String description;
  public HashMap<String, String> visual;
  public HashMap<String, Integer> attributes;
  public TreeMap<String, Integer> bodyParts;
  public TreeMap<String, String> wearableBodyParts;
  public HashMap<String, String> effects;
}
