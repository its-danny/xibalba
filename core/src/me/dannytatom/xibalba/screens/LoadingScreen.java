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
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.map.CaveGenerator;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.systems.AttributesSystem;
import me.dannytatom.xibalba.systems.MouseMovementSystem;
import me.dannytatom.xibalba.systems.actions.MeleeSystem;
import me.dannytatom.xibalba.systems.actions.MovementSystem;
import me.dannytatom.xibalba.systems.actions.RangeSystem;
import me.dannytatom.xibalba.systems.ai.BrainSystem;
import me.dannytatom.xibalba.systems.ai.TargetSystem;
import me.dannytatom.xibalba.systems.ai.WanderSystem;
import me.dannytatom.xibalba.systems.statuses.BleedingSystem;
import me.dannytatom.xibalba.systems.statuses.CrippledSystem;

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
        setup();
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

  private void setup() {
    // Setup engine (systems are run in order added)
    WorldManager.engine.addSystem(new AttributesSystem());
    WorldManager.engine.addSystem(new MouseMovementSystem());
    WorldManager.engine.addSystem(new BrainSystem());
    WorldManager.engine.addSystem(new WanderSystem());
    WorldManager.engine.addSystem(new TargetSystem());
    WorldManager.engine.addSystem(new MeleeSystem());
    WorldManager.engine.addSystem(new RangeSystem());
    WorldManager.engine.addSystem(new MovementSystem());
    WorldManager.engine.addSystem(new CrippledSystem());
    WorldManager.engine.addSystem(new BleedingSystem());
  }

  private void generateWorld() {
    generating = true;

    for (int i = 0; i < 5; i++) {
      int mapWidth = MathUtils.random(150, 200);
      int mapHeight = MathUtils.random(100, 150);

      Gdx.app.log(
          "CaveGenerator",
          "Starting cave generation for cave " + (i + 1) + ", size " + mapWidth + "x" + mapHeight
      );

      CaveGenerator generator = new CaveGenerator(mapWidth, mapHeight);
      generator.generate();

      Map map = new Map(generator.geometry, Main.assets.get("sprites/main.atlas"));
      map.paintCave();

      WorldManager.world.maps.add(map);

      spawnShit(i);
    }

    // Add player entity
    WorldManager.entityHelpers.spawnPlayer(WorldManager.player, WorldManager.world.currentMapIndex);
    WorldManager.engine.addEntity(WorldManager.player);
  }

  private void spawnShit(int mapIndex) {
    // Spawn an entrance on every level but first
    if (mapIndex > 0) {
      WorldManager.engine.addEntity(WorldManager.entityHelpers.spawnEntrance(mapIndex));
    }

    // Spawn an exit on every level but last
    if (mapIndex < 4) {
      WorldManager.engine.addEntity(WorldManager.entityHelpers.spawnExit(mapIndex));
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnEnemy("spiderMonkey",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnEnemy("bat",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnEnemy("jaguar",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnEnemy("giantSpider",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("chippedFlint",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("macuahuitl",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("shield",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("spear",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("bow",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 20; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("arrow",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 20; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("healthPlant",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 20; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnItem("strengthPlant",
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 50; i++) {
      WorldManager.engine.addEntity(
          WorldManager.entityHelpers.spawnRandomDecoration(
              mapIndex, WorldManager.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
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
