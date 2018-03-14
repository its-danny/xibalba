package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

public class HelpScreen implements Screen {
  private final Stage stage;

  /**
   * Help screen.
   *
   * @param main Instance of Main
   */
  public HelpScreen(Main main) {
    stage = new Stage(new FitViewport(960, 540));

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    Table titleTable = new Table();

    HorizontalGroup titleGroup = new HorizontalGroup();
    titleGroup.space(10);
    titleTable.add(titleGroup).pad(10).width(Gdx.graphics.getWidth() - 20);

    ActionButton closeButton = new ActionButton("Q", null);
    closeButton.setKeys(Input.Keys.Q);
    closeButton.setAction(table, () -> main.setScreen(Main.playScreen));
    titleGroup.addActor(closeButton);

    Label title = new Label("Help", Main.skin);
    titleGroup.addActor(title);

    VerticalGroup group = new VerticalGroup().top().left().columnLeft();

    Table helpTable = new Table();
    helpTable.add(group).pad(10).top().left();

    group.addActor(new Label("[DARK_GRAY]Movement & melee attacks:[] numpad keys", Main.skin));
    group.addActor(new Label("[DARK_GRAY]Shoot range weapon:[] r", Main.skin));
    group.addActor(new Label("[DARK_GRAY]Focused attack:[] shift + attack key", Main.skin));
    group.addActor(new Label("[DARK_GRAY]Look at a tile you're not on:[] s", Main.skin));
    group.addActor(new Label("[DARK_GRAY]Throw:[] t", Main.skin));
    group.addActor(new Label("[DARK_GRAY]Confirm action:[] space", Main.skin));
    group.addActor(new Label("[DARK_GRAY]Close dialogs or cancel actions:[] q", Main.skin));

    table.add(titleTable);
    table.row();
    table.add(helpTable).left();

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
