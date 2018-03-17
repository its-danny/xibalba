package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.utils.SoundManager;

import org.yaml.snakeyaml.Yaml;

public class LoadingScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private final Label label;
  private volatile boolean isLoading;

  /**
   * Loading screen.
   *
   * @param main Instance of Main
   */
  public LoadingScreen(Main main) {
    this.main = main;

    stage = new Stage(new FitViewport(960, 540));

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    label = new Label("", Main.skin);
    table.add(label);

    isLoading = true;

    new Thread(() -> Gdx.app.postRunnable(this::loadAssets)).start();
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

    if (!isLoading && Main.assets.update()) {
      Main.asciiAtlas = Main.assets.get("sprites/qbicfeet_10x10.atlas");
      Main.soundManager = new SoundManager();

      main.setScreen(new MainMenuScreen(main));
    }

    stage.act(delta);
    stage.draw();
  }

  private void loadAssets() {
    // Data
    label.setText("Loading data");
    Yaml dataManifestYaml = new Yaml();
    FileHandle dataManifest = Gdx.files.internal("data/manifest.yaml");
    //noinspection unchecked
    HashMap<String, ArrayList<String>> dataFiles
        = (HashMap<String, ArrayList<String>>) dataManifestYaml.load(dataManifest.reader());

    for (Map.Entry<String, ArrayList<String>> entry : dataFiles.entrySet()) {
      String category = entry.getKey();
      ArrayList<String> files = entry.getValue();

      switch (category) {
        case "abilities": {
          Main.abilitiesData = new HashMap<>();

          for (String file : files) {
            FileHandle handler = Gdx.files.internal("data/" + file);
            Main.abilitiesData.put(
                file.split("/")[1].replaceAll(".yaml", ""),
                handler.readString()
            );
          }

          break;
        }
        case "traits": {
          Main.traitsData = new HashMap<>();

          for (String file : files) {
            FileHandle handler = Gdx.files.internal("data/" + file);
            Main.traitsData.put(file.split("/")[1].replaceAll(".yaml", ""), handler.readString());
          }

          break;
        }
        case "defects": {
          Main.defectsData = new HashMap<>();

          for (String file : files) {
            FileHandle handler = Gdx.files.internal("data/" + file);
            Main.defectsData.put(file.split("/")[1].replaceAll(".yaml", ""), handler.readString());
          }

          break;
        }
        case "gods": {
          Main.godsData = new HashMap<>();

          for (String file : files) {
            FileHandle handler = Gdx.files.internal("data/" + file);
            Main.godsData.put(file.split("/")[1].replaceAll(".yaml", ""), handler.readString());
          }

          break;
        }
        case "enemies": {
          Main.enemiesData = new HashMap<>();

          for (String file : files) {
            FileHandle handler = Gdx.files.internal("data/" + file);
            Main.enemiesData.put(file.split("/")[1].replaceAll(".yaml", ""), handler.readString());
          }

          break;
        }
        case "items": {
          Main.itemsData = new HashMap<>();

          for (String file : files) {
            FileHandle handler = Gdx.files.internal("data/" + file);
            Main.itemsData.put(file.split("/")[1].replaceAll(".yaml", ""), handler.readString());
          }

          break;
        }
        default:
      }
    }

    label.setText("Loading assets");

    Main.assets.load("i18n/xibalba", I18NBundle.class);
    Main.assets.load("sprites/qbicfeet_10x10.atlas", TextureAtlas.class);
    Main.assets.load("sounds/Stab_Punch_Hack_12.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_13.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_14.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_15.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_17.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_22.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_09.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_18.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_19.wav", Sound.class);
    Main.assets.load("sounds/Stab_Punch_Hack_63.wav", Sound.class);

    isLoading = false;
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
