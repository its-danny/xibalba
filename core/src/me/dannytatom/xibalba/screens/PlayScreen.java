package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.dannytatom.xibalba.HudRenderer;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.PlayerInput;
import me.dannytatom.xibalba.WorldManager;
import me.dannytatom.xibalba.WorldRenderer;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

class PlayScreen implements Screen {
  private final Main main;

  private final FPSLogger fps;
  private final WorldRenderer worldRenderer;
  private final HudRenderer hudRenderer;
  private final InputMultiplexer multiplexer;

  private final SpriteBatch batch;
  private float autoTimer = 0;

  /**
   * Play Screen.
   *
   * @param main Instance of Main class
   */
  public PlayScreen(Main main) {
    this.main = main;

    autoTimer = 0;
    fps = new FPSLogger();
    batch = new SpriteBatch();

    // Add player entity
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);
    playerPosition.pos = WorldManager.mapHelpers.getEntrancePosition();

    // Setup renderers
    OrthographicCamera worldCamera = new OrthographicCamera();
    worldRenderer = new WorldRenderer(worldCamera, batch);
    hudRenderer = new HudRenderer(main, batch);

    // Setup input
    multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(hudRenderer.stage);
    multiplexer.addProcessor(new PlayerInput(worldCamera));

    // Change state to playing
    WorldManager.state = WorldManager.State.PLAYING;

    Gdx.app.log("PlayScreen", "Game Started");
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

    fps.log();

    autoTimer += delta;

    // In some cases, we want the game to take turns on it's own
    if ((WorldManager.state == WorldManager.State.MOVING || WorldManager.entityHelpers.skipTurn(WorldManager.player)) && autoTimer >= .10) {
      autoTimer = 0;
      WorldManager.executeTurn = true;
    }

    // Update engine if it's time to execute a turn
    if (WorldManager.executeTurn) {
      WorldManager.turnCount += 1;

      WorldManager.engine.update(delta);
      WorldManager.executeTurn = false;
    }

    if (ComponentMappers.attributes.get(WorldManager.player).health <= 0) {
      WorldManager.world.currentMapIndex = 0;

      Main.playScreen.dispose();
      main.setScreen(new MainMenuScreen(main));
    } else {
      worldRenderer.render();
      hudRenderer.render(delta);
    }
  }

  @Override
  public void resize(int width, int height) {
    worldRenderer.resize(width, height);
    hudRenderer.resize(width, height);
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(multiplexer);
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

    WorldManager.state = null;
  }
}
