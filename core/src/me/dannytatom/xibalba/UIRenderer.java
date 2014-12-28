package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.ai.BrainComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class UIRenderer {
  private final OrthographicCamera camera;
  private final Engine engine;
  private final SpriteBatch batch;
  private final BitmapFont font;

  /**
   * UIRenderer constructor.
   *
   * @param engine Ashely engine
   * @param batch  The sprite batch to use (set in PlayScreen)
   */
  public UIRenderer(Engine engine, SpriteBatch batch) {
    this.engine = engine;
    this.batch = batch;

    font = new BitmapFont();

    camera = new OrthographicCamera(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight());
    camera.update();
  }

  public void render() {
    Gdx.gl.glViewport((Gdx.graphics.getWidth() / 4) * 3, 0,
        Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight());

    camera.position.set((Gdx.graphics.getWidth() / 4) - (Gdx.graphics.getWidth() / 4) / 2,
        Gdx.graphics.getHeight() / 2, 0);
    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    drawShit();

    batch.end();
  }

  private void drawShit() {
    ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(AttributesComponent.class).get());

    for (int i = 0; i < entities.size(); i++) {
      Entity entity = entities.get(i);
      BrainComponent brain = ComponentMappers.brain.get(entity);
      AttributesComponent attributes = ComponentMappers.attributes.get(entity);
      PositionComponent position = ComponentMappers.position.get(entity);

      String str =
          (brain != null ? brain.state.toString().toLowerCase() : "player") + " "
              + attributes.energy + "e " + attributes.health + "hp "
              + position.pos.x + ", " + position.pos.y;

      if (entity.getComponent(PlayerComponent.class) != null) {
        font.setColor(Color.WHITE);
      } else {
        font.setColor(Color.LIGHT_GRAY);
      }

      font.draw(batch, str, 10, Gdx.graphics.getHeight() - (10 + (i * 20)));

      entity.getComponents();
    }
  }
}
