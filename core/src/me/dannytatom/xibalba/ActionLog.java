package me.dannytatom.xibalba;

import com.badlogic.gdx.utils.I18NBundle;

import java.util.ArrayList;

public class ActionLog {
  private final I18NBundle i18n;
  public final ArrayList<String> actions;

  public ActionLog() {
    i18n = Main.assets.get("i18n/xibalba", I18NBundle.class);
    actions = new ArrayList<>();

    add("intro");
  }

  public void add(String key, Object... args) {
    if (actions.size() == 5) {
      actions.remove(4);
    }

    actions.add(0, i18n.format(key, args));
  }
}

