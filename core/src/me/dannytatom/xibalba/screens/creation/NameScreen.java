package me.dannytatom.xibalba.screens.creation;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.*;
import me.dannytatom.xibalba.screens.LoadingScreen;
import me.dannytatom.xibalba.screens.MainMenuScreen;

import java.util.ArrayList;
import java.util.Objects;

public class NameScreen implements Screen {
  private final Main main;

  private Stage stage;
  private TextField nameField;

  /**
   * Character Creation: Traits Screen.
   *
   * @param main Instance of main class
   */
  public NameScreen(Main main) {
    this.main = main;

    stage = new Stage();

    Skin skin = new Skin();
    skin.add("Inconsolata", this.main.font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));
    skin.getFont("default-font").getData().markupEnabled = true;

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    nameField = new TextField("", skin);
    stage.setKeyboardFocus(nameField);

    VerticalGroup traitsGroup = new VerticalGroup().left();
    ArrayList<Entity> traits = this.main.player.getComponent(TraitsComponent.class).traits;
    ArrayList<Entity> defects = this.main.player.getComponent(DefectsComponent.class).defects;

    for (Entity entity : traits) {
      TraitComponent trait = entity.getComponent(TraitComponent.class);
      traitsGroup.addActor(new Label(trait.name + " [LIGHT_GRAY]" + trait.description, skin));
    }

    for (Entity entity : defects) {
      DefectComponent defect = entity.getComponent(DefectComponent.class);
      traitsGroup.addActor(new Label(defect.name + " [LIGHT_GRAY]" + defect.description, skin));
    }

    table.add(
        new Label(
            "[LIGHT_GRAY]After reviewing, enter a name then [WHITE]ENTER[LIGHT_GRAY] to begin",
            skin
        )
    ).pad(10).width(Gdx.graphics.getWidth() / 2 - 20);

    table.add(new Label("Traits & Defects", skin)).pad(10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.row();
    table.add(nameField).top().pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.add(traitsGroup).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

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

    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      String name = nameField.getText();

      if (!Objects.equals(name, "")) {
        main.player.add(new AttributesComponent(nameField.getText(), 100, 10, 50, 5, 5));
        main.setScreen(new LoadingScreen(main));
      }
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
