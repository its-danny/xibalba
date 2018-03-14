package me.dannytatom.xibalba.screens.creation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Objects;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.screens.GeneratingWorldScreen;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.PlayerSetup;

class NameScreen implements Screen {
  private final Main main;
  private final PlayerSetup playerSetup;

  private final Stage stage;
  private final TextField worldSeedField;
  private final TextField playerNameField;
  private final TextField playerColorField;
  private long worldSeed;

  /**
   * Character Creation: Review Screen.
   *
   * @param main Instance of main class
   */
  public NameScreen(Main main, PlayerSetup playerSetup) {
    this.main = main;
    this.playerSetup = playerSetup;

    stage = new Stage(new FitViewport(960, 540));
    worldSeed = System.currentTimeMillis();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    table.pad(10);
    stage.addActor(table);

    ActionButton backButton = new ActionButton("Q", "Back");
    backButton.setKeys(Input.Keys.Q);
    backButton.setAction(table, () -> main.setScreen(new YouScreen(main)));
    table.add(backButton).pad(0, 0, 10, 0).left();

    table.row();

    Label worldSeedLabel = new Label("World Seed", Main.skin);
    table.add(worldSeedLabel).pad(0, 0, 10, 0).width(Gdx.graphics.getWidth() / 2);
    table.row();
    worldSeedField = new TextField(worldSeed + "", Main.skin);
    table.add(worldSeedField).pad(0, 0, 10, 0).width(Gdx.graphics.getWidth() / 2);

    table.row();
    Label playerNameLabel = new Label("Name", Main.skin);
    table.add(playerNameLabel).pad(0, 0, 10, 0).width(Gdx.graphics.getWidth() / 2);
    table.row();
    playerNameField = new TextField(playerSetup.name, Main.skin);
    table.add(playerNameField).pad(0, 0, 10, 0).width(Gdx.graphics.getWidth() / 2);

    table.row();
    Label playerColorLabel = new Label("Color", Main.skin);
    table.add(playerColorLabel).pad(0, 0, 10, 0).width(Gdx.graphics.getWidth() / 2);
    table.row();
    playerColorField = new TextField(playerSetup.color, Main.skin);
    playerColorField.setMaxLength(6);
    table.add(playerColorField).pad(0, 0, 10, 0).width(Gdx.graphics.getWidth() / 2);

    ActionButton continueButton = new ActionButton("ENTER", "Begin Your Journey");
    continueButton.setKeys(Input.Keys.ENTER);
    continueButton.setAction(table, this::startGame);

    table.row();
    table.add(continueButton).left();

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

  private void startGame() {
    // Set world seed
    String seedInput = worldSeedField.getText();

    if (!Objects.equals(seedInput, "")) {
      worldSeed = Long.parseLong(seedInput);
    }

    Gdx.app.log("World Seed", worldSeed + "");
    MathUtils.random.setSeed(worldSeed);

    // Set player name
    String playerName = playerNameField.getText();
    playerSetup.name = Objects.equals(playerName, "") ? playerSetup.name : playerName;

    // Set player color
    String playerColor = playerColorField.getText();
    playerSetup.color = Objects.equals(playerColor, "") ? playerSetup.color : playerColor;

    main.setScreen(new GeneratingWorldScreen(main, playerSetup));
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
