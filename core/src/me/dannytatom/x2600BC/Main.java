package me.dannytatom.x2600BC;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import me.dannytatom.x2600BC.screens.LoadingScreen;

public class Main extends Game {
    public AssetManager assets;

    public boolean lightEnabled = true;

    public void create() {
        this.assets = new AssetManager();

        setScreen(new LoadingScreen(this));
    }

    public void render() {
        super.render();
    }
}