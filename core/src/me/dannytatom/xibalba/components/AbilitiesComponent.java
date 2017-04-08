package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.utils.YamlToAbility;

public class AbilitiesComponent implements Component {
  public final Array<YamlToAbility> abilities = new Array<>();
}
