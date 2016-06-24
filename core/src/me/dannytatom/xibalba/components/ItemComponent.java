package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemComponent implements Component {
  public String name;
  public String description;
  public String skill;
  public String type;
  public boolean usesAmmunition;
  public String ammunitionType;
  public String location;
  public ArrayList<String> verbs;
  public HashMap<String, Integer> attributes;
  public HashMap<String, Boolean> actions;

  // TODO: This is only here 'cause it's in the JSON,
  // should fix how we load items
  public HashMap<String, Array<String>> visual;

  public boolean throwing = false;
}
