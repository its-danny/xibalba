package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.JsonToLevel;
import me.dannytatom.xibalba.utils.PlayerSetup;
import me.dannytatom.xibalba.world.Map;
import me.dannytatom.xibalba.world.MapLight;
import me.dannytatom.xibalba.world.MapWeather;
import me.dannytatom.xibalba.world.WorldManager;
import me.dannytatom.xibalba.world.generators.CaveGenerator;
import me.dannytatom.xibalba.world.generators.ForestGenerator;

public class GeneratingWorldScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private final PlayerSetup playerSetup;

  /**
   * World generation screen.
   *
   * @param main        Instance of Main
   * @param playerSetup Holds data for player creation
   */
  public GeneratingWorldScreen(Main main, PlayerSetup playerSetup) {
    this.main = main;
    this.playerSetup = playerSetup;

    stage = new Stage(new FitViewport(960, 540));

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    table.add(new Label("HUN-CAME IS PREPARING.", Main.skin));

    new Thread(() -> Gdx.app.postRunnable(this::generateWorld)).start();
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

  private void generateWorld() {
    WorldManager.setup();

    ArrayList levels = (new Json()).fromJson(
        ArrayList.class, JsonToLevel.class, Gdx.files.internal("data/world.json")
    );

    for (int i = 0; i < levels.size(); i++) {
      JsonToLevel level = (JsonToLevel) levels.get(i);

      String[] widthRange = level.size.get("width").split(",");
      String[] heightRange = level.size.get("height").split(",");

      int mapWidth = MathUtils.random(
          Integer.parseInt(widthRange[0]), Integer.parseInt(widthRange[1])
      );

      int mapHeight = MathUtils.random(
          Integer.parseInt(heightRange[0]), Integer.parseInt(heightRange[1])
      );

      Gdx.app.log(
          "World Generation",
          "Starting " + level.type + " generation for level " + (i + 1)
              + ", size " + mapWidth + "x" + mapHeight
      );

      switch (level.type) {
        case "forest":
          ForestGenerator forestGenerator = new ForestGenerator(mapWidth, mapHeight);
          forestGenerator.generate();

          Map forestMap = new Map(i, "forest", forestGenerator.geometry);
          forestMap.paint();

          WorldManager.world.maps.add(forestMap);

          break;
        case "cave":
          CaveGenerator caveGenerator = new CaveGenerator(mapWidth, mapHeight);
          caveGenerator.generate();

          Map caveMap = new Map(i, "cave", caveGenerator.geometry);
          caveMap.paint();

          WorldManager.world.maps.add(caveMap);

          break;
        default:
          break;
      }

      spawnShit(level, i, i == levels.size() - 1);
    }

    WorldManager.world.setup(main);
    Main.playScreen = new PlayScreen(main);
    main.setScreen(Main.playScreen);
  }

  private void spawnShit(JsonToLevel level, int mapIndex, boolean isLast) {
    WorldManager.world.entities.put(mapIndex, new Array<>());

    // Spawn an entrance on every level but first
    if (mapIndex > 0) {
      Entity entrance = WorldManager.entityFactory.createEntrance(mapIndex);

      WorldManager.world.entities.get(mapIndex).add(entrance);

      WorldManager.world.getMap(mapIndex).entrance
          = ComponentMappers.position.get(entrance).pos;
    } else {
      WorldManager.world.getMap(mapIndex).entrance
          = WorldManager.mapHelpers.getRandomOpenPositionOnLand();
    }

    // Spawn an exit on every level but last
    if (!isLast) {
      Entity exit = WorldManager.entityFactory.createExit(mapIndex);

      WorldManager.world.entities.get(mapIndex).add(exit);
      WorldManager.world.getMap(mapIndex).exit = ComponentMappers.position.get(exit).pos;
    } else {
      WorldManager.world.getMap(mapIndex).exit
          = WorldManager.mapHelpers.getRandomOpenPositionOnLand();
    }

    // Spawn player on first
    if (mapIndex == 0) {
      WorldManager.player = playerSetup.create();
      WorldManager.world.entities.get(WorldManager.world.currentMapIndex).add(WorldManager.player);
    }

    // Traps
    for (int i = 0; i < level.traps.size; i++) {
      HashMap<String, String> trap = level.traps.get(i);
      String[] range = trap.get("spawnRange").split(",");
      int amount = MathUtils.random(Integer.parseInt(range[0]), Integer.parseInt(range[1]));

      for (int j = 0; j < amount; j++) {
        WorldManager.world.entities.get(mapIndex).add(
            WorldManager.entityFactory.createTrap(trap.get("name"),
                WorldManager.mapHelpers.getRandomOpenPositionOnLand(mapIndex)
            )
        );
      }
    }

    // Spawn items
    for (int i = 0; i < level.items.size; i++) {
      HashMap<String, String> item = level.items.get(i);
      String[] range = item.get("spawnRange").split(",");
      int amount = MathUtils.random(Integer.parseInt(range[0]), Integer.parseInt(range[1]));

      for (int j = 0; j < amount; j++) {
        WorldManager.world.entities.get(mapIndex).add(
            WorldManager.entityFactory.createItem(item.get("name"),
                WorldManager.mapHelpers.getRandomOpenPositionOnLand(mapIndex))
        );
      }
    }

    // Spawn enemies
    for (int i = 0; i < level.enemies.size; i++) {
      HashMap<String, String> enemy = level.enemies.get(i);
      String[] range = enemy.get("spawnRange").split(",");
      int amount = MathUtils.random(Integer.parseInt(range[0]), Integer.parseInt(range[1]));

      for (int j = 0; j < amount; j++) {
        Entity entity = WorldManager.entityFactory.createEnemy(
            enemy.get("name"), new Vector2(0, 0)
        );

        BrainComponent brain = ComponentMappers.brain.get(entity);
        Vector2 position;

        if (brain.dna.contains(BrainComponent.Dna.AQUATIC, false)) {
          if (WorldManager.world.getMap(mapIndex).hasWater) {
            position = WorldManager.mapHelpers.getRandomOpenPositionInWater(mapIndex);
          } else {
            continue;
          }
        } else {
          position = WorldManager.mapHelpers.getRandomOpenPositionOnLand(mapIndex);
        }

        ComponentMappers.position.get(entity).pos.set(position);
        WorldManager.world.entities.get(mapIndex).add(entity);
      }
    }

    // Lights
    WorldManager.world.getMap(mapIndex).light = new MapLight(mapIndex);

    // Weather
    if (Objects.equals(level.type, "forest")) {
      WorldManager.world.getMap(mapIndex).weather = new MapWeather(mapIndex);
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
