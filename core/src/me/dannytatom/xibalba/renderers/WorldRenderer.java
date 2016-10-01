package me.dannytatom.xibalba.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.EntranceComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.TrapComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.Map;
import me.dannytatom.xibalba.world.MapCell;
import me.dannytatom.xibalba.world.WorldManager;
import org.apache.commons.lang3.ArrayUtils;
import org.xguzm.pathfinding.grid.GridCell;

public class WorldRenderer {
  private final SpriteBatch batch;
  private final Viewport viewport;
  private final OrthographicCamera worldCamera;

  private final PlayerComponent playerDetails;

  // These get reused a ton
  private final Sprite shadow;

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
    shadow = Main.asciiAtlas.createSprite("1113");
  }

  /**
   * Render shit.
   */
  public void render(float delta) {
    // Get playerDetails position
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

    // Update lighting
    WorldManager.world.updateLighting(playerPosition.pos.x, playerPosition.pos.y);

    // Handle screen shake
    if (Main.cameraShake.time > 0) {
      Main.cameraShake.update(delta, worldCamera, playerPosition.pos);
    } else {
      // Set worldCamera to follow player
      worldCamera.position.set(
          playerPosition.pos.x * Main.SPRITE_WIDTH,
          playerPosition.pos.y * Main.SPRITE_HEIGHT, 0
      );
    }

    worldCamera.update();

    batch.setProjectionMatrix(worldCamera.combined);
    batch.begin();

    Map map = WorldManager.world.getCurrentMap();

    for (int x = 0; x < map.width - 1; x++) {
      for (int y = 0; y < map.height - 1; y++) {
        MapCell cell = WorldManager.mapHelpers.getCell(x, y);

        if (map.lightMap[x][y] > 0) {
          cell.hidden = false;

          if (cell.tween != null && !cell.tween.isStarted()) {
            cell.tween.start(Main.tweenManager);
          }
        }

        if (!cell.hidden) {
          cell.forgotten = map.lightMap[x][y] <= 0;

          if (WorldManager.mapHelpers.getEntitiesAt(new Vector2(x, y)).size() == 0) {
            cell.sprite.draw(batch);
          }
        }
      }
    }

    renderStairs();
    renderDecorations();
    renderTraps();
    renderItems();
    renderEnemies();
    renderPlayer();
    renderShadows();
    renderHighlights();

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

  private void renderTraps() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(TrapComponent.class).get());

    for (Entity entity : entities) {
      if (ComponentMappers.visual.has(entity) && WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      }
    }
  }

  private void renderItems() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(
            Family.all(ItemComponent.class).get()
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
      if (Main.tweenManager.getRunningTimelinesCount() == 0) {
        PositionComponent position = ComponentMappers.position.get(entity);
        WorldManager.entityHelpers.updateSpritePosition(entity, position.pos);
      }

      if (WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      }
    }
  }

  private void renderPlayer() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

    if (entities.size() > 0) {
      Entity player = entities.first();

      if (Main.tweenManager.getRunningTimelinesCount() == 0) {
        PositionComponent position = ComponentMappers.position.get(player);
        WorldManager.entityHelpers.updateSpritePosition(player, position.pos);
      }

      ComponentMappers.visual.get(player).sprite.draw(batch);
    }
  }

  private void renderShadows() {
    Map map = WorldManager.world.getCurrentMap();

    for (int x = 0; x < map.lightMap.length; x++) {
      for (int y = 0; y < map.lightMap[0].length; y++) {
        float minimum;

        switch (WorldManager.world.getCurrentMap().time.time) {
          case DAWN:
            minimum = .15f;
            break;
          case DAY:
            minimum = .20f;
            break;
          case DUSK:
            minimum = .15f;
            break;
          case NIGHT:
            minimum = .10f;
            break;
          default:
            minimum = .10f;
            break;
        }

        float alpha = map.lightMap[x][y] <= minimum ? minimum : map.lightMap[x][y];

        shadow.setColor(Colors.get(WorldManager.world.getCurrentMap().type + "Background"));
        shadow.setAlpha(-alpha);
        shadow.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);

        shadow.draw(batch);
      }
    }
  }

  private void renderHighlights() {
    if (playerDetails.path != null && playerDetails.target != null) {
      for (int i = 0; i < playerDetails.path.size(); i++) {
        GridCell cell = playerDetails.path.get(i);

        shadow.setColor(Color.WHITE);
        shadow.setAlpha(.15f);
        shadow.setPosition(cell.x * Main.SPRITE_WIDTH, cell.y * Main.SPRITE_HEIGHT);

        shadow.draw(batch);
      }
    }
  }

  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }
}
