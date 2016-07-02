package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import me.dannytatom.xibalba.Main;

public class PauseScreen implements Screen {
  private final Main main;

  private final Stage stage;

  /**
   * Main Menu Screen.
   *
   * @param main Instance of main class
   */
  public PauseScreen(Main main) {
    this.main = main;
    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    TextButton returnToGameButton = new TextButton(
        "[DARK_GRAY][ [CYAN]R[DARK_GRAY] ][WHITE] Return to Game", main.skin
    );
    returnToGameButton.pad(5);
    returnToGameButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        returnToGameButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        returnToGameButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);

        main.setScreen(main.playScreen);
      }
    });

    TextButton mainMenuButton = new TextButton(
        "[DARK_GRAY][ [CYAN]M[DARK_GRAY] ][WHITE] Main Menu", main.skin
    );
    mainMenuButton.pad(5);
    mainMenuButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        mainMenuButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        mainMenuButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);

        main.playScreen.dispose();
        main.playScreen = null;

        main.setScreen(new MainMenuScreen(main));
      }
    });

    TextButton quitButton = new TextButton(
        "[DARK_GRAY][ [CYAN]Q[DARK_GRAY] ][WHITE] Quit", main.skin
    );
    quitButton.pad(5);
    quitButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        quitButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        quitButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);

        Gdx.app.exit();
      }
    });

    table.add(new Label("[LIGHT_GRAY]PAUSED[]", main.skin)).pad(0, 0, 10, 0);
    table.row();
    table.add(returnToGameButton).pad(0, 0, 10, 0);
    table.row();
    table.add(mainMenuButton).pad(0, 0, 10, 0);
    table.row();
    table.add(quitButton);

    Gdx.input.setInputProcessor(stage);
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

    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
      main.setScreen(main.playScreen);
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
      main.playScreen.dispose();
      main.playScreen = null;

      main.setScreen(new MainMenuScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      Gdx.app.exit();
    }

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
