package me.dannytatom.xibalba;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import me.dannytatom.xibalba.screens.LoadingScreen;

public class Main extends Game {
  public AssetManager assets;

  public boolean debug = false;
  public boolean executeTurn = false;

  /**
   * Initialize the asset manager and start the loading screen.
   */
  public void create() {
    assets = new AssetManager();

    setScreen(new LoadingScreen(this));
  }
}