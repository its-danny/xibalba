package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.*;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.map.CaveGenerator;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.systems.AttributesSystem;
import me.dannytatom.xibalba.systems.EffectSystem;
import me.dannytatom.xibalba.systems.actions.MeleeSystem;
import me.dannytatom.xibalba.systems.actions.MovementSystem;
import me.dannytatom.xibalba.systems.actions.RangeSystem;
import me.dannytatom.xibalba.systems.ai.BrainSystem;
import me.dannytatom.xibalba.systems.ai.TargetSystem;
import me.dannytatom.xibalba.systems.ai.WanderSystem;
import me.dannytatom.xibalba.utils.CombatHelpers;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;
import me.dannytatom.xibalba.utils.SkillHelpers;

class PlayScreen implements Screen {
  private final Main game;
  private final WorldRenderer worldRenderer;
  private final InterfaceRenderer interfaceRenderer;
  private final SpriteBatch batch;
  private final Engine engine;
  private ActionLog actionLog;
  private EntityHelpers entityHelpers;
  private Map map;
  private InventoryHelpers inventoryHelpers;

  /**
   * Play Screen.
   *
   * @param main Instance of Main class
   */
  public PlayScreen(Main main) {
    game = main;
    engine = new Engine();
    batch = new SpriteBatch();

    // Setup action log
    actionLog = new ActionLog();

    entityHelpers = new EntityHelpers(engine, game.assets);

    // Generate cave & initialize map
    CaveGenerator cave = new CaveGenerator(game.assets.get("sprites/cave.atlas"),
        MathUtils.random(50, 80), MathUtils.random(30, 60));
    map = new Map(engine, entityHelpers, cave.map);

    // Add player entity
    entityHelpers.spawnPlayer(game.player, map.findPlayerStart());
    engine.addEntity(game.player);

    inventoryHelpers = new InventoryHelpers(game.player);

    // Spawn some spider monkeys
    for (int i = 0; i < 5; i++) {
      engine.addEntity(entityHelpers.spawnEnemy("spiderMonkey", map.getRandomOpenPosition()));
    }

    for (int i = 0; i < 2; i++) {
      engine.addEntity(entityHelpers.spawnItem("chippedFlint", map.getRandomOpenPosition()));
    }

    for (int i = 0; i < 3; i++) {
      engine.addEntity(entityHelpers.spawnItem("bomb", map.getRandomOpenPosition()));
    }

    CombatHelpers combatHelpers = new CombatHelpers(
        engine, actionLog, inventoryHelpers, new SkillHelpers(actionLog)
    );

    // Setup engine (they're run in order added)
    engine.addSystem(new AttributesSystem());
    engine.addSystem(new BrainSystem(entityHelpers, map));
    engine.addSystem(new WanderSystem(map));
    engine.addSystem(new TargetSystem(map));
    engine.addSystem(new MeleeSystem(combatHelpers));
    engine.addSystem(new RangeSystem(engine, map, entityHelpers, combatHelpers, inventoryHelpers));
    engine.addSystem(new EffectSystem(engine, map, combatHelpers));
    engine.addSystem(new MovementSystem(map));

    // Setup renderers
    worldRenderer = new WorldRenderer(game, engine, batch, map, game.player);
    interfaceRenderer = new InterfaceRenderer(game, actionLog, game.player);
  }

  @Override
  public void render(float delta) {
    if (game.executeTurn) {
      engine.update(delta);

      game.executeTurn = false;
    }

    worldRenderer.render(delta);
    interfaceRenderer.render(delta);

    if (game.player.getComponent(AttributesComponent.class).health <= 0) {
      game.setScreen(new MainMenuScreen(game));
    }
  }

  @Override
  public void resize(int width, int height) {
    interfaceRenderer.resize(width, height);
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(
        new PlayerInput(game, actionLog, map, entityHelpers, inventoryHelpers)
    );
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
    interfaceRenderer.dispose();
  }
}
