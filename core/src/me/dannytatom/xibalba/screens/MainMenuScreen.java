package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.screens.creation.TraitsScreen;

public class MainMenuScreen implements Screen {
  private final Main main;

  private final Stage stage;

  /**
   * Main Menu Screen.
   *
   * @param main Instance of main class
   */
  public MainMenuScreen(Main main) {
    this.main = main;
    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    table.add(new Label("[CYAN]N[]ew Game", main.skin));
    table.row();
    table.add(new Label("[CYAN]Q[]uit", main.skin));

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
      main.setScreen(new TraitsScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      Gdx.app.exit();
    }
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}