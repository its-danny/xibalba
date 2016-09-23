package me.dannytatom.xibalba;

import java.util.ArrayList;

public class ActionLog {
  public final ArrayList<String> actions;

  public ActionLog() {
    actions = new ArrayList<>();
    actions.add("Make it to Xibalba and kill the 10 Lords");
  }

  /**
   * Add to the action log.
   *
   * @param thing The string to add
   */
  public void add(String thing) {
    if (actions.size() == 5) {
      actions.remove(4);
    }

    actions.add(0, thing);
  }
}

