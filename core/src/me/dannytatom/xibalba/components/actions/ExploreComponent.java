package me.dannytatom.xibalba.components.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ExploreComponent implements Component {
  public Array<Vector2> path;

  public ExploreComponent() {
    path = new Array<>();
  }
}
