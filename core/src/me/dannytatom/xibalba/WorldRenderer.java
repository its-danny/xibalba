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
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.map.ShadowCaster;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;

public class WorldRenderer {
  private final Main main;
  private final SpriteBatch batch;
  private final ShadowCaster caster;
  private final Viewport viewport;
  private final OrthographicCamera worldCamera;

  /**
   * Renders the game world.
   *
   * @param main  Instance of Main class
   * @param batch The sprite batch to use (set in PlayScreen)
   */
  public WorldRenderer(Main main, OrthographicCamera worldCamera, SpriteBatch batch) {
    this.main = main;
    this.worldCamera = worldCamera;
    this.batch = batch;

    viewport = new FitViewport(960 / 2, 540 / 2, worldCamera);

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

    // Set worldCamera to follow player
    worldCamera.position.set(
        playerPosition.pos.x * Main.SPRITE_WIDTH,
        playerPosition.pos.y * Main.SPRITE_HEIGHT, 0
    );

    worldCamera.update();

    batch.setProjectionMatrix(worldCamera.combined);
    batch.begin();

    AttributesComponent playerAttributes = main.player.getComponent(AttributesComponent.class);

    Map map = main.getMap();

    float[][] lightMap = caster.calculateFov(
        main.mapHelpers.createFovMap(),
        (int) playerPosition.pos.x, (int) playerPosition.pos.y,
        playerAttributes.vision
    );

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        Cell cell = main.mapHelpers.getCell(x, y);

        if (lightMap[x][y] > 0) {
          cell.hidden = false;
        }

        if (!cell.hidden) {
          cell.forgotten = lightMap[x][y] <= 0;

          batch.setColor(1f, 1f, 1f, lightMap[x][y] <= 0.15f ? 0.15f : lightMap[x][y]);
          batch.draw(cell.sprite, x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);
          batch.setColor(1f, 1f, 1f, 1f);
        }
      }
    }

    TextureAtlas atlas = main.assets.get("sprites/main.atlas");

    if (map.targetingPath != null && map.target != null) {
      for (int i = 0; i < map.targetingPath.size(); i++) {
        GridCell cell = map.targetingPath.get(i);
        boolean isLast = i == (map.targetingPath.size() - 1);

        batch.setColor(1f, 1f, 1f, isLast ? 1f : 0.25f);
        batch.draw(
            atlas.createSprite("Level/Cave/UI/Target-1"),
            cell.x * Main.SPRITE_WIDTH, cell.y * Main.SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    } else if (map.lookingPath != null && map.target != null) {
      for (int i = 0; i < map.lookingPath.size(); i++) {
        GridCell cell = map.lookingPath.get(i);
        boolean isLast = i == (map.lookingPath.size() - 1);

        batch.setColor(1f, 1f, 1f, isLast ? 1f : 0.25f);
        batch.draw(
            atlas.createSprite("Level/Cave/UI/Target-1"),
            cell.x * Main.SPRITE_WIDTH, cell.y * Main.SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }

    renderDecorations(lightMap);
    renderItems(lightMap);
    renderPlayer(lightMap);
    renderEnemies(lightMap);
    renderStairs(lightMap);

    batch.end();
  }

  private void renderDecorations(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(DecorationComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);
      Map map = main.getMap();

      if (main.mapHelpers.cellExists(position.pos) && !main.mapHelpers.getCell(position.pos.x, position.pos.y).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * Main.SPRITE_WIDTH, position.pos.y * Main.SPRITE_HEIGHT
        );
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

      if (!main.mapHelpers.getCell(position.pos.x, position.pos.y).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * Main.SPRITE_WIDTH, position.pos.y * Main.SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderPlayer(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (!main.mapHelpers.getCell(position.pos.x, position.pos.y).hidden) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite,
            position.pos.x * Main.SPRITE_WIDTH,
            position.pos.y * Main.SPRITE_HEIGHT + (Main.SPRITE_HEIGHT / 4)
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderEnemies(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (main.entityHelpers.isVisible(entity, main.getMap())) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite,
            position.pos.x * Main.SPRITE_WIDTH,
            position.pos.y * Main.SPRITE_HEIGHT + (Main.SPRITE_HEIGHT / 4)
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  private void renderStairs(float[][] lightMap) {
    ImmutableArray<Entity> entities =
        main.engine.getEntitiesFor(Family.all(ExitComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (main.entityHelpers.isVisible(entity, main.getMap())) {
        VisualComponent visual = ComponentMappers.visual.get(entity);

        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);
        batch.draw(
            visual.sprite, position.pos.x * Main.SPRITE_WIDTH, position.pos.y * Main.SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }
  }

  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }
}
