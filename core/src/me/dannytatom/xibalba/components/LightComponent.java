package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.utils.YamlToItem;

import java.util.ArrayList;

public class LightComponent implements Component {
  public float radius;
  public boolean flickers;
  public ArrayList<Color> colors;

  public LightComponent(YamlToItem data) {
    this.radius = data.lightRadius;
    this.flickers = data.lightFlickers;

    colors = new ArrayList<>();

    for (String color : data.lightColors) {
      colors.add(Main.parseColor(color));
    }
  }
}
