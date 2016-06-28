package me.dannytatom.xibalba.screens.creation;

import com.badlogic.ashley.core.Entity;
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
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DefectComponent;
import me.dannytatom.xibalba.components.DefectsComponent;
import me.dannytatom.xibalba.components.TraitComponent;
import me.dannytatom.xibalba.components.TraitsComponent;
import me.dannytatom.xibalba.screens.LoadingScreen;
import me.dannytatom.xibalba.screens.MainMenuScreen;

import java.util.ArrayList;
import java.util.Objects;

class ReviewScreen implements Screen {
  private final Main main;

  private final Stage stage;
  private TextField worldSeedField;
  private TextField playerNameField;

  /**
   * Character Creation: Review Screen.
   *
   * @param main Instance of main class
   */
  public ReviewScreen(Main main) {
    this.main = main;

    stage = new Stage();

    main.world.seed = System.currentTimeMillis();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    VerticalGroup traitsGroup = new VerticalGroup().left();
    ArrayList<Entity> traits = this.main.player.getComponent(TraitsComponent.class).traits;
    ArrayList<Entity> defects = this.main.player.getComponent(DefectsComponent.class).defects;

    for (Entity entity : traits) {
      TraitComponent trait = entity.getComponent(TraitComponent.class);
      traitsGroup.addActor(new Label(trait.name + " [LIGHT_GRAY]" + trait.description, main.skin));
    }

    for (Entity entity : defects) {
      DefectComponent defect = entity.getComponent(DefectComponent.class);
      traitsGroup.addActor(
          new Label(defect.name + " [LIGHT_GRAY]" + defect.description, main.skin)
      );
    }

    table.add(
        new Label(
            "[LIGHT_GRAY]After reviewing, enter a name then [WHITE]ENTER[LIGHT_GRAY] to begin",
            main.skin
        )
    ).pad(10).width(Gdx.graphics.getWidth() / 2 - 20);

    table.row();
    table.add(traitsGroup).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

    table.row();
    Label worldSeedLabel = new Label("World Seed", main.skin);
    table.add(worldSeedLabel).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.row();
    worldSeedField = new TextField(main.world.seed + "", main.skin);
    table.add(worldSeedField).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

    table.row();
    Label playerNameLabel = new Label("Name", main.skin);
    table.add(playerNameLabel).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.row();
    playerNameField = new TextField("Aapo" + "", main.skin);
    table.add(playerNameField).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

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

    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      // Set world seed
      String seedInput = worldSeedField.getText();

      if (!Objects.equals(seedInput, "")) {
        main.world.seed = Long.parseLong(seedInput);
      }

      Gdx.app.log("World Seed", main.world.seed + "");
      MathUtils.random.setSeed(main.world.seed);

      // Set player name
      String playerName = playerNameField.getText();

      if (Objects.equals(playerName, "")) {
        playerName = "Aapo";
      }

      main.player.add(new AttributesComponent(playerName, "It's you", 100, 10, 50, 5, 5));
      main.setScreen(new LoadingScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      main.setScreen(new MainMenuScreen(main));
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
