package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemComponent extends Component {
  public String name;
  public String description;
  public String skill;
  public String type;
  public String effect;
  public int effectRange;
  public int effectTurns;
  public ArrayList<String> verbs;
  public HashMap<String, Integer> attributes;
  public HashMap<String, Boolean> actions;
  public String identifier;

  public boolean lookingAt = false;
  public boolean equipped = false;
}
