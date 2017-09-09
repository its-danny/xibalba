package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class GodComponent implements Component {
  public final String name;
  public final String description;
  public ArrayList<String> hates;
  public ArrayList<String> likes;

  public GodComponent(String name, String description, ArrayList<String> hates, ArrayList<String> likes) {
    this.name = name;
    this.description = description;
    this.hates = hates;
    this.likes = likes;
  }
}
