package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import me.dannytatom.xibalba.Main;

public class LoadingScreen implements Screen {
  private final Main game;

  private SpriteBatch batch;
  private BitmapFont font;

  /**
   * Loading Screen.
   *
   * @param main Instance of main class
   */
  public LoadingScreen(Main main) {
    game = main;

    font = new BitmapFont();
    batch = new SpriteBatch();

    game.assets.load("sprites/utils/wander.png", Texture.class);
    game.assets.load("sprites/utils/target.png", Texture.class);
    game.assets.load("sprites/cave.atlas", TextureAtlas.class);
    game.assets.load("sprites/player.png", Texture.class);
    game.assets.load("sprites/spiderMonkey.png", Texture.class);
  }

  @Override
  public void render(float delta) {
    batch.begin();
    font.draw(batch, "Loading...", Gdx.graphics.getWidth() / 2 - 24, Gdx.graphics.getHeight() / 2);
    batch.end();

    if (game.assets.update()) {
      game.setScreen(new PlayScreen(game));
    }
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void show() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    batch.dispose();
    font.dispose();
  }
}
