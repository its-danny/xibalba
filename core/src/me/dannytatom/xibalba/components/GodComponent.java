package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class GodComponent implements Component {
  public final String name;
  public final ArrayList<String> hates;
  public final ArrayList<String> likes;
  public final ArrayList<String> wrath;
  public boolean hasWrath = false;

  /**
   * The god component.
   *
   * @param name  Their name
   * @param hates A list of things they hate
   * @param likes A list of things they like
   * @param wrath A list of things that happens when wrath triggers
   */
  public GodComponent(
      String name,
      ArrayList<String> hates, ArrayList<String> likes,
      ArrayList<String> wrath
  ) {
    this.name = name;
    this.hates = hates;
    this.likes = likes;
    this.wrath = wrath;
  }
}
