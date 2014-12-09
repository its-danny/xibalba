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

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class PlayScreen implements Screen, InputProcessor {
    static final int SPRITE_WIDTH = 24;
    static final int SPRITE_HEIGHT = 24;
    static final float ambientIntensity = .7f;
    static final Vector3 ambientColor = new Vector3(0.3f, 0.3f, 0.7f);

    final Main game;
    OrthographicCamera camera;
    SpriteBatch batch;
    Engine engine;
    Entity player;
    CaveGenerator cave;
    Queue<Entity> queue;
    ShaderProgram defaultShader;
    ShaderProgram lightShader;
    FrameBuffer buffer;
    Texture light;

    public PlayScreen(final Main game) {
        this.game = game;

        ShaderProgram.pedantic = false;
        defaultShader = new ShaderProgram(new FileHandle("vertexShader.glsl").readString(),
                new FileHandle("defaultShader.glsl").readString());
        lightShader = new ShaderProgram(new FileHandle("vertexShader.glsl").readString(),
                new FileHandle("lightShader.glsl").readString());

        lightShader.begin();
        lightShader.setUniformf("ambientColor", ambientColor.x, ambientColor.y, ambientColor.z, ambientIntensity);
        ;
        lightShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        lightShader.setUniformi("u_lightmap", 1);
        lightShader.end();

        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight(), false);

        light = game.assets.get("sprites/light.png");

        batch = new SpriteBatch();
        queue = new LinkedList<>();

        // Generate cave & find player starting position
        cave = new CaveGenerator(game.assets.get("sprites/cave.atlas"), 40, 30);
        Map<String, Integer> startingPosition = cave.findPlayerStart();

        // Setup engine
        engine = new Engine();
        engine.addSystem(new MovementSystem(cave.geometry));

        // Setup camera
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        // Setup input
        Gdx.input.setInputProcessor(this);

        // Add player entity
        player = new Entity();
        player.add(new PositionComponent(startingPosition.get("x"), startingPosition.get("y")));
        player.add(new VisualComponent(game.assets.get("sprites/player.png")));
        player.add(new AttributesComponent(100));
        engine.addEntity(player);

        // Create some mobs
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
    public void render(float delta) {
        // Get all entities with a position & visual component
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(AttributesComponent.class, PositionComponent.class, VisualComponent.class).get());
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);

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

        // Draw shader
        buffer.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.setShader(defaultShader);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(light,
                (playerPosition.x * SPRITE_WIDTH) - (5 * SPRITE_WIDTH) + (SPRITE_WIDTH / 2),
                (playerPosition.y * SPRITE_HEIGHT) - (5 * SPRITE_HEIGHT) + (SPRITE_HEIGHT / 2),
                10 * SPRITE_WIDTH, 10 * SPRITE_HEIGHT);
        batch.end();
        buffer.end();

        // Draw other shit
        batch.setProjectionMatrix(camera.combined);
        batch.setShader(lightShader);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
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
        for (Entity entity : entities) {
            PositionComponent position = Mappers.position.get(entity);
            VisualComponent visual = Mappers.visual.get(entity);

            batch.draw(visual.sprite, position.x * SPRITE_WIDTH, position.y * SPRITE_HEIGHT);
        }

        batch.end();
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
    }

    @Override
    public boolean keyDown(int keyCode) {
        PositionComponent position = player.getComponent(PositionComponent.class);
        AttributesComponent attributes = player.getComponent(AttributesComponent.class);

        switch (keyCode) {
            case Input.Keys.UP:
                attributes.actions.add("move");
                position.moveN = true;
                break;
            case Input.Keys.RIGHT:
                attributes.actions.add("move");
                position.moveE = true;
                break;
            case Input.Keys.DOWN:
                attributes.actions.add("move");
                position.moveS = true;
                break;
            case Input.Keys.LEFT:
                attributes.actions.add("move");
                position.moveW = true;
                break;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        PositionComponent position = player.getComponent(PositionComponent.class);

        switch (keyCode) {
            case Input.Keys.UP:
                position.moveN = false;
                break;
            case Input.Keys.RIGHT:
                position.moveE = false;
                break;
            case Input.Keys.DOWN:
                position.moveS = false;
                break;
            case Input.Keys.LEFT:
                position.moveW = false;
                break;
        }

        return true;
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
