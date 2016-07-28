package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class ItemComponent implements Component {
  public String type;
  public String name;
  public String description;

  public HashMap<String, Array<String>> visual;

  public boolean twoHanded;
  public String skill;
  public String location;
  public String ammunition;

  public HashMap<String, Integer> attributes;
  public Array<String> actions;
  public Array<String> verbs;

  public HashMap<String, String> effects;

  public boolean throwing = false;
}
