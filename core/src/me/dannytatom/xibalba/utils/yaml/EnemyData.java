package me.dannytatom.xibalba.utils.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.effects.Effect;

public class EnemyData {
  public AttributesComponent.Type type;
  public HashMap<String, String> visual;
  public HashMap<String, Integer> attributes;
  public BrainData brain;
  public TreeMap<String, Integer> bodyParts;
  public TreeMap<String, ArrayList<Effect>> wearableBodyParts;
  public ArrayList<Effect> effects;
}
