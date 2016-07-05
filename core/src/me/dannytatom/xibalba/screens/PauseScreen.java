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
import me.dannytatom.xibalba.ui.ActionButton;

public class PauseScreen implements Screen {
  private final Stage stage;

  /**
   * Main Menu Screen.
   *
   * @param main Instance of main class
   */
  public PauseScreen(Main main) {
    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    ActionButton returnToGameButton = new ActionButton("ESC", "Return to Game");
    returnToGameButton.setKeys(Input.Keys.ESCAPE);
    returnToGameButton.setAction(table, () -> main.setScreen(Main.playScreen));

    ActionButton mainMenuButton = new ActionButton("M", "Main Menu");
    mainMenuButton.setKeys(Input.Keys.M);
    mainMenuButton.setAction(table, () -> {
      Main.playScreen.dispose();
      main.setScreen(new MainMenuScreen(main));
    });

    ActionButton quitButton = new ActionButton("Q", "Quit");
    quitButton.setKeys(Input.Keys.Q);
    quitButton.setAction(table, () -> Gdx.app.exit());

    table.add(new Label("[LIGHT_GRAY]PAUSED[]", Main.skin)).pad(0, 0, 10, 0);
    table.row();
    table.add(returnToGameButton).pad(0, 0, 10, 0);
    table.row();
    table.add(mainMenuButton).pad(0, 0, 10, 0);
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
        Colors.get("CAVE_BACKGROUND").r,
        Colors.get("CAVE_BACKGROUND").g,
        Colors.get("CAVE_BACKGROUND").b,
        Colors.get("CAVE_BACKGROUND").a
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
