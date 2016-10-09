package me.dannytatom.xibalba.utils;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class YamlToEnemy {
  public String name;
  public String description;
  public HashMap<String, String> visual;
  public HashMap<String, Integer> attributes;
  public TreeMap<String, Integer> bodyParts;
  public HashMap<String, List<String>> brain;
  public HashMap<String, String> effects;
}
