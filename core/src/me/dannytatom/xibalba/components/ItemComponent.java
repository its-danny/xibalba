package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.utils.YamlToItem;

import java.util.HashMap;

public class ItemComponent implements Component {
  public final String type;
  public final String description;
  public final boolean twoHanded;
  public final String skill;
  public final HashMap<String, Integer> attributes;
  public final Array<String> verbs;
  public String location;
  public Array<String> actions;
  public String name;
  public boolean throwing = false;

  /**
   * Initialize item component from yaml data.
   *
   * @param data YamlToItem instance containing data from relevant yaml file
   */
  public ItemComponent(YamlToItem data) {
    this.type = data.type;
    this.name = data.name;
    this.description = data.description;
    this.location = data.location;
    this.twoHanded = data.twoHanded;
    this.skill = data.skill;

    this.attributes = data.attributes;
    this.actions = data.actions == null ? null : new Array<>(data.actions.toArray(new String[0]));
    this.verbs = data.verbs == null ? null : new Array<>(data.verbs.toArray(new String[0]));
  }
}
