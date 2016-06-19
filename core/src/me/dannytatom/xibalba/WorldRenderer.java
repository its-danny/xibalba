package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.effects.DamageEffectComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.map.ShadowCaster;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;

public class WorldRenderer {
  private static final int SPRITE_WIDTH = 8;
  private static final int SPRITE_HEIGHT = 8;

  private final Main main;
  private final Engine engine;
  private final SpriteBatch batch;
  private final Map map;
  private final ShadowCaster caster;
  private Viewport viewport;
  private OrthographicCamera camera;

  /**
   * Renders the game world.
   *
   * @param engine Ashely engine
   * @param batch  The sprite batch to use (set in PlayScreen)
   * @param map    The map we're on
   */
  public WorldRenderer(Main main, Engine engine, SpriteBatch batch, Map map) {
    this.main = main;
    this.engine = engine;
    this.batch = batch;
    this.map = map;

    camera = new OrthographicCamera();
    viewport = new FitViewport(960 / 2, 540 / 2, camera);

    caster = new ShadowCaster();
  }

  /**
   * Render shit.
   *
   * @param delta Elapsed time
   */
  public void render(float delta) {
    // Get player position
    PositionComponent playerPosition = main.player.getComponent(PositionComponent.class);

    // Set camera to follow player
    camera.position.set(
        playerPosition.pos.x * SPRITE_WIDTH,
        playerPosition.pos.y * SPRITE_HEIGHT, 0
    );

    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    AttributesComponent playerAttributes = main.player.getComponent(AttributesComponent.class);

    float[][] lightMap = caster.calculateFov(
        map.createFovMap(),
        (int) playerPosition.pos.x, (int) playerPosition.pos.y,
        playerAttributes.vision
    );

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        Cell cell = map.getCell(x, y);

        if (lightMap[x][y] > 0) {
          cell.hidden = false;
        }

        if (!cell.hidden) {
          batch.setColor(1f, 1f, 1f, lightMap[x][y] <= 0.25f ? 0.25f : lightMap[x][y]);
          batch.draw(cell.sprite, x * SPRITE_WIDTH, y * SPRITE_HEIGHT);
          batch.setColor(1f, 1f, 1f, 1f);
        }
      }
    }

    if (map.targetingPath != null) {
      for (GridCell cell : map.targetingPath) {
        TextureAtlas atlas = main.assets.get("sprites/main.atlas");

        batch.setColor(1f, 1f, 1f,
            lightMap[cell.x][cell.y] <= 0.35f ? 0.35f : lightMap[cell.x][cell.y]);
        batch.draw(
            atlas.createSprite("Universal/UI/Target/UI-Target-1"), cell.x * SPRITE_WIDTH, cell.y * SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }

    renderEffects(lightMap);
    renderItems(lightMap);
    renderPlayer(delta, lightMap);
    renderEnemies(delta, lightMap);

    batch.end();
  }

  private void renderEffects(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        engine.getEntitiesFor(Family.all(DamageEffectComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);
      VisualComponent visual = ComponentMappers.visual.get(entity);

      if (map.getCell(position.pos) != null && !map.getCell(position.pos).hidden) {
        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderItems(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        engine.getEntitiesFor(
            Family.all(ItemComponent.class, PositionComponent.class, VisualComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (!map.getCell(position.pos).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderPlayer(float delta, float[][] lightMap) {
    ImmutableArray<Entity> entities =
        engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (!map.getCell(position.pos).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        visual.elapsedTime += delta;

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderEnemies(float delta, float[][] lightMap) {
    ImmutableArray<Entity> entities =
        engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (main.entityHelpers.isVisible(entity, map)) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        visual.elapsedTime += delta;

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  public void resize(int width, int height) {
    viewport.update(width, height);
  }
}
