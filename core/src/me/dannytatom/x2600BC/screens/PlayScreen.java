package me.dannytatom.x2600BC.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import me.dannytatom.x2600BC.Main;
import me.dannytatom.x2600BC.Mappers;
import me.dannytatom.x2600BC.components.AttributesComponent;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.components.VisualComponent;
import me.dannytatom.x2600BC.generators.CaveGenerator;
import me.dannytatom.x2600BC.systems.MovementSystem;

import java.util.Map;

public class PlayScreen implements Screen, InputProcessor {
  static final int SPRITE_WIDTH = 24;
  static final int SPRITE_HEIGHT = 24;
  static final float AMBIENT_INTENSITY = .7f;
  static final Vector3 AMBIENT_COLOR = new Vector3(0.3f, 0.3f, 0.7f);

  final Main game;
  OrthographicCamera camera;
  SpriteBatch batch;
  Engine engine;
  Entity player;
  CaveGenerator cave;
  ShaderProgram defaultShader;
  ShaderProgram lightShader;
  FrameBuffer buffer;
  Texture light;

  /**
   * Play Screen.
   *
   * @param g Instance of Main class
   */
  public PlayScreen(Main g) {
    game = g;

    // Setup input
    Gdx.input.setInputProcessor(this);

    ShaderProgram.pedantic = false;
    defaultShader = new ShaderProgram(new FileHandle("vertexShader.glsl").readString(),
        new FileHandle("defaultShader.glsl").readString());
    lightShader = new ShaderProgram(new FileHandle("vertexShader.glsl").readString(),
        new FileHandle("lightShader.glsl").readString());

    lightShader.begin();
    lightShader.setUniformf("ambientColor",
        AMBIENT_COLOR.x, AMBIENT_COLOR.y,
        AMBIENT_COLOR.z, AMBIENT_INTENSITY);

    lightShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    lightShader.setUniformi("u_lightmap", 1);
    lightShader.end();

    buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(),
        Gdx.graphics.getHeight(), false);

    light = game.assets.get("sprites/light.png");

    batch = new SpriteBatch();

    // Generate cave
    cave = new CaveGenerator(game.assets.get("sprites/cave.atlas"), 40, 30);

    // Setup engine
    engine = new Engine();
    engine.addSystem(new MovementSystem(cave.geometry));

    // Setup camera
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.update();

    // Add player entity
    Map<String, Integer> startingPosition = cave.findPlayerStart();
    player = new Entity();
    player.add(new PositionComponent(startingPosition.get("x"), startingPosition.get("y")));
    player.add(new VisualComponent(game.assets.get("sprites/player.png")));
    player.add(new AttributesComponent(100));
    engine.addEntity(player);

    spawnMobs();
  }

  @Override
  public void render(float delta) {
    PositionComponent playerPosition = player.getComponent(PositionComponent.class);

    // Get all entities with a position & visual component
    ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(AttributesComponent.class,
        PositionComponent.class, VisualComponent.class).get());

    // Clear screen
    Gdx.gl.glClearColor(0, 0, 0, 1);

    // Don't update any entities until the player has
    // an action to take
    if (!player.getComponent(AttributesComponent.class).actions.isEmpty()) {
      // Update Ashley engine
      engine.update(delta);

      // Give energy back
      //
      // elapsed = player action cost * 100 / player speed
      for (Entity entity : entities) {
        AttributesComponent attributes = Mappers.attributes.get(entity);
        player.getComponent(AttributesComponent.class).speed = 100;
        int playerSpeed = player.getComponent(AttributesComponent.class).speed;
        int elapsed = ((100 - playerSpeed) * 100) / playerSpeed;

        attributes.speed += (attributes.speed * elapsed) / 100;
      }
    }

    // Update camera
    camera.position.set(playerPosition.x * SPRITE_WIDTH, playerPosition.y * SPRITE_HEIGHT, 0);
    camera.update();

    renderLight();
    renderMap();
  }

  /**
   * Render torch light around player.
   */
  public void renderLight() {
    buffer.begin();
    batch.setProjectionMatrix(camera.combined);
    batch.setShader(defaultShader);

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    PositionComponent playerPosition = player.getComponent(PositionComponent.class);

    batch.begin();
    batch.draw(light,
        (playerPosition.x * SPRITE_WIDTH) - (6 * SPRITE_WIDTH) + (SPRITE_WIDTH / 2),
        (playerPosition.y * SPRITE_HEIGHT) - (6 * SPRITE_HEIGHT) + (SPRITE_HEIGHT / 2),
        12 * SPRITE_WIDTH, 12 * SPRITE_HEIGHT);
    batch.end();

    buffer.end();
  }

  /**
   * Render the map and all entities on it.
   */
  public void renderMap() {
    batch.setProjectionMatrix(camera.combined);

    if (game.lightEnabled) {
      batch.setShader(lightShader);
    }

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    // Idk what this does, but it's needed?
    buffer.getColorBufferTexture().bind(1);
    light.bind(0);

    // Draw map
    for (int x = 0; x < cave.map.length; x++) {
      for (int y = 0; y < cave.map[x].length; y++) {
        batch.draw(cave.map[x][y], x * SPRITE_WIDTH, y * SPRITE_HEIGHT);
      }
    }

    // Iterate entities with a Position & Visual component
    // and draw them
    ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(AttributesComponent.class,
        PositionComponent.class, VisualComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = Mappers.position.get(entity);
      VisualComponent visual = Mappers.visual.get(entity);

      batch.draw(visual.sprite, position.x * SPRITE_WIDTH, position.y * SPRITE_HEIGHT);
    }

    batch.end();
  }

  /**
   * Spawn mobs, randomly for now.
   */
  public void spawnMobs() {
    for (int i = 0; i < 5; i++) {
      Map<String, Integer> pos = cave.findMobStart();
      Entity mob = new Entity();

      mob.add(new PositionComponent(pos.get("x"), pos.get("y")));
      mob.add(new VisualComponent(game.assets.get("sprites/spider.png")));
      mob.add(new AttributesComponent(100));

      engine.addEntity(mob);
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
    buffer.dispose();
    defaultShader.dispose();
    lightShader.dispose();
    light.dispose();
  }

  @Override
  public boolean keyDown(int keyCode) {
    PositionComponent position = player.getComponent(PositionComponent.class);
    AttributesComponent attributes = player.getComponent(AttributesComponent.class);

    switch (keyCode) {
      case Input.Keys.BACKSLASH:
        game.lightEnabled ^= true;
        break;
      case Input.Keys.K:
        attributes.actions.add("move");
        position.moveDir = "N";
        break;
      case Input.Keys.U:
        attributes.actions.add("move");
        position.moveDir = "NE";
        break;
      case Input.Keys.L:
        attributes.actions.add("move");
        position.moveDir = "E";
        break;
      case Input.Keys.N:
        attributes.actions.add("move");
        position.moveDir = "SE";
        break;
      case Input.Keys.J:
        attributes.actions.add("move");
        position.moveDir = "S";
        break;
      case Input.Keys.B:
        attributes.actions.add("move");
        position.moveDir = "SW";
        break;
      case Input.Keys.H:
        attributes.actions.add("move");
        position.moveDir = "W";
        break;
      case Input.Keys.Y:
        attributes.actions.add("move");
        position.moveDir = "NW";
        break;
      default:
    }

    return true;
  }

  @Override
  public boolean keyUp(int keyCode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
