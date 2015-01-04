package me.dannytatom.xibalba;

import java.util.ArrayList;

public class ActionLog {
  public final ArrayList<String> items;

  public ActionLog() {
    items = new ArrayList<>();
    items.add("Make it to Xibalba, Necahual, and kill the 10 Lords of Xibalba");
  }

  public void add(String str) {
    if (items.size() == 6) {
      items.remove(5);
    }

    items.add(0, str);
  }
}
