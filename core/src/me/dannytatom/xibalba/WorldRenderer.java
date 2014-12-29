package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.map.ShadowCaster;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class WorldRenderer {
  private static final int SPRITE_WIDTH = 24;
  private static final int SPRITE_HEIGHT = 24;

  private final Main game;
  private final Engine engine;
  private final SpriteBatch batch;
  private final Map map;
  private final Entity player;
  private final OrthographicCamera camera;
  private final ShadowCaster caster;

  /**
   * WorldRenderer constructor.
   *
   * @param engine Ashely engine
   * @param batch  The sprite batch to use (set in PlayScreen)
   * @param map    The map we're on
   */
  public WorldRenderer(Main main, Engine engine, SpriteBatch batch, Map map, Entity player) {
    game = main;
    this.engine = engine;
    this.batch = batch;
    this.map = map;
    this.player = player;

    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.update();

    caster = new ShadowCaster();
  }

  /**
   * Render shit.
   */
  public void render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    Gdx.gl.glClearColor(0, 0, 0, 1);

    // Get player pos & attributes
    PositionComponent playerPosition = player.getComponent(PositionComponent.class);
    AttributesComponent playerAttributes = player.getComponent(AttributesComponent.class);

    // Update worldCamera
    camera.position.set(playerPosition.pos.x * SPRITE_WIDTH,
        playerPosition.pos.y * SPRITE_HEIGHT, 0);
    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    float[][] lightMap = caster.calculateFOV(map.createResistanceMap(),
        (int) playerPosition.pos.x, (int) playerPosition.pos.y,
        playerAttributes.vision);

    for (int x = 0; x < map.width; x++) {
      for (int y = 0; y < map.height; y++) {
        Cell cell = map.getCell(x, y);

        if (lightMap[x][y] > 0) {
          cell.hidden = false;
        }

        if (!cell.hidden) {
          batch.setColor(1f, 1f, 1f, lightMap[x][y] <= 0.35f ? 0.35f : lightMap[x][y]);
          batch.draw(cell.sprite, x * SPRITE_WIDTH, y * SPRITE_HEIGHT);
          batch.setColor(1f, 1f, 1f, 1f);
        }
      }
    }

    ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(PositionComponent.class,
        VisualComponent.class).get());

    for (Entity entity : entities) {
      PositionComponent position = ComponentMappers.position.get(entity);
      VisualComponent visual = ComponentMappers.visual.get(entity);
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);

      if (!map.getCell(position.pos).hidden) {
        batch.setColor(1f, 1f, 1f, lightMap[(int) position.pos.x][(int) position.pos.y]);

        if (attributes != null) {
          TextureAtlas atlas = game.assets.get("sprites/ui.atlas");
          Sprite sprite;

          int which;
          int fourth = (attributes.maxHealth / 4) - (attributes.health / 4);

          if (fourth <= 2.5) {
            which = 1;
          } else if (fourth <= 5) {
            which = 2;
          } else if (fourth <= 7.5) {
            which = 3;
          } else {
            which = 4;
          }

          if (entity.getComponent(PlayerComponent.class) != null) {
            sprite = atlas.createSprite("player-health-" + which);
          } else {
            sprite = atlas.createSprite("enemy-health-" + which);
          }

          batch.draw(sprite, position.pos.x * SPRITE_WIDTH, position.pos.y * SPRITE_HEIGHT);
        }

        batch.draw(visual.sprite, position.pos.x * SPRITE_WIDTH,
            (position.pos.y * SPRITE_HEIGHT) + (SPRITE_HEIGHT / 2));

        batch.setColor(1f, 1f, 1f, 1f);
      }
    }

    batch.end();
  }
}
