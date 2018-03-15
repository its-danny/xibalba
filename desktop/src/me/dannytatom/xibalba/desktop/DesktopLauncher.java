package me.dannytatom.xibalba.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import me.dannytatom.xibalba.Main;

class DesktopLauncher {
  /**
   * Start desktop launcher.
   *
   * @param arg Arguments
   */
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

    config.title = "Xibalba";
    config.width = 960;
    config.height = 540;

    config.vSyncEnabled = false;
    config.foregroundFPS = 0;
    config.backgroundFPS = 0;

    new LwjglApplication(new Main(), config);
  }
}
