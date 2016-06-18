package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.map.CaveGenerator;
import me.dannytatom.xibalba.map.Cell;

public class LoadingScreen implements Screen {
  private final Main main;

  private Stage stage;
  private Label label;

  /**
   * Loading Screen.
   *
   * @param main Instance of main class
   */
  public LoadingScreen(Main main) {
    this.main = main;
    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    label = new Label(null, main.skin);
    table.add(label);

    loadAssets();
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    if (main.assets.update()) {
      Cell[][] map = generateMap();

      main.playScreen = new PlayScreen(main, map);
      main.setScreen(main.playScreen);
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

  private void loadAssets() {
    label.setText("Loading assets");

    main.assets.load("sprites/main.atlas", TextureAtlas.class);
  }

  private Cell[][] generateMap() {
    label.setText("Generating map");

    return new CaveGenerator(
        main.assets.get("sprites/main.atlas"),
        MathUtils.random(50, 80), MathUtils.random(30, 60)
    ).map;
  }
}
