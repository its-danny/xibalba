package me.dannytatom.xibalba.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.dannytatom.xibalba.Main;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

    config.title = "Xibalba";
    config.width = 1280;
    config.height = 1024;

    new LwjglApplication(new Main(), config);
  }
}
