package me.dannytatom.xibalba;

import java.util.ArrayList;

public class ActionLog {
  public final ArrayList<String> things;

  public ActionLog() {
    things = new ArrayList<>();
    things.add("Make it to Xibalba and kill the 10 Lords of Xibalba");
  }

  /**
   * Add to the action log.
   *
   * @param thing The string to add
   */
  public void add(String thing) {
    if (things.size() == 6) {
      things.remove(5);
    }

    things.add(0, thing);
  }
}

