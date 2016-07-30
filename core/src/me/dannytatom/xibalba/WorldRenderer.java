package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.EntranceComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.apache.commons.lang3.ArrayUtils;
import org.xguzm.pathfinding.grid.GridCell;

public class WorldRenderer {
  private final SpriteBatch batch;
  private final Viewport viewport;
  private final OrthographicCamera worldCamera;

  private final PlayerComponent playerDetails;

  // These get reused a ton
  private final Sprite target;
  private final Sprite targetThrow;
  private final Sprite targetPath;

  /**
   * Setup world renderer.
   *
   * @param worldCamera Instance of camera
   * @param batch       Instance of sprite batch
   */
  public WorldRenderer(OrthographicCamera worldCamera, SpriteBatch batch) {
    this.worldCamera = worldCamera;
    this.batch = batch;

    viewport = new FitViewport(960, 540, worldCamera);

    playerDetails = ComponentMappers.player.get(WorldManager.player);

    target = Main.atlas.createSprite("Level/Cave/UI/Target-1");
    targetThrow = Main.atlas.createSprite("Level/Cave/UI/Target-Throw-1");
    targetPath = Main.atlas.createSprite("Level/Cave/UI/Target-Path-1");
  }

  /**
   * Render shit.
   */
  public void render() {
    // Get playerDetails position
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

    // Set worldCamera to follow playerDetails
    worldCamera.position.set(
        playerPosition.pos.x * Main.SPRITE_WIDTH,
        playerPosition.pos.y * Main.SPRITE_HEIGHT, 0
    );

    worldCamera.update();

    batch.setProjectionMatrix(worldCamera.combined);
    batch.begin();

    Map map = WorldManager.world.getCurrentMap();

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        Cell cell = WorldManager.mapHelpers.getCell(x, y);

        if (map.lightMap[x][y] > 0) {
          cell.hidden = false;
        }

        if (!cell.hidden) {
          cell.forgotten = map.lightMap[x][y] <= 0;
          cell.sprite.draw(batch);
        }
      }
    }

    if (playerDetails.path != null && playerDetails.target != null) {
      for (int i = 0; i < playerDetails.path.size(); i++) {
        boolean isLast = i == (playerDetails.path.size() - 1);

        Sprite targetSprite;

        if (WorldManager.state == WorldManager.State.TARGETING) {
          targetSprite = targetThrow;
        } else {
          targetSprite = target;
        }

        GridCell cell = playerDetails.path.get(i);

        batch.setColor(1f, 1f, 1f, isLast ? 1f : 0.5f);
        batch.draw(
            isLast ? targetSprite : targetPath,
            cell.x * Main.SPRITE_WIDTH, cell.y * Main.SPRITE_HEIGHT
        );
        batch.setColor(1f, 1f, 1f, 1f);
      }
    }

    renderStairs();
    renderDecorations();
    renderItems();
    renderEnemies();
    renderPlayer();

    batch.end();
  }

  private void renderStairs() {
    ImmutableArray<Entity> entrances =
        WorldManager.engine.getEntitiesFor(Family.all(EntranceComponent.class).get());
    ImmutableArray<Entity> exits =
        WorldManager.engine.getEntitiesFor(Family.all(ExitComponent.class).get());

    Object[] entities = ArrayUtils.addAll(entrances.toArray(), exits.toArray());

    for (Object e : entities) {
      Entity entity = (Entity) e;

      if (WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      }
    }
  }

  private void renderDecorations() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(DecorationComponent.class).get());

    for (Entity entity : entities) {
      if (WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      }
    }
  }

  private void renderItems() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(
            Family.all(ItemComponent.class, PositionComponent.class, VisualComponent.class).get()
        );

    for (Entity entity : entities) {
      if (WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      }
    }
  }

  private void renderEnemies() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      if (WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      }
    }
  }

  private void renderPlayer() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

    ComponentMappers.visual.get(entities.first()).sprite.draw(batch);
  }

  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }
}
