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
import me.dannytatom.xibalba.World;
import me.dannytatom.xibalba.map.CaveGenerator;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.systems.AttributesSystem;
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
        spawnShit();

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
    main.entityHelpers = new EntityHelpers(main);
    main.inventoryHelpers = new InventoryHelpers(main);
    main.equipmentHelpers = new EquipmentHelpers();
    main.skillHelpers = new SkillHelpers(main);
    main.combatHelpers = new CombatHelpers(main);

    // Setup engine (systems are run in order added)
    main.engine = new Engine();
    main.engine.addSystem(new AttributesSystem());
    main.engine.addSystem(new BrainSystem(main));
    main.engine.addSystem(new WanderSystem(main));
    main.engine.addSystem(new TargetSystem(main));
    main.engine.addSystem(new MeleeSystem(main));
    main.engine.addSystem(new RangeSystem(main));
    main.engine.addSystem(new MovementSystem(main));
  }

  private void generateWorld() {
    generating = true;

    main.world = new World();

    for (int i = 0; i < 4; i++) {
      CaveGenerator generator = new CaveGenerator(MathUtils.random(80, 100), MathUtils.random(50, 80));
      generator.generate();

      Map map = new Map(main, generator.geometry);
      map.paintCave();

      main.world.maps.add(map);
    }
  }

  private void spawnShit() {
    // Add player entity
    main.entityHelpers.spawnPlayer(main.player, main.getCurrentMap().findPlayerStart());
    main.engine.addEntity(main.player);

    // Spawn an exit somewhere
    main.engine.addEntity(main.entityHelpers.spawnExit(main.getCurrentMap().getRandomOpenPosition()));

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(main.entityHelpers.spawnEnemy("spiderMonkey", main.getCurrentMap().getRandomOpenPosition()));
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(main.entityHelpers.spawnItem("chippedFlint", main.getCurrentMap().getRandomOpenPosition()));
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(main.entityHelpers.spawnItem("macuahuitl", main.getCurrentMap().getRandomOpenPosition()));
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(main.entityHelpers.spawnItem("shield", main.getCurrentMap().getRandomOpenPosition()));
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(main.entityHelpers.spawnItem("spear", main.getCurrentMap().getRandomOpenPosition()));
    }

    for (int i = 0; i < 5; i++) {
      main.engine.addEntity(main.entityHelpers.spawnItem("bow", main.getCurrentMap().getRandomOpenPosition()));
    }

    for (int i = 0; i < 20; i++) {
      main.engine.addEntity(main.entityHelpers.spawnItem("arrow", main.getCurrentMap().getRandomOpenPosition()));
    }

    for (int i = 0; i < 50; i++) {
      main.engine.addEntity(main.entityHelpers.spawnRandomDecoration(main.getCurrentMap().getRandomOpenPosition()));
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
