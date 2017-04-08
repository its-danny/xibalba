package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class TraitsComponent implements Component {
  public final Array<String> traits;

  public TraitsComponent() {
    traits = new Array<>();
  }
}
