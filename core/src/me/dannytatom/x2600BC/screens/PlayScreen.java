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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.dannytatom.x2600BC.Constants;
import me.dannytatom.x2600BC.Main;
import me.dannytatom.x2600BC.Mappers;
import me.dannytatom.x2600BC.components.PositionComponent;
import me.dannytatom.x2600BC.components.VisualComponent;
import me.dannytatom.x2600BC.generators.CaveGenerator;
import me.dannytatom.x2600BC.systems.MovementSystem;

import java.util.Map;

public class PlayScreen implements Screen, InputProcessor {
    static int SPRITE_WIDTH = 24;
    static int SPRITE_HEIGHT = 24;
    final Main game;
    OrthographicCamera camera;
    SpriteBatch batch;
    Engine engine;
    Entity player;
    CaveGenerator cave;
    int[][] map;

    public PlayScreen(final Main game) {
        this.game = game;

        this.batch = new SpriteBatch();

        // Generate cave & find player starting position
        this.cave = new CaveGenerator(40, 30);
        this.map = cave.generate();
        Map<String, Integer> startingPosition = cave.findPlayerStart();

        // Setup engine
        this.engine = new Engine();
        engine.addSystem(new MovementSystem(map));

        // Setup camera
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        // Setup input
        Gdx.input.setInputProcessor(this);

        // Add player entity
        this.player = new Entity();
        player.add(new PositionComponent(startingPosition.get("x"), startingPosition.get("y")));
        player.add(new VisualComponent(game.assets, "sprites/player.png"));
        engine.addEntity(player);

        // Create some mobs
//        for (int i = 0; i < 3; i++) {
//            Map<String, Integer> pos = cave.findMobStart();
//            Entity mob = new Entity();
//
//            mob.add(new PositionComponent(pos.get("x"), pos.get("y")));
//            mob.add(new VisualComponent(game.assets, "sprites/spider.png"));
//
//            engine.addEntity(mob);
//        }
    }

    @Override
    public void render(float delta) {
        // Get all entities with a position & visual component
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(PositionComponent.class, VisualComponent.class).get());
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update Ashley engine
        engine.update(delta);

        // Update camera
        camera.position.set(playerPosition.x * SPRITE_WIDTH, playerPosition.y * SPRITE_HEIGHT, 0);
        camera.update();

        // Draw shit
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        // Draw map
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                Texture texture = null;

                switch (map[x][y]) {
                    case Constants.EMPTINESS:
                        break;
                    case Constants.GROUND:
                        texture = game.assets.get("sprites/ground.png");
                        break;
                    case Constants.WALL_TOP:
                        texture = game.assets.get("sprites/wall_top.png");
                        break;
                    case Constants.WALL_FRONT:
                        texture = game.assets.get("sprites/wall_front.png");
                        break;
                }

                if (texture != null) {
                    batch.draw(texture, x * SPRITE_WIDTH, y * SPRITE_HEIGHT);
                }
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

        switch (keyCode) {
            case Input.Keys.UP:
                position.moveN = true;
                break;
            case Input.Keys.RIGHT:
                position.moveE = true;
                break;
            case Input.Keys.DOWN:
                position.moveS = true;
                break;
            case Input.Keys.LEFT:
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
