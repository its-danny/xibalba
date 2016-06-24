package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;

public class HelpScreen implements Screen {
  private final Main main;
  private final Stage stage;

  /**
   * The help screen, ya dingus.
   *
   * @param main Instance of Main class
   */
  public HelpScreen(Main main) {
    this.main = main;

    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    table.add(createHeader()).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() - 20);
    table.row();
    table.add(createHelp()).pad(0, 10, 10, 10).left();

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

    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      main.setScreen(new CharacterScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
      main.setScreen(new InventoryScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      main.setScreen(main.playScreen);
    }
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  private VerticalGroup createHeader() {
    String header = "[DARK_GRAY][ ";
    header += "[CYAN]1[LIGHT_GRAY] ";
    header += main.player.getComponent(AttributesComponent.class).name;
    header += "[DARK_GRAY] | ";
    header += "[CYAN]2[LIGHT_GRAY] Inventory";
    header += "[DARK_GRAY] | ";
    header += "[CYAN]3[WHITE] Help";
    header += "[DARK_GRAY] ]";

    VerticalGroup group = new VerticalGroup().center();
    group.addActor(new Label(header, main.skin));

    return group;
  }

  private VerticalGroup createHelp() {
    VerticalGroup group = new VerticalGroup().left();

    group.addActor(new Label("[LIGHT_GRAY]Always used[]", main.skin));
    group.addActor(new Label(null, main.skin));
    group.addActor(new Label("[YELLOW]hjkl yubn[] movement", main.skin));
    group.addActor(new Label(null, main.skin));
    group.addActor(new Label("[YELLOW]c[] character sheet", main.skin));
    group.addActor(new Label("[YELLOW]i[] inventory", main.skin));
    group.addActor(new Label("[YELLOW]s[] search", main.skin));
    group.addActor(new Label("[YELLOW]z[] skip turn", main.skin));
    group.addActor(new Label("[YELLOW]q[] quit out of things", main.skin));
    group.addActor(new Label("[YELLOW]space[] confirm & interact", main.skin));
    group.addActor(new Label("[YELLOW]esc[] pause", main.skin));
    group.addActor(new Label(null, main.skin));
    group.addActor(new Label("[LIGHT_GRAY]Weapon specific[]", main.skin));
    group.addActor(new Label(null, main.skin));
    group.addActor(new Label("[DARK_GRAY](Bow)[] [YELLOW]r[] release arrow", main.skin));

    return group;
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
