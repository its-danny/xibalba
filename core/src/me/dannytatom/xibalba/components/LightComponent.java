package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class LightComponent implements Component {
  public final float radius;
  public final boolean flickers;
  public final ArrayList<Color> colors;

  /**
   * A light source, gives off light color and increases FoV.
   */
  public LightComponent(float radius, boolean flickers, ArrayList<Color> colors) {
    this.radius = radius;
    this.flickers = flickers;
    this.colors = colors;
  }
}
