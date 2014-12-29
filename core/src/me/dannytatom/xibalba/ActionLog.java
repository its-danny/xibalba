package me.dannytatom.xibalba;

import java.util.ArrayList;

public class ActionLog {
  public final ArrayList<String> items;

  public ActionLog() {
    items = new ArrayList<>();
    items.add("Welcome to Xibalba...");
  }

  public void add(String str) {
    if (items.size() == 6) {
      items.remove(5);
    }

    items.add(0, str);
  }
}
