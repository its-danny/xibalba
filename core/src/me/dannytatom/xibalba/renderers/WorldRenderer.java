package me.dannytatom.xibalba.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DecorationComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.EntranceComponent;
import me.dannytatom.xibalba.components.ExitComponent;
import me.dannytatom.xibalba.components.GodComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.TrapComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.GrayscaleShader;
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
  private final AttributesComponent playerAttributes;
  private final PositionComponent playerPosition;
  private final GodComponent god;

  // These get reused a ton
  private final Sprite shadow;
  private final Sprite question;

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
    playerAttributes = ComponentMappers.attributes.get(WorldManager.player);
    playerPosition = ComponentMappers.position.get(WorldManager.player);
    god = ComponentMappers.god.get(WorldManager.god);

    shadow = Main.asciiAtlas.createSprite("1113");
    question = Main.asciiAtlas.createSprite("1503");

    BitmapFont font = new BitmapFont();
    font.getData().setScale(.25f);
  }

  /**
   * Render shit.
   */
  public void render(float delta, float wrathFade) {
    Main.handheldCamera.update(delta, worldCamera, playerPosition.pos);

    // Handle screen shake
    if (Main.cameraShake.time > 0) {
      Main.cameraShake.update(delta, worldCamera, playerPosition.pos);
    }

    worldCamera.update();

    if (god.hasWrath) {
      GrayscaleShader.shader.begin();
      GrayscaleShader.shader.setUniformf("u_grayness", wrathFade);
      batch.setShader(GrayscaleShader.shader);
    } else {
      GrayscaleShader.shader.end();
      batch.setShader(null);
    }

    batch.setProjectionMatrix(worldCamera.combined);
    batch.begin();

    renderCells(wrathFade);
    renderStairs();
    renderDecorations();
    renderTraps();
    renderItems();
    renderEnemies();
    renderPlayer();
    renderShadows();
    renderLights();
    renderHighlights();

    batch.end();
  }

  private void renderCells(float wrathFade) {
    Map map = WorldManager.world.getCurrentMap();

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        MapCell cell = map.getCellMap()[x][y];

        if (cell.hasBlood() && !cell.onFire) {
          if (god.hasWrath) {
            batch.setShader(null);
          }
        }

        if (playerAttributes.visionMap[x][y] > 0) {
          cell.hidden = false;

          if (cell.tween != null && !cell.tween.isStarted()) {
            cell.tween.start(Main.tweenManager);
          }
        }

        if (!cell.hidden) {
          cell.forgotten = playerAttributes.visionMap[x][y] <= 0;

          if (cell.forgotten) {
            if (!god.hasWrath) {
              cell.sprite.draw(batch);
            }
          } else {
            if (WorldManager.mapHelpers.getEntitiesAt(new Vector2(x, y)).size() == 0) {
              cell.sprite.draw(batch);
            }
          }
        }

        if (god.hasWrath) {
          batch.setShader(GrayscaleShader.shader);
        }
      }
    }
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
            Family.all(ItemComponent.class, PositionComponent.class, VisualComponent.class).get()
        );

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (Main.tweenManager.getRunningTimelinesCount() == 0) {
        WorldManager.entityHelpers.updateSprite(entity, position.pos.x, position.pos.y);
      }

      if (WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      }
    }
  }

  private void renderEnemies() {
    ImmutableArray<Entity> entities =
        WorldManager.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);

      if (Main.tweenManager.getRunningTimelinesCount() == 0) {
        WorldManager.entityHelpers.updateSprite(entity, position.pos.x, position.pos.y);
      }

      if (WorldManager.entityHelpers.isVisible(entity)) {
        ComponentMappers.visual.get(entity).sprite.draw(batch);
      } else if (WorldManager.entityHelpers.canHear(WorldManager.player, entity)) {
        question.setPosition(
            position.pos.x * Main.SPRITE_WIDTH, position.pos.y * Main.SPRITE_HEIGHT
        );

        question.draw(batch);
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
        WorldManager.entityHelpers.updateSprite(player, position.pos.x, position.pos.y);
      }

      ComponentMappers.visual.get(player).sprite.draw(batch);
    }
  }

  private void renderShadows() {
    Map map = WorldManager.world.getCurrentMap();

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        MapCell cell = WorldManager.mapHelpers.getCell(x, y);

        if (cell.hidden) {
          continue;
        }

        if (god.hasWrath && cell.forgotten) {
          continue;
        }

        Entity enemy = WorldManager.mapHelpers.getEnemyAt(x, y);

        boolean canHearEnemy = WorldManager.entityHelpers.hasTrait(
            WorldManager.player, "Perceptive"
        ) && enemy != null && WorldManager.entityHelpers.canHear(WorldManager.player, enemy);

        if (canHearEnemy) {
          continue;
        }

        float alpha = playerAttributes.visionMap[x][y];

        if (map.light.hasLights() && alpha > 0) {
          if (alpha + map.light.lightMap[x][y] > 1) {
            alpha = 0.9f;
          } else {
            alpha += map.light.lightMap[x][y];
          }
        }

        alpha = alpha <= .15f ? .15f : alpha;

        if (god.hasWrath) {
          shadow.setColor(Color.BLACK);
        } else {
          shadow.setColor(Colors.get(WorldManager.world.getCurrentMap().type + "Background"));
        }

        shadow.setAlpha(-alpha);
        shadow.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);

        shadow.draw(batch);
      }
    }
  }

  private void renderLights() {
    Map map = WorldManager.world.getCurrentMap();

    if (map.light.hasLights()) {
      for (int x = 0; x < map.width; x++) {
        for (int y = 0; y < map.height; y++) {
          MapCell cell = WorldManager.mapHelpers.getCell(x, y);

          if (cell.hidden || cell.forgotten) {
            continue;
          }

          float alpha = map.light.lightMap[x][y];

          if (alpha + map.light.lightMap[x][y] > 1) {
            alpha = 0.9f;
          }

          shadow.setColor(map.light.colorMap[x][y]);
          shadow.setAlpha(alpha / 10);
          shadow.setPosition(x * Main.SPRITE_WIDTH, y * Main.SPRITE_HEIGHT);

          shadow.draw(batch);
        }
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

  /**
   * Self-explanatory.
   *
   * @param width  New width
   * @param height New height
   */
  public void resize(int width, int height) {
    viewport.update(width, height, true);

    worldCamera.position.set(
        playerPosition.pos.x * Main.SPRITE_WIDTH,
        playerPosition.pos.y * Main.SPRITE_HEIGHT,
        0
    );
  }
}
