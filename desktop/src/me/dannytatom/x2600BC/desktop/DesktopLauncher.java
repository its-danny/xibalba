package me.dannytatom.x2600BC.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.dannytatom.x2600BC.Main;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

    config.title = "2600 BC";
    config.width = 960;
    config.height = 480;
    config.resizable = false;
    config.fullscreen = false;

    new LwjglApplication(new Main(), config);
  }
}
