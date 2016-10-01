package me.dannytatom.xibalba.utils;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class JsonToLevel {
  public String type;
  public HashMap<String, String> size;
  public Array<HashMap<String, String>> enemies;
  public Array<HashMap<String, String>> items;
  public Array<HashMap<String, String>> traps;
}
