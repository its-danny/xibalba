package me.dannytatom.xibalba.screens.creation;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.DefectComponent;
import me.dannytatom.xibalba.components.DefectsComponent;
import me.dannytatom.xibalba.components.TraitComponent;
import me.dannytatom.xibalba.components.TraitsComponent;
import me.dannytatom.xibalba.screens.MainMenuScreen;

public class TraitsScreen implements Screen {
  private final Main main;

  private Stage stage;

  private int points = 10;
  private Label infoLabel;

  private Array<TraitComponent> traits;
  private Array<DefectComponent> defects;
  private Array<TraitComponent> selectedTraits;
  private Array<DefectComponent> selectedDefects;
  private VerticalGroup traitsGroup;
  private VerticalGroup defectsGroup;
  private VerticalGroup currentGroup;
  private int currentIndex;

  /**
   * Character Creation: Traits Screen.
   *
   * @param main Instance of main class
   */
  public TraitsScreen(Main main) {
    this.main = main;
    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    traits = new Array<>();
    selectedTraits = new Array<>();
    traitsGroup = new VerticalGroup().left();
    FileHandle traitsHandle = Gdx.files.internal("data/traits/");

    for (FileHandle entry : traitsHandle.list()) {
      TraitComponent trait =
          (new Json()).fromJson(
              TraitComponent.class, Gdx.files.internal("data/traits/" + entry.name())
          );
      traits.add(trait);
    }

    defects = new Array<>();
    selectedDefects = new Array<>();
    defectsGroup = new VerticalGroup().left();
    FileHandle defectsHandle = Gdx.files.internal("data/defects/");

    for (FileHandle entry : defectsHandle.list()) {
      DefectComponent defect =
          (new Json()).fromJson(
              DefectComponent.class, Gdx.files.internal("data/defects/" + entry.name())
          );
      defects.add(defect);
    }


    infoLabel = new Label(null, main.skin);
    currentGroup = traitsGroup;
    currentIndex = 0;

    table.add(infoLabel).left().pad(10);
    table.add(
        new Label("[LIGHT_GRAY]Take defects for more trait points", main.skin)
    ).left().pad(10);
    table.row();

    table.add(new Label("Traits", main.skin))
        .pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.add(new Label("Defects", main.skin))
        .pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

    table.row();
    table.add(traitsGroup).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.add(defectsGroup).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    infoLabel.setText(
        "[WHITE]SPACE[LIGHT_GRAY] to select, [WHITE]ENTER[LIGHT_GRAY] when done / [CYAN]"
            + Integer.toString(points) + "[LIGHT_GRAY] points left"
    );

    traitsGroup.clear();

    for (int i = 0; i < traits.size; i++) {
      TraitComponent trait = traits.get(i);
      String selected = "[_]";

      if (selectedTraits.contains(trait, true)) {
        selected = "[x]";
      }

      if (currentGroup == traitsGroup && currentIndex == i) {
        traitsGroup.addActor(
            new Label(
                "[WHITE]" + selected
                    + " [CYAN][" + trait.cost + "][WHITE] "
                    + trait.name + " [LIGHT_GRAY]"
                    + trait.description + "[]",
                main.skin)
        );
      } else {
        traitsGroup.addActor(
            new Label(
                "[LIGHT_GRAY]" + selected
                    + " [CYAN][" + trait.cost + "][LIGHT_GRAY] "
                    + trait.name + " [DARK_GRAY]"
                    + trait.description + "[]",
                main.skin)
        );
      }
    }

    defectsGroup.clear();

    for (int i = 0; i < defects.size; i++) {
      DefectComponent defect = defects.get(i);
      String selected = "[_]";

      if (selectedDefects.contains(defect, true)) {
        selected = "[x]";
      }

      if (currentGroup == defectsGroup && currentIndex == i) {
        defectsGroup.addActor(
            new Label(
                "[WHITE]" + selected
                    + " [RED][" + defect.prize + "][WHITE] "
                    + defect.name + " [LIGHT_GRAY]"
                    + defect.description + "[]",
                main.skin)
        );
      } else {
        defectsGroup.addActor(
            new Label(
                "[LIGHT_GRAY]" + selected
                    + " [RED][" + defect.prize + "][LIGHT_GRAY] "
                    + defect.name + " [DARK_GRAY]"
                    + defect.description + "[]",
                main.skin)
        );
      }
    }

    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
      if (currentIndex == currentGroup.getChildren().size - 1) {
        currentIndex = 0;
      } else {
        currentIndex += 1;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
      if (currentIndex == 0) {
        currentIndex = currentGroup.getChildren().size - 1;
      } else {
        currentIndex -= 1;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
      currentGroup = traitsGroup;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
      currentGroup = defectsGroup;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      if (currentGroup == traitsGroup) {
        TraitComponent trait = traits.get(currentIndex);

        if (selectedTraits.contains(trait, true)) {
          selectedTraits.removeValue(trait, true);
          points += trait.cost;
        } else {
          selectedTraits.add(trait);
          points -= trait.cost;
        }
      } else {
        DefectComponent defect = defects.get(currentIndex);

        if (selectedDefects.contains(defect, true)) {
          selectedDefects.removeValue(defect, true);
          points -= defect.prize;
        } else {
          selectedDefects.add(defect);
          points += defect.prize;
        }
      }
    }

    // Continue to next step of character creation
    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      // Create player entity
      main.player = new Entity();

      TraitsComponent traitsComponent = new TraitsComponent();
      DefectsComponent defectsComponent = new DefectsComponent();

      // Add selected traits to traitsComponent
      for (int i = 0; i < selectedTraits.size; i++) {
        TraitComponent trait = selectedTraits.get(i);
        Entity entity = new Entity();
        entity.add(trait);
        traitsComponent.traits.add(entity);
      }

      // Add selected traits to traitsComponent
      for (int i = 0; i < selectedDefects.size; i++) {
        DefectComponent defect = selectedDefects.get(i);
        Entity entity = new Entity();
        entity.add(defect);
        defectsComponent.defects.add(entity);
      }

      main.player.add(traitsComponent);
      main.player.add(defectsComponent);

      // Continue on
      main.setScreen(new ReviewScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      main.setScreen(new MainMenuScreen(main));
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
