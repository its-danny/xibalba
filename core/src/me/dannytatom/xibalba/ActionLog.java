package me.dannytatom.xibalba;

import com.badlogic.gdx.utils.I18NBundle;

import java.util.ArrayList;

public class ActionLog {
  public final ArrayList<String> actions;
  private final I18NBundle i18n;

  /**
   * Handles adding messages to the HUD.
   */
  public ActionLog() {
    i18n = Main.assets.get("i18n/xibalba", I18NBundle.class);
    actions = new ArrayList<>();

    add("intro");
  }

  /**
   * Add a message to the action log.
   *
   * @param key  The message key (found in i18n/xibalba.properties)
   * @param args Arguments to pass to the message
   */
  public void add(String key, Object... args) {
    if (actions.size() == 5) {
      actions.remove(4);
    }

    actions.add(0, i18n.format(key, args));
  }
}

