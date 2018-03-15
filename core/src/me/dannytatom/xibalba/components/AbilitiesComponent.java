package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

import me.dannytatom.xibalba.abilities.Ability;

public class AbilitiesComponent implements Component {
  public final HashMap<String, Ability> abilities = new HashMap<>();
}
