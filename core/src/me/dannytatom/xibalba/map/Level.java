package me.dannytatom.xibalba.map;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Level {
  public String type;
  public HashMap<String, String> size;
  public Array<HashMap<String, String>> enemies;
  public Array<HashMap<String, String>> items;
}
