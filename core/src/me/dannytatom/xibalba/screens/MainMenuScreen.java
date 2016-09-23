package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.screens.creation.ReviewScreen;
import me.dannytatom.xibalba.ui.ActionButton;

public class MainMenuScreen implements Screen {
  private final Stage stage;

  /**
   * Main Menu Screen.
   *
   * @param main Instance of main class
   */
  public MainMenuScreen(Main main) {
    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    ActionButton continueButton = new ActionButton("C", "Continue");
    continueButton.setKeys(Input.Keys.C);
    continueButton.setAction(table, () -> main.setScreen(new LoadingScreen(main, false, null)));

    ActionButton newGameButton = new ActionButton("N", "New Game");
    newGameButton.setKeys(Input.Keys.N);
    newGameButton.setAction(table, () -> main.setScreen(new ReviewScreen(main)));

    ActionButton quitButton = new ActionButton("Q", "Quit");
    quitButton.setKeys(Input.Keys.Q);
    quitButton.setAction(table, () -> Gdx.app.exit());

    table.add(new Label("[LIGHT_GRAY]Xibalba v0.1.0[]", Main.skin)).pad(0, 0, 10, 0);
    table.row();

    if (Gdx.files.local("save").exists()) {
      table.add(continueButton).pad(0, 0, 10, 0);
      table.row();
    }

    table.add(newGameButton).pad(0, 0, 10, 0);
    table.row();
    table.add(quitButton);

    Gdx.input.setInputProcessor(stage);
    stage.setKeyboardFocus(table);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(
        Colors.get("screenBackground").r,
        Colors.get("screenBackground").g,
        Colors.get("screenBackground").b,
        Colors.get("screenBackground").a
    );

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();
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
