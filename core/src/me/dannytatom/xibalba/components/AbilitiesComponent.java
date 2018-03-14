package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

import me.dannytatom.xibalba.utils.yaml.AbilityData;

public class AbilitiesComponent implements Component {
  public final HashMap<String, AbilityData> abilities = new HashMap<>();
}
