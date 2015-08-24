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
import me.dannytatom.xibalba.components.AttributesComponent;

public class InventoryScreen implements Screen {
  private final Main main;

  private Skin skin;
  private Stage stage;

  /**
   * View and manage inventory.
   *
   * @param main Instance of the main class
   */
  public InventoryScreen(Main main) {
    this.main = main;

    stage = new Stage();

    skin = new Skin();
    skin.add("Inconsolata", this.main.font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));
    skin.getFont("default-font").getData().markupEnabled = true;

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    table.add(createHeader()).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() - 20);

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      main.setScreen(new CharacterScreen(main));
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
    header += "[CYAN]2[WHITE] Inventory";
    header += "[DARK_GRAY] ]";

    VerticalGroup group = new VerticalGroup().center();
    group.addActor(new Label(header, skin));

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
