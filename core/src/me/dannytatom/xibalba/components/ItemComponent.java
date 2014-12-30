package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("UnusedDeclaration")
public class ItemComponent extends Component {
  public String name;
  public String skill;
  public String type;
  public ArrayList<String> verbs;
  public HashMap<String, Integer> attributes;
  public HashMap<String, Boolean> actions;

  public boolean equipped = false;
}
