package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.PlayerInput;
import me.dannytatom.xibalba.WorldRenderer;
import me.dannytatom.xibalba.factories.MobFactory;
import me.dannytatom.xibalba.factories.PlayerFactory;
import me.dannytatom.xibalba.map.CaveGenerator;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.systems.AttributesSystem;
import me.dannytatom.xibalba.systems.BrainSystem;
import me.dannytatom.xibalba.systems.MovementSystem;
import me.dannytatom.xibalba.systems.PlayerSystem;
import me.dannytatom.xibalba.systems.ai.AttackSystem;
import me.dannytatom.xibalba.systems.ai.TargetSystem;
import me.dannytatom.xibalba.systems.ai.WanderSystem;

class PlayScreen implements Screen {
  private final Main game;
  private final WorldRenderer worldRenderer;
  private final SpriteBatch batch;
  private final Engine engine;

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

    // Add player entity
    Entity player = new PlayerFactory(game.assets).spawn(map.findPlayerStart());
    engine.addEntity(player);

    // Spawn some spider monkeys
    MobFactory mobFactory = new MobFactory(game.assets);
    for (int i = 0; i < 5; i++) {
      engine.addEntity(mobFactory.spawn("spiderMonkey", map.getRandomOpenPosition()));
    }

    // Setup engine (they're run in order added)
    engine.addSystem(new AttributesSystem());
    engine.addSystem(new PlayerSystem());
    engine.addSystem(new BrainSystem(map));
    engine.addSystem(new WanderSystem(map));
    engine.addSystem(new TargetSystem(map));
    engine.addSystem(new AttackSystem(map));
    engine.addSystem(new MovementSystem(map));

    // Setup input
    Gdx.input.setInputProcessor(new PlayerInput(game, player));

    // Setup renderers
    worldRenderer = new WorldRenderer(engine, batch, map);
  }

  @Override
  public void render(float delta) {
    worldRenderer.render();

    if (game.executeTurn) {
      engine.update(delta);

      game.executeTurn = false;
    }
  }

  @Override
  public void resize(int width, int height) {

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
  }
}
