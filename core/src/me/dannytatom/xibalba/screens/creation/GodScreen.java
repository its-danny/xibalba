package me.dannytatom.xibalba.screens.creation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.PlayerSetup;
import me.dannytatom.xibalba.utils.YamlToAbility;
import me.dannytatom.xibalba.utils.YamlToGod;
import org.apache.commons.lang3.text.WordUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.ArrayList;

public class GodScreen implements Screen {
  private final PlayerSetup playerSetup;
  private final ArrayList<YamlToGod> godList;

  private final Stage stage;
  private final VerticalGroup godsGroup;
  private final VerticalGroup abilityGroup;
  private int godSelected = 0;

  /**
   * Character Creation: God Screen.
   *
   * @param main        Instance of main class
   * @param playerSetup Instance of PlayerSetup (used to store creation info)
   */
  public GodScreen(Main main, PlayerSetup playerSetup) {
    this.playerSetup = playerSetup;

    stage = new Stage(new FitViewport(960, 540));

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    table.pad(10);
    stage.addActor(table);

    Yaml yaml = new Yaml(new Constructor(YamlToGod.class));
    FileHandle directoryHandle = Gdx.files.internal("data/gods");
    godList = new ArrayList<>();

    for (FileHandle god : directoryHandle.list()) {
      YamlToGod data = (YamlToGod) yaml.load(god.reader());
      godList.add(data);
    }

    Table titleTable = new Table();

    HorizontalGroup titleGroup = new HorizontalGroup();
    titleGroup.space(10);
    titleTable.add(titleGroup).pad(0, 0, 10, 0).width(Gdx.graphics.getWidth());

    ActionButton backButton = new ActionButton("Q", "Back");
    backButton.setKeys(Input.Keys.Q);
    backButton.setAction(table, () -> main.setScreen(new YouScreen(main)));
    titleGroup.addActor(backButton);

    Label title = new Label("Your God", Main.skin);
    titleGroup.addActor(title);

    Label instructions = new Label(
        "[LIGHT_GRAY]Up & down to navigate gods, enter to select.",
        Main.skin
    );
    titleGroup.addActor(instructions);

    godsGroup = new VerticalGroup().top().left().columnLeft();
    abilityGroup = new VerticalGroup().top().left().columnLeft();

    float width = Gdx.graphics.getWidth() / 2;
    Table mainTable = new Table();
    mainTable.add(godsGroup).pad(0, 0, 10, 0).width(width);
    mainTable.add(abilityGroup).pad(0, 0, 10, 0).width(width);

    ActionButton continueButton = new ActionButton("ENTER", "Enter Your Name");
    continueButton.setKeys(Input.Keys.ENTER);
    continueButton.setAction(table, () -> goToNameScreen(main));

    table.add(titleTable);
    table.row();
    table.add(mainTable);
    table.row();
    table.add(continueButton).left();

    updateGodsGroup();
    updateAbilitiesGroup();

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

    if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
      if (godSelected > 0) {
        godSelected -= 1;
      }

      updateGodsGroup();
      updateAbilitiesGroup();
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      if (godSelected < godList.size() - 1) {
        godSelected += 1;
      }

      updateGodsGroup();
      updateAbilitiesGroup();
    }

    stage.act(delta);
    stage.draw();
  }

  private void updateGodsGroup() {
    godsGroup.clear();

    godsGroup.addActor(new Label("Gods", Main.skin));
    godsGroup.addActor(new Label("", Main.skin));

    for (int i = 0; i < godList.size(); i++) {
      YamlToGod god = godList.get(i);

      String string;

      if (i == godSelected) {
        string = "[DARK_GRAY]> [WHITE]" + god.name + "\n[DARK_GRAY]" + god.description;
      } else {
        string = "[LIGHT_GRAY]" + god.name + "\n[DARK_GRAY]" + god.description;
      }

      godsGroup.addActor(new Label(string, Main.skin));
    }
  }

  private void updateAbilitiesGroup() {
    abilityGroup.clear();

    abilityGroup.addActor(new Label("Abilities", Main.skin));
    abilityGroup.addActor(new Label("", Main.skin));

    YamlToGod god = godList.get(godSelected);

    Yaml yaml = new Yaml(new Constructor(YamlToAbility.class));
    god.abilities.forEach((String ability) -> {
      YamlToAbility details = (YamlToAbility) yaml.load(
          Gdx.files.internal("data/abilities/" + ability + ".yaml").read()
      );

      abilityGroup.addActor(
          new Label(
              createAbilityText(details.name, details.description, details.recharge
              ), Main.skin)
      );
    });
  }

  public void goToNameScreen(Main main) {
    playerSetup.god = godList.get(godSelected);
    main.setScreen(new NameScreen(main, playerSetup));
  }

  private String createAbilityText(String name, String description, int recharge) {
    String desc = WordUtils.wrap(description, 70);

    return name + "[LIGHT_GRAY] every " + recharge + " turns\n" + "[DARK_GRAY]" + desc;
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

  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
