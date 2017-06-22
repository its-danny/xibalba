package me.dannytatom.xibalba.components.items;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.yaml.ItemData;

public class WeaponComponent implements Component {
  public final String type;
  public final String ammunitionType;
  public Material material;

  public WeaponComponent(ItemData data) {
    this.type = data.weaponType;
    this.ammunitionType = data.ammunition;
  }

  public enum Material {
    OBSIDIAN, CHERT
  }
}
