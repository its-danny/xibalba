package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.*;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.map.CaveGenerator;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.systems.AttributesSystem;
import me.dannytatom.xibalba.systems.PlayerSystem;
import me.dannytatom.xibalba.systems.actions.MeleeSystem;
import me.dannytatom.xibalba.systems.actions.MovementSystem;
import me.dannytatom.xibalba.systems.ai.BrainSystem;
import me.dannytatom.xibalba.systems.ai.TargetSystem;
import me.dannytatom.xibalba.systems.ai.WanderSystem;
import me.dannytatom.xibalba.utils.EntityFactory;

class PlayScreen implements Screen {
  private final Main game;
  private final WorldRenderer worldRenderer;
  private final UIRenderer uiRenderer;
  private final SpriteBatch batch;
  private final Engine engine;
  private final Entity player;

  /**
   * Play Screen.
   *
   * @param main Instance of Main class
   */
  public PlayScreen(Main main) {
    game = main;
    engine = new Engine();
    batch = new SpriteBatch();

    // Generate cave & initialize map
    CaveGenerator cave = new CaveGenerator(game.assets.get("sprites/cave.atlas"),
        MathUtils.random(50, 80), MathUtils.random(30, 60));
    Map map = new Map(engine, cave.map);

    EntityFactory entityFactory = new EntityFactory(game.assets);

    // Add player entity
    player = entityFactory.spawnPlayer(map.findPlayerStart());
    engine.addEntity(player);

    // Spawn some spider monkeys
    for (int i = 0; i < 5; i++) {
      engine.addEntity(entityFactory.spawnMob("spiderMonkey", map.getRandomOpenPosition()));
    }

    // Add ano item
    engine.addEntity(entityFactory.spawnItem("dagger"));

    // Setup action log
    ActionLog logger = new ActionLog();

    // Setup engine (they're run in order added)
    engine.addSystem(new AttributesSystem());
    engine.addSystem(new PlayerSystem());
    engine.addSystem(new BrainSystem(map));
    engine.addSystem(new WanderSystem(map));
    engine.addSystem(new TargetSystem(map));
    engine.addSystem(new MovementSystem(map));
    engine.addSystem(new MeleeSystem(engine, logger, map));

    // Setup input
    Gdx.input.setInputProcessor(new PlayerInput(game, map, player));

    // Setup renderers
    worldRenderer = new WorldRenderer(game, engine, batch, map, player);
    uiRenderer = new UIRenderer(logger);
  }

  @Override
  public void render(float delta) {
    if (game.executeTurn) {
      engine.update(delta);

      game.executeTurn = false;
    }

    worldRenderer.render();
    uiRenderer.render(delta);

    if (player.getComponent(AttributesComponent.class).health <= 0) {
      game.setScreen(new LoadingScreen(game));
    }
  }

  @Override
  public void resize(int width, int height) {
    uiRenderer.resize(width, height);
  }

  @Override
  public void show() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    batch.dispose();
    uiRenderer.dispose();
  }
}
