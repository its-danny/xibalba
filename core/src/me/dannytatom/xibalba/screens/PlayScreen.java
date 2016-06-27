package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.dannytatom.xibalba.ActionLog;
import me.dannytatom.xibalba.HudRenderer;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.PlayerInput;
import me.dannytatom.xibalba.WorldRenderer;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;

public class PlayScreen implements Screen {
  private final Main main;

  private final FPSLogger fps;
  private final OrthographicCamera worldCamera;
  private final WorldRenderer worldRenderer;
  private final HudRenderer hudRenderer;

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

    // Setup action log
    main.log = new ActionLog();

    // Add player entity
    PositionComponent playerPosition = main.player.getComponent(PositionComponent.class);
    playerPosition.pos = main.mapHelpers.getRandomOpenPosition();

    // Setup renderers
    worldCamera = new OrthographicCamera();
    worldRenderer = new WorldRenderer(main, worldCamera, batch);
    hudRenderer = new HudRenderer(main, batch);

    // Change state to playing
    main.state = Main.State.PLAYING;
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

    // When moving w/ mouse, execute a turn every tenth of a second
    if (main.state == Main.State.MOVING && autoTimer >= .10) {
      autoTimer = 0;
      main.executeTurn = true;
    }

    // Update engine if it's time to execute a turn
    if (main.executeTurn) {
      main.engine.update(delta);
      main.executeTurn = false;
    }

    if (main.player.getComponent(AttributesComponent.class).health <= 0) {
      main.currentMapIndex = 0;

      main.getScreen().dispose();
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
    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(new PlayerInput(main, worldCamera));
    multiplexer.addProcessor(hudRenderer.stage);

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

    main.log = null;
    main.state = null;
  }
}
