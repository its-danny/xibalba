package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;

public class HudRenderer {
  private final Main main;
  private Engine engine;
  private Map map;

  private Stage stage;

  private VerticalGroup actionLog;
  private VerticalGroup areaDetails;
  private Label lookDetails;

  public HudRenderer(Main main, Engine engine, SpriteBatch batch, Map map) {
    this.main = main;
    this.engine = engine;
    this.map = map;

    Viewport viewport = new FitViewport(960, 540, new OrthographicCamera());
    stage = new Stage(viewport, batch);

    Table topTable = new Table();
    topTable.top().left();
    topTable.setFillParent(true);

    actionLog = new VerticalGroup().left();
    topTable.add(actionLog).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 4 * 3 - 20).top();
    areaDetails = new VerticalGroup().right();
    topTable.add(areaDetails).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 4 - 20).top();

    stage.addActor(topTable);

    Table bottomTable = new Table();
    bottomTable.bottom().left();
    bottomTable.setFillParent(true);

    lookDetails = new Label(null, main.skin);
    bottomTable.add(lookDetails).pad(10, 10, 10, 10);

    stage.addActor(bottomTable);
  }

  public void render(float delta) {
    renderActionLog();
    renderAreaDetails();
    renderLookDetails();

    stage.act(delta);
    stage.draw();
  }

  private void renderActionLog() {
    actionLog.clear();

    for (int i = 0; i < main.log.things.size(); i++) {
      Label label = new Label(main.log.things.get(i), main.skin);
      label.setColor(1f, 1f, 1f, (i == 0 ? 1f : 1f / (i + 1)));
      label.setWrap(true);

      actionLog.addActor(label);
    }
  }

  private void renderAreaDetails() {
    areaDetails.clear();

    // Player area

    AttributesComponent playerAttributes = main.player.getComponent(AttributesComponent.class);

    areaDetails.addActor(new Label(playerAttributes.name, main.skin));

    String playerHealthColor;

    if (playerAttributes.health / playerAttributes.maxHealth <= 0.5f) {
      playerHealthColor = "[RED]";
    } else {
      playerHealthColor = "[WHITE]";
    }

    areaDetails.addActor(
        new Label(
            playerHealthColor + playerAttributes.health
                + "[LIGHT_GRAY]/" + playerAttributes.maxHealth, main.skin
        )
    );

    areaDetails.addActor(new Label("[DARK_GRAY]-----[]", main.skin));

    // Enemies visible in area

    ImmutableArray<Entity> enemies =
        this.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity enemy : enemies) {
      if (main.entityHelpers.isVisibleToPlayer(enemy, map)) {
        AttributesComponent enemyAttributes = enemy.getComponent(AttributesComponent.class);

        areaDetails.addActor(new Label(enemyAttributes.name, main.skin));

        String enemyHealthColor;

        if (enemyAttributes.health / enemyAttributes.maxHealth <= 0.5f) {
          enemyHealthColor = "[RED]";
        } else {
          enemyHealthColor = "[WHITE]";
        }

        areaDetails.addActor(
            new Label(
                enemyHealthColor + enemyAttributes.health
                    + "[LIGHT_GRAY]/" + enemyAttributes.maxHealth, main.skin
            )
        );
      }
    }
  }

  private void renderLookDetails() {
    lookDetails.clear();

    if (main.state == Main.State.SEARCHING && map.target != null) {
      Cell cell = map.getCell(map.target);

      if (cell.forgotten) {
        lookDetails.setText("You remember seeing " + cell.description);
      } else {
        lookDetails.setText("You see " + cell.description);
      }
    }
  }
}
