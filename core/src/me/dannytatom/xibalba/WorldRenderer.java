package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.ShadowCaster;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;

public class WorldRenderer {
  private static final int SPRITE_WIDTH = 16;
  private static final int SPRITE_HEIGHT = 16;

  private final Main main;
  private final SpriteBatch batch;
  private final ShadowCaster caster;
  private final Viewport viewport;
  private final OrthographicCamera camera;

  /**
   * Renders the game world.
   *
   * @param batch The sprite batch to use (set in PlayScreen)
   */
  public WorldRenderer(Main main, SpriteBatch batch) {
    this.main = main;
    this.batch = batch;

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
        main.getCurrentMap().createFovMap(),
        (int) playerPosition.pos.x, (int) playerPosition.pos.y,
        playerAttributes.vision
    );

    for (int x = 0; x < main.getCurrentMap().width; x++) {
      for (int y = 0; y < main.getCurrentMap().height; y++) {
        Cell cell = main.getCurrentMap().getCell(x, y);

        if (lightMap[x][y] > 0) {
          cell.hidden = false;
        }

        if (!cell.hidden) {
          cell.forgotten = lightMap[x][y] <= 0;

          batch.setColor(1f, 1f, 1f, lightMap[x][y] <= 0.15f ? 0.15f : lightMap[x][y]);
          batch.draw(cell.sprite, x * SPRITE_WIDTH, y * SPRITE_HEIGHT);
          batch.setColor(1f, 1f, 1f, 1f);
        }
      }
    }

    if (main.getCurrentMap().targetingPath != null) {
      for (GridCell cell : main.getCurrentMap().targetingPath) {
        TextureAtlas atlas = main.assets.get("sprites/main.atlas");

        batch.setColor(1f, 1f, 1f,
            lightMap[cell.x][cell.y] <= 0.5f ? 0.5f : lightMap[cell.x][cell.y]);
        batch.draw(
            atlas.createSprite("Level/Cave/UI/Target-1"),
            cell.x * SPRITE_WIDTH, cell.y * SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }

    if (main.getCurrentMap().searchingPath != null) {
      for (GridCell cell : main.getCurrentMap().searchingPath) {
        TextureAtlas atlas = main.assets.get("sprites/main.atlas");

        batch.setColor(1f, 1f, 1f,
            lightMap[cell.x][cell.y] <= 0.5f ? 0.5f : lightMap[cell.x][cell.y]);
        batch.draw(
            atlas.createSprite("Level/Cave/UI/Target-1"),
            cell.x * SPRITE_WIDTH, cell.y * SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }

    renderDecorations(lightMap);
    renderItems(lightMap);
    renderPlayer(delta, lightMap);
    renderEnemies(delta, lightMap);
    renderStairs(delta, lightMap);

    batch.end();
  }

  private void renderDecorations(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(DecorationComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (main.getCurrentMap().cellExists(position.pos) && !main.getCurrentMap().getCell(position.pos).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderItems(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(
            Family.all(ItemComponent.class, PositionComponent.class, VisualComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (!main.getCurrentMap().getCell(position.pos).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderPlayer(float delta, float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (!main.getCurrentMap().getCell(position.pos).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT + SPRITE_HEIGHT / 4
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderEnemies(float delta, float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (main.entityHelpers.isVisible(entity, main.getCurrentMap())) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT + SPRITE_HEIGHT / 4
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderStairs(float delta, float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(ExitComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (main.entityHelpers.isVisible(entity, main.getCurrentMap())) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT + SPRITE_HEIGHT / 4
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  public void resize(int width, int height) {
    viewport.update(width, height);
  }
}
