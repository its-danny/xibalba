package me.dannytatom.xibalba;

import me.dannytatom.xibalba.map.Map;

import java.util.ArrayList;

public class World {
  public final ArrayList<Map> maps;
  public int currentMapIndex = 0;

  public World() {
    maps = new ArrayList<>();
  }

  public Map getCurrentMap() {
    return maps.get(currentMapIndex);
  }

  public Map getMap(int index) {
    return maps.get(index);
  }
}
