package me.dannytatom.x2600BC.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    static int SPRITE_WIDTH = 24;
    static int SPRITE_HEIGHT = 24;
    final Main game;
    OrthographicCamera camera;
    SpriteBatch batch;
    Engine engine;
    Entity player;
    CaveGenerator cave;
    Queue<Entity> queue;

    public PlayScreen(final Main game) {
        this.game = game;

        this.batch = new SpriteBatch();
        this.queue = new LinkedList<>();

        // Generate cave & find player starting position
        this.cave = new CaveGenerator(game.assets.get("sprites/cave.atlas"), 40, 30);
        Map<String, Integer> startingPosition = cave.findPlayerStart();

        // Setup engine
        this.engine = new Engine();
        engine.addSystem(new MovementSystem(cave.geometry));

        // Setup camera
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        // Setup input
        Gdx.input.setInputProcessor(this);

        // Add player entity
        this.player = new Entity();
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        // Draw shit
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

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
