package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.utils.YamlToItem;

import java.util.ArrayList;

public class LightComponent implements Component {
  public final float radius;
  public final boolean flickers;
  public final ArrayList<Color> colors;

  /**
   * A light source, gives off light color and increases FoV.
   *
   * @param data Light data
   */
  public LightComponent(YamlToItem data) {
    this.radius = data.lightRadius;
    this.flickers = data.lightFlickers;

    colors = new ArrayList<>();

    for (String color : data.lightColors) {
      colors.add(Main.parseColor(color));
    }
  }
}
