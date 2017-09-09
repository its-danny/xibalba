package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import me.dannytatom.xibalba.utils.yaml.AbilityData;

import java.util.HashMap;

public class AbilitiesComponent implements Component {
  public final HashMap<String, AbilityData> abilities = new HashMap<>();
}
