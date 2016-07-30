package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.utils.YamlToItem;

import java.util.HashMap;

public class ItemComponent implements Component {
  public final String type;
  public final String name;
  public final String description;

  public final boolean twoHanded;
  public final String skill;
  public final String location;
  public final String ammunition;

  public final HashMap<String, Integer> attributes;
  public final Array<String> actions;
  public HashMap<String, String> effects;
  public final Array<String> verbs;

  public boolean throwing = false;

  /**
   * Initialize item component from yaml data.
   *
   * @param yaml YamlToItem instance containing
   *             data from relevant yaml file
   */
  public ItemComponent(YamlToItem yaml) {
    this.type = yaml.type;
    this.name = yaml.name;
    this.description = yaml.description;

    this.twoHanded = yaml.twoHanded;
    this.skill = yaml.skill;
    this.location = yaml.location;
    this.ammunition = yaml.ammunition;

    this.attributes = yaml.attributes;
    this.actions = yaml.actions == null ? null : new Array<>(yaml.actions.toArray(new String[0]));
    this.effects = yaml.effects;
    this.verbs = yaml.verbs == null ? null : new Array<>(yaml.verbs.toArray(new String[0]));
  }
}
