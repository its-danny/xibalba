package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.TreeMap;

public class CorpseComponent implements Component {
  public final String entityName;
  public TreeMap<String, Integer> parts;

  public CorpseComponent(String entityName, TreeMap<String, Integer> parts) {
    this.entityName = entityName;
    this.parts = parts;
  }
}
