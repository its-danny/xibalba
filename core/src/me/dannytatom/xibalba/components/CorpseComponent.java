package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.TreeMap;

public class CorpseComponent implements Component {
  public final String entity;
  public final AttributesComponent.Type type;
  public final TreeMap<String, Integer> parts;
  public final TreeMap<String, String> wearable;

  /**
   * A corpse.
   *
   * @param entity   Name of the entity this corpse belongs to
   * @param parts    What parts it has that can be dismembered
   * @param wearable What parts of it are wearable
   */
  public CorpseComponent(String entity, AttributesComponent.Type type,
                         TreeMap<String, Integer> parts, TreeMap<String, String> wearable) {
    this.entity = entity;
    this.type = type;
    this.parts = parts;
    this.wearable = wearable;
  }
}
