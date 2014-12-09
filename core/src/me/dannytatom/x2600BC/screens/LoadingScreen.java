package me.dannytatom.x2600BC.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.dannytatom.x2600BC.Main;

public class LoadingScreen implements Screen {
    final Main game;

    SpriteBatch batch;
    BitmapFont font;

    public LoadingScreen(final Main game) {
        this.game = game;

        this.font = new BitmapFont();
        this.batch = new SpriteBatch();

        game.assets.load("sprites/ground.png", Texture.class);
        game.assets.load("sprites/cave_floor.png", Texture.class);
        game.assets.load("sprites/wall_front.png", Texture.class);
        game.assets.load("sprites/wall_top.png", Texture.class);
        game.assets.load("sprites/player.png", Texture.class);
        game.assets.load("sprites/spider.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        batch.begin();
        font.draw(batch, "Loading...", Gdx.graphics.getWidth() / 2 - 24, Gdx.graphics.getHeight() / 2);
        batch.end();

        if (game.assets.update()) {
            game.setScreen(new PlayScreen(game));
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
        font.dispose();
    }
}
