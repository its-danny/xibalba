package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.TreeMap;

import me.dannytatom.xibalba.effects.Effect;

public class CorpseComponent implements Component {
  public final String entity;
  public final AttributesComponent.Type type;
  public final TreeMap<String, Integer> bodyParts;
  public final TreeMap<String, ArrayList<Effect>> wearableBodyParts;

  /**
   * A corpse.
   *
   * @param entity            Name of the entity this corpse belongs to
   * @param bodyParts         What bodyParts it has that can be dismembered
   * @param wearableBodyParts What bodyParts of it are wearableBodyParts
   */
  public CorpseComponent(String entity, AttributesComponent.Type type,
                         TreeMap<String, Integer> bodyParts,
                         TreeMap<String, ArrayList<Effect>> wearableBodyParts) {
    this.entity = entity;
    this.type = type;
    this.bodyParts = bodyParts;
    this.wearableBodyParts = wearableBodyParts;
  }
}
