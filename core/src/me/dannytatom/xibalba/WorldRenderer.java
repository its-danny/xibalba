package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class WorldRenderer {
  private static final int SPRITE_WIDTH = 24;
  private static final int SPRITE_HEIGHT = 24;

  private final Engine engine;
  private final SpriteBatch batch;
  private final Map map;
  private final Entity player;
  private final OrthographicCamera camera;

  /**
   * WorldRenderer constructor.
   *
   * @param engine Ashely engine
   * @param batch  The sprite batch to use (set in PlayScreen)
   * @param map    The map we're on
   */
  public WorldRenderer(Engine engine, SpriteBatch batch, Map map, Entity player) {
    this.engine = engine;
    this.batch = batch;
    this.map = map;
    this.player = player;

    camera = new OrthographicCamera((Gdx.graphics.getWidth() / 4) * 3, Gdx.graphics.getHeight());
    camera.update();
  }

  /**
   * Render shit.
   */
  public void render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glViewport(0, 0, (Gdx.graphics.getWidth() / 4) * 3, Gdx.graphics.getHeight());

    // Get player pos for worldCamera
    PositionComponent playerPosition = player.getComponent(PositionComponent.class);

    // Update worldCamera
    camera.position.set(playerPosition.pos.x * SPRITE_WIDTH,
        playerPosition.pos.y * SPRITE_HEIGHT, 0);
    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        Cell cell = map.getCell(x, y);

        batch.draw(cell.sprite, x * SPRITE_WIDTH, y * SPRITE_HEIGHT);
      }
    }

    ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(PositionComponent.class,
        VisualComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);
      VisualComponent visual = ComponentMappers.visual.get(entity);

      batch.draw(visual.sprite, position.pos.x * SPRITE_WIDTH,
          (position.pos.y * SPRITE_HEIGHT) + (SPRITE_HEIGHT / 2));
    }

    batch.end();
  }
}
