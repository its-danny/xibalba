package me.dannytatom.xibalba.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.PlayerInput;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.renderers.HudRenderer;
import me.dannytatom.xibalba.renderers.WorldRenderer;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class PlayScreen implements Screen {
  private final WorldRenderer worldRenderer;
  private final HudRenderer hudRenderer;
  private final PlayerInput playerInput;
  private final InputMultiplexer multiplexer;

  private final SpriteBatch batch;
  private final AttributesComponent playerAttributes;
  private float autoTimer;
  private float keyHoldTimerDelay;
  private float keyHoldTimer;

  /**
   * Play Screen.
   *
   * @param main Instance of Main class
   */
  public PlayScreen(Main main) {
    autoTimer = 0;
    keyHoldTimerDelay = 0;
    keyHoldTimer = 0;
    batch = new SpriteBatch();

    // Setup camera;
    OrthographicCamera worldCamera = new OrthographicCamera();

    // Setup renderers
    worldRenderer = new WorldRenderer(worldCamera, batch);
    hudRenderer = new HudRenderer(main, batch);

    // Setup input
    multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(hudRenderer.stage);
    playerInput = new PlayerInput(worldCamera);
    multiplexer.addProcessor(playerInput);

    // Player attributes
    playerAttributes = ComponentMappers.attributes.get(WorldManager.player);

    // Change state to playing
    WorldManager.state = WorldManager.State.PLAYING;
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(
        Colors.get(WorldManager.world.getCurrentMap().type + "Background").r,
        Colors.get(WorldManager.world.getCurrentMap().type + "Background").g,
        Colors.get(WorldManager.world.getCurrentMap().type + "Background").b,
        Colors.get(WorldManager.world.getCurrentMap().type + "Background").a
    );

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    autoTimer += delta;
    keyHoldTimer += delta;

    // Weather!
    if (WorldManager.world.getCurrentMap().weather != null) {
      WorldManager.world.getCurrentMap().weather.update(delta);
    }

    // Keep moving if a key is held down
    if (playerInput.keyHeld != -1) {
      keyHoldTimerDelay += delta;

      if (keyHoldTimerDelay >= .5f) {
        if (keyHoldTimer >= .10f) {
          keyHoldTimer = 0;
          playerInput.keyDown(playerInput.keyHeld);
        }
      }
    } else {
      keyHoldTimerDelay = 0;
    }

    // In some cases, we want the game to take turns on it's own
    if ((WorldManager.state == WorldManager.State.MOVING
        || WorldManager.state == WorldManager.State.DEAD
        || WorldManager.entityHelpers.shouldSkipTurn(WorldManager.player))
        && autoTimer >= .10f) {
      autoTimer = 0;
      WorldManager.executeTurn = true;
    }

    // Update engine if it's time to execute a turn
    if (WorldManager.executeTurn) {
      WorldManager.turnCount += 1;

      WorldManager.engine.update(delta);
      WorldManager.world.getCurrentMap().time.update();
      WorldManager.executeTurn = false;
    }

    // Start tweens
    if (WorldManager.tweens.size > 0) {
      WorldManager.state = WorldManager.State.WAITING;
      Timeline timeline = Timeline.createParallel();

      for (int i = 0; i < WorldManager.tweens.size; i++) {
        timeline.push(WorldManager.tweens.get(i));
        WorldManager.tweens.removeIndex(i);
      }

      timeline.setCallback(
          (type, source) -> {
            if (type == TweenCallback.COMPLETE) {
              WorldManager.state = WorldManager.State.PLAYING;
            }
          }
      ).start(Main.tweenManager);
    }

    // Check if going up or down levels
    if (WorldManager.state == WorldManager.State.GOING_DOWN) {
      WorldManager.world.goDown();
    } else if (WorldManager.state == WorldManager.State.GOING_UP) {
      WorldManager.world.goUp();
    } else {
      Main.tweenManager.update(delta);

      worldRenderer.render(delta);
      hudRenderer.render(delta);
    }

    // Check player health for DEATH
    if (playerAttributes.health <= 0) {
      WorldManager.state = WorldManager.State.DEAD;
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
  }
}
