package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Engine;
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
import me.dannytatom.xibalba.utils.CombatHelpers;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.EquipmentHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;
import me.dannytatom.xibalba.utils.MapHelpers;
import me.dannytatom.xibalba.utils.SkillHelpers;

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

    label = new Label("HUN-CAME IS PREPARING.", main.skin);
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

    if (main.assets.update()) {
      if (!generating) {
        setup();
        generateWorld();

        main.playScreen = new PlayScreen(main);
        main.setScreen(main.playScreen);
      }
    }

    stage.act(delta);
    stage.draw();
  }

  private void loadAssets() {
    main.assets.load("sprites/main.atlas", TextureAtlas.class);
  }

  private void setup() {
    // Setup helpers
    main.mapHelpers = new MapHelpers(main);
    main.entityHelpers = new EntityHelpers(main);
    main.inventoryHelpers = new InventoryHelpers(main);
    main.equipmentHelpers = new EquipmentHelpers();
    main.skillHelpers = new SkillHelpers(main);
    main.combatHelpers = new CombatHelpers(main);

    // Setup engine (systems are run in order added)
    main.engine = new Engine();
    main.engine.addSystem(new AttributesSystem());
    main.engine.addSystem(new MouseMovementSystem(main));
    main.engine.addSystem(new BrainSystem(main));
    main.engine.addSystem(new WanderSystem(main));
    main.engine.addSystem(new TargetSystem(main));
    main.engine.addSystem(new MeleeSystem(main));
    main.engine.addSystem(new RangeSystem(main));
    main.engine.addSystem(new MovementSystem(main));
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

      Map map = new Map(generator.geometry, main.assets.get("sprites/main.atlas"));
      map.paintCave();

      main.world.maps.add(map);

      spawnShit(i);
    }

    // Add player entity
    main.entityHelpers.spawnPlayer(
        main.player, main.world.currentMapIndex, main.mapHelpers.getRandomOpenPosition()
    );
    main.engine.addEntity(main.player);
  }

  private void spawnShit(int mapIndex) {
    // Spawn an exit on every level but last
    main.engine.addEntity(
        main.entityHelpers.spawnExit(mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
    );

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnEnemy("spiderMonkey",
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnItem("chippedFlint",
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnItem("macuahuitl",
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnItem("shield",
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnItem("spear",
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnItem("bow",
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 20; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnItem("arrow",
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
      );
    }

    for (int i = 0; i < 50; i++) {
      main.engine.addEntity(
          main.entityHelpers.spawnRandomDecoration(
              mapIndex, main.mapHelpers.getRandomOpenPositionOnMap(mapIndex))
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
