package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.map.CaveGenerator;
import me.dannytatom.xibalba.map.Level;
import me.dannytatom.xibalba.map.Map;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadingScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private final Label label;
  private boolean generating = false;

  /**
   * Loading Screen.
   *
   * @param main Instance of main class
   */
  public LoadingScreen(Main main) {
    this.main = main;
    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    label = new Label("HUN-CAME IS PREPARING.", Main.skin);
    table.add(label);

    // Start loading & generating shit
    loadAssets();
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

    label.clear();

    if (Main.assets.update()) {
      if (!generating) {
        generateWorld();

        Main.playScreen = new PlayScreen(main);
        main.setScreen(Main.playScreen);
      }
    }

    stage.act(delta);
    stage.draw();
  }

  private void loadAssets() {
    Main.assets.load("sprites/main.atlas", TextureAtlas.class);
  }

  private void generateWorld() {
    ArrayList levels = levelsFromJson();

    generating = true;

    for (int i = 0; i < levels.size(); i++) {
      Level level = (Level) levels.get(i);

      String[] widthRange = level.size.get("width").split(",");
      String[] heightRange = level.size.get("height").split(",");

      int mapWidth = MathUtils.random(
          Integer.parseInt(widthRange[0]), Integer.parseInt(widthRange[1])
      );

      int mapHeight = MathUtils.random(
          Integer.parseInt(heightRange[0]), Integer.parseInt(heightRange[1])
      );

      Gdx.app.log(
          "CaveGenerator",
          "Starting cave generation for cave " + (i + 1) + ", size " + mapWidth + "x" + mapHeight
      );

      // TODO: Once we have more types, check level.type to figure out which generator to use
      CaveGenerator generator = new CaveGenerator(mapWidth, mapHeight);
      generator.generate();

      Map map = new Map(generator.geometry, Main.assets.get("sprites/main.atlas"));
      map.paintCave();

      WorldManager.world.maps.add(map);

      spawnShit(level, i, i == levels.size());
    }

    // Add player entity
    WorldManager.entityHelpers.spawnPlayer(WorldManager.player, WorldManager.world.currentMapIndex);
    WorldManager.engine.addEntity(WorldManager.player);
  }

  private void spawnShit(Level level, int mapIndex, boolean isLast) {
    // Spawn an entrance on every level but first
    if (mapIndex > 0) {
      WorldManager.engine.addEntity(WorldManager.entityHelpers.spawnEntrance(mapIndex));
    }

    // Spawn an exit on every level but last
    if (!isLast) {
      WorldManager.engine.addEntity(WorldManager.entityHelpers.spawnExit(mapIndex));
    }

    // Spawn enemies
    for (int i = 0; i < level.enemies.size; i++) {
      HashMap<String, String> enemy = level.enemies.get(i);
      String[] range = enemy.get("spawnRange").split(",");
      int amount = MathUtils.random(Integer.parseInt(range[0]), Integer.parseInt(range[1]));

      for (int j = 0; j < amount; j++) {
        WorldManager.engine.addEntity(
            WorldManager.entityHelpers.spawnEnemy(enemy.get("name"),
                mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
        );
      }
    }

    // Spawn items
    for (int i = 0; i < level.items.size; i++) {
      HashMap<String, String> item = level.items.get(i);
      String[] range = item.get("spawnRange").split(",");
      int amount = MathUtils.random(Integer.parseInt(range[0]), Integer.parseInt(range[1]));

      for (int j = 0; j < amount; j++) {
        WorldManager.engine.addEntity(
            WorldManager.entityHelpers.spawnItem(item.get("name"),
                mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
        );
      }
    }
  }

  private ArrayList levelsFromJson() {
    return (new Json()).fromJson(
        ArrayList.class, Level.class, Gdx.files.internal("data/world.json")
    );
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
