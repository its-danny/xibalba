package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.utils.yaml.ItemData;
import me.dannytatom.xibalba.utils.yaml.ItemRequiredComponentData;
import me.dannytatom.xibalba.world.WorldManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemComponent implements Component {
  public final String key;
  public final String type;
  public final String description;
  public final float weight;
  public final boolean twoHanded;
  public final String skill;
  public final HashMap<String, Integer> attributes;
  public final Array<String> verbs;
  public final Array<String> actions;
  public String location;
  public String name;
  public Quality quality;
  public StoneMaterial stoneMaterial;
  public boolean craftable;
  public ArrayList<Integer> craftedRange;
  public ArrayList<RequiredComponent> requiredComponents;
  public boolean throwing = false;

  /**
   * Initialize item component from yaml data.
   *
   * @param data ItemData instance containing data from relevant yaml file
   */
  public ItemComponent(String key, String name, String description, ItemData data) {
    this.key = key;
    this.name = name;
    this.description = description;
    this.type = data.type;
    this.weight = data.weight;
    this.location = data.location;
    this.twoHanded = data.twoHanded;
    this.skill = data.skill;

    this.attributes = data.attributes;
    this.actions = data.actions == null ? null : new Array<>(data.actions.toArray(new String[0]));
    this.verbs = data.verbs == null ? null : new Array<>(data.verbs.toArray(new String[0]));

    this.craftable = data.craftable;
    this.craftedRange = data.craftedRange;

    Quality[] qualities = Quality.values();
    this.quality = qualities[MathUtils.random(0, qualities.length - 1)];

    if (data.hasStoneMaterial) {
      StoneMaterial[] stoneMaterials = StoneMaterial.values();
      this.stoneMaterial = stoneMaterials[MathUtils.random(0, stoneMaterials.length - 1)];
    }

    this.requiredComponents = new ArrayList<>();
    if (data.requiredComponents != null) {
      for (ItemRequiredComponentData component : data.requiredComponents) {
        Entity entity = WorldManager.entityFactory.createItem(component.key, new Vector2(0, 0));
        this.requiredComponents.add(new RequiredComponent(entity, component.amount));
      }
    }
  }

  public enum Quality {
    BROKEN(-6), POOR(-2), USABLE(0), GOOD(2), GREAT(6);

    private int modifier;

    Quality(int modifier) {
      this.modifier = modifier;
    }

    public int getModifier() {
      return modifier;
    }
  }

  public enum StoneMaterial {
    CHERT(0), OBSIDIAN(2), JADE(4);

    private int modifier;

    StoneMaterial(int modifier) {
      this.modifier = modifier;
    }

    public int getModifier() {
      return modifier;
    }
  }

  public class RequiredComponent {
    public Entity item;
    public int amount;

    RequiredComponent(Entity item, int amount) {
      this.item = item;
      this.amount = amount;
    }
  }
}
