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
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.screens.LoadingScreen;
import me.dannytatom.xibalba.screens.MainMenuScreen;
import me.dannytatom.xibalba.ui.ActionButton;

import java.util.Objects;

public class ReviewScreen implements Screen {
  private final Main main;

  private final Stage stage;
  private final TextField worldSeedField;
  private final TextField playerNameField;

  /**
   * Character Creation: Review Screen.
   *
   * @param main Instance of main class
   */
  public ReviewScreen(Main main) {
    this.main = main;

    stage = new Stage();

    WorldManager.setup();
    WorldManager.world.seed = System.currentTimeMillis();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    table.pad(10);
    stage.addActor(table);

    ActionButton backButton = new ActionButton("Q", "Back to Main Menu");
    backButton.setKeys(Input.Keys.Q);
    backButton.setAction(table, () -> main.setScreen(new MainMenuScreen(main)));
    table.add(backButton).pad(0, 0, 10, 0).left();

    table.row();

    Label worldSeedLabel = new Label("World Seed", Main.skin);
    table.add(worldSeedLabel).pad(0, 0, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.row();
    worldSeedField = new TextField(WorldManager.world.seed + "", Main.skin);
    table.add(worldSeedField).pad(0, 0, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

    table.row();
    Label playerNameLabel = new Label("Name", Main.skin);
    table.add(playerNameLabel).pad(0, 0, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.row();
    playerNameField = new TextField("Aapo" + "", Main.skin);
    table.add(playerNameField).pad(0, 0, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

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
        Colors.get("CAVE_BACKGROUND").r,
        Colors.get("CAVE_BACKGROUND").g,
        Colors.get("CAVE_BACKGROUND").b,
        Colors.get("CAVE_BACKGROUND").a
    );

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();
  }

  private void startGame() {
    // Set world seed
    String seedInput = worldSeedField.getText();

    if (!Objects.equals(seedInput, "")) {
      WorldManager.world.seed = Long.parseLong(seedInput);
    }

    Gdx.app.log("World Seed", WorldManager.world.seed + "");
    MathUtils.random.setSeed(WorldManager.world.seed);

    // Set player name
    String playerName = playerNameField.getText();

    if (Objects.equals(playerName, "")) {
      playerName = "Aapo";
    }

    WorldManager.player.add(new AttributesComponent(playerName, "It's you", 100, 10, 4, 4));

    main.setScreen(new LoadingScreen(main));
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
