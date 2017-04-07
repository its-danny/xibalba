package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class DefectsComponent implements Component {
  public Array<String> defects;

  public DefectsComponent() {
    defects = new Array<>();
  }
}
