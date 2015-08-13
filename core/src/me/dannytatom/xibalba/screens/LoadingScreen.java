package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.dannytatom.xibalba.Main;

public class LoadingScreen implements Screen {
  private final Main game;

  private Stage stage;

  /**
   * Loading Screen.
   *
   * @param main Instance of main class
   */
  public LoadingScreen(Main main) {
    game = main;
    stage = new Stage();

    Skin skin = new Skin();
    skin.add("Inconsolata", game.font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    table.add(new Label("Loading assets", skin));

    game.assets.load("sprites/ui.atlas", TextureAtlas.class);
    game.assets.load("sprites/cave.atlas", TextureAtlas.class);
    game.assets.load("sprites/player.atlas", TextureAtlas.class);
    game.assets.load("sprites/spiderMonkey.atlas", TextureAtlas.class);

    game.assets.load("sprites/chippedFlint.png", Texture.class);
    game.assets.load("sprites/bomb.png", Texture.class);
    game.assets.load("sprites/poison.png", Texture.class);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

    if (game.assets.update()) {
      game.playScreen = new PlayScreen(game);
      game.setScreen(game.playScreen);
    }
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void show() {

  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
