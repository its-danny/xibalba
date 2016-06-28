package me.dannytatom.xibalba.screens.creation;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
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
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.TraitComponent;
import me.dannytatom.xibalba.components.TraitsComponent;
import me.dannytatom.xibalba.screens.MainMenuScreen;

public class TraitsScreen implements Screen {
  private final Main main;

  private final Stage stage;
  private final Label infoLabel;
  private final VerticalGroup traitsGroup;
  private final VerticalGroup defectsGroup;
  private Array<TraitComponent> traits;
  private Array<DefectComponent> defects;
  private Array<TraitComponent> selectedTraits;
  private Array<DefectComponent> selectedDefects;
  private int points = 10;
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

    traitsGroup = new VerticalGroup().left();
    defectsGroup = new VerticalGroup().left();

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

    loadTraits();
    loadDefects();

    Gdx.input.setInputProcessor(stage);
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
      main.player.add(new SkillsComponent());

      TraitsComponent traitsComponent = new TraitsComponent();
      DefectsComponent defectsComponent = new DefectsComponent();

      // Add selected traits to traitsComponent
      for (int i = 0; i < selectedTraits.size; i++) {
        TraitComponent trait = selectedTraits.get(i);
        Entity entity = new Entity();
        entity.add(trait);
        traitsComponent.traits.add(entity);

        SkillsComponent skills = main.player.getComponent(SkillsComponent.class);

        switch (trait.name) {
          case "Archer":
            skills.archery = 6;
            break;
          case "Brawler":
            skills.unarmed = 6;
            break;
          case "Brute":
            skills.bashing = 6;
            break;
          case "Slasher":
            skills.slashing = 6;
            break;
          case "Slinger":
            skills.throwing = 6;
            break;
          case "Warrior":
            skills.piercing = 6;
            break;
          default:
        }
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

    stage.act(delta);
    stage.draw();
  }

  private void loadTraits() {
    traits = new Array<>();
    selectedTraits = new Array<>();

    traits.add((new Json()).fromJson(
        TraitComponent.class, Gdx.files.internal("data/traits/archer.json")
    ));

    traits.add((new Json()).fromJson(
        TraitComponent.class, Gdx.files.internal("data/traits/brawler.json")
    ));

    traits.add((new Json()).fromJson(
        TraitComponent.class, Gdx.files.internal("data/traits/brute.json")
    ));

    traits.add((new Json()).fromJson(
        TraitComponent.class, Gdx.files.internal("data/traits/slasher.json")
    ));

    traits.add((new Json()).fromJson(
        TraitComponent.class, Gdx.files.internal("data/traits/slinger.json")
    ));

    traits.add((new Json()).fromJson(
        TraitComponent.class, Gdx.files.internal("data/traits/warrior.json")
    ));
  }

  private void loadDefects() {
    defects = new Array<>();
    selectedDefects = new Array<>();
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
