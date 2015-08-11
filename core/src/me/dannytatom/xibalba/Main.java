package me.dannytatom.xibalba;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import me.dannytatom.xibalba.screens.MainMenuScreen;

public class Main extends Game {
  public AssetManager assets;

  public boolean debug = true;
  public boolean executeTurn = false;

  /**
   * Initialize the asset manager and start the loading screen.
   */
  public void create() {
    assets = new AssetManager();

    Colors.put("CYAN", parseColor("5bb9c7"));
    Colors.put("RED", parseColor("cc4141"));
    Colors.put("LIGHT_GRAY", parseColor("999999"));
    Colors.put("DARK_GRAY", parseColor("666666"));

    setScreen(new MainMenuScreen(this));
  }

  private Color parseColor(String hex) {
    String s1 = hex.substring(0, 2);
    int v1 = Integer.parseInt(s1, 16);
    float f1 = (float) v1 / 255f;
    String s2 = hex.substring(2, 4);
    int v2 = Integer.parseInt(s2, 16);
    float f2 = (float) v2 / 255f;
    String s3 = hex.substring(4, 6);
    int v3 = Integer.parseInt(s3, 16);
    float f3 = (float) v3 / 255f;
    return new Color(f1, f2, f3, 1);
  }
}