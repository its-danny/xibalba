package me.dannytatom.xibalba.utils.yaml;

import java.util.HashMap;
import java.util.TreeMap;

import me.dannytatom.xibalba.components.AttributesComponent;

public class EnemyData {
  public AttributesComponent.Type type;
  public HashMap<String, String> visual;
  public HashMap<String, Integer> attributes;
  public BrainData brain;
  public TreeMap<String, Integer> bodyParts;
  public TreeMap<String, String> wearableBodyParts;
  public HashMap<String, String> effects;
}
