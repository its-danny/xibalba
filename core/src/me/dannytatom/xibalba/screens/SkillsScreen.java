package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.SkillsComponent;

public class SkillsScreen implements Screen {
  private final Main game;

  private Skin skin;
  private Stage stage;

  /**
   * View and manage skills.
   *
   * @param main Instance of the main class
   */
  public SkillsScreen(Main main) {
    game = main;

    stage = new Stage();

    skin = new Skin();
    skin.add("Inconsolata", game.font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));
    skin.getFont("default-font").getData().markupEnabled = true;

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    SkillsComponent skills = game.player.getComponent(SkillsComponent.class);
    VerticalGroup group = new VerticalGroup().left();

    if (skills.unarmed > 0) {
      group.addActor(skillLine("Unarmed", skills.unarmed));
    }

    if (skills.throwing > 0) {
      group.addActor(skillLine("Throwing", skills.throwing));
    }

    if (skills.slashing > 0) {
      group.addActor(skillLine("Slashing", skills.slashing));
    }

    if (skills.stabbing > 0) {
      group.addActor(skillLine("Stabbing", skills.stabbing));
    }

    table.add(new Label("Skills", skin)).left().pad(10);
    table.row();
    table.add(group).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      game.setScreen(game.playScreen);
    }
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

  private Label skillLine(String skill, int level) {
    String str = "[DARK_GRAY][[";

    switch (level) {
      case 0:
        str += "[DARK_GRAY]xxxxx";
        break;
      case 4:
        str += "[WHITE]x[DARK_GRAY]xxxx";
        break;
      case 6:
        str += "[WHITE]xx[DARK_GRAY]xxx";
        break;
      case 8:
        str += "[WHITE]xxx[DARK_GRAY]xx";
        break;
      case 10:
        str += "[WHITE]xxxx[DARK_GRAY]x";
        break;
      case 12:
        str += "[WHITE]xxxxx[DARK_GRAY]";
        break;
      default:
    }

    str += "[DARK_GRAY]]";

    return new Label(str + " [WHITE]" + skill, skin);
  }
}
