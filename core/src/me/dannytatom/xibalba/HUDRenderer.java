package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.screens.CharacterScreen;
import me.dannytatom.xibalba.screens.HelpScreen;
import me.dannytatom.xibalba.screens.InventoryScreen;

public class HudRenderer {
  public final Stage stage;
  private final Main main;
  private final Viewport viewport;
  private final VerticalGroup actionLog;
  private final VerticalGroup areaDetails;
  private final Label lookDetails;
  private final Dialog lookDialog;
  private final VerticalGroup lookDialogList;
  private boolean lookDialogShowing = false;

  /**
   * Renders the HUD.
   *
   * @param main  Instance of Main class
   * @param batch The sprite batch to use (set in PlayScreen)
   */
  public HudRenderer(Main main, SpriteBatch batch) {
    this.main = main;

    viewport = new FitViewport(960, 540, new OrthographicCamera());
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
    bottomTable.bottom();
    bottomTable.setFillParent(true);

    TextButton characterButton = new TextButton("[CYAN]C[LIGHT_GRAY] Character", main.skin);
    characterButton.pad(5);
    characterButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        main.setScreen(new CharacterScreen(main));
      }
    });

    TextButton inventoryButton = new TextButton("[CYAN]I[LIGHT_GRAY] Inventory", main.skin);
    inventoryButton.pad(5);
    inventoryButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        main.setScreen(new InventoryScreen(main));
      }
    });

    TextButton helpButton = new TextButton("[CYAN]?[LIGHT_GRAY] Help", main.skin);
    helpButton.pad(5);
    helpButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        main.setScreen(new HelpScreen(main));
      }
    });

    Table buttons = new Table();
    buttons.add(characterButton).pad(0, 5, 0, 5);
    buttons.add(inventoryButton).pad(0, 5, 0, 5);
    buttons.add(helpButton).pad(0, 5, 0, 5);

    lookDetails = new Label(null, main.skin);
    bottomTable.add(lookDetails).pad(0, 0, 10, 0);
    bottomTable.row();
    bottomTable.add(buttons).pad(0, 0, 10, 0);

    stage.addActor(bottomTable);

    lookDialog = new Dialog("", main.skin);
    lookDialog.pad(5, 10, 15, 10);
    lookDialog.setModal(false);
    lookDialog.setMovable(false);
    lookDialogList = new VerticalGroup().left();
    lookDialog.add(lookDialogList);
  }

  /**
   * Do some rendering.
   *
   * @param delta Elapsed time
   */
  public void render(float delta) {
    renderActionLog();
    renderAreaDetails();
    checkAndRenderLookDetails();

    stage.act(delta);
    stage.draw();
  }

  public void resize(int width, int height) {
    viewport.update(width, height, true);
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

    // Depth
    areaDetails.addActor(
        new Label("[DARK_GRAY]Depth " + (main.currentMapIndex + 1) + "[]", main.skin)
    );
    areaDetails.addActor(new Label("", main.skin));

    // Player area

    AttributesComponent playerAttributes = main.player.getComponent(AttributesComponent.class);

    String name = playerAttributes.name;

    if (main.state == Main.State.LOOKING) {
      name += " [DARK_GRAY][LOOKING][]";
    } else if (main.state == Main.State.TARGETING) {
      name += " [DARK_GRAY][TARGETING][]";
    }

    areaDetails.addActor(new Label(name, main.skin));

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
        main.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    for (Entity enemy : enemies) {
      if (main.entityHelpers.isVisibleToPlayer(enemy)) {
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

  private void checkAndRenderLookDetails() {
    lookDialogList.clear();

    Map map = main.getCurrentMap();

    if (map.target != null) {
      renderLookDetails(map.target);
    } else {
      lookDetails.setText("");

      if (lookDialogShowing) {
        lookDialogShowing = false;
        lookDialog.hide(null);
      }
    }
  }

  private void renderLookDetails(Vector2 position) {
    Cell cell = main.mapHelpers.getCell(position.x, position.y);

    if (cell.forgotten) {
      lookDetails.setText("You remember seeing " + cell.description + ".");
    } else {
      lookDetails.setText("You see " + cell.description + ".");

      boolean showLookDialog = false;

      Entity itemAtLocation = main.entityHelpers.getItemAt(position);

      if (itemAtLocation != null) {
        showLookDialog = true;
        ItemComponent itemComponent = itemAtLocation.getComponent(ItemComponent.class);

        lookDialogList.addActor(
            new Label("[YELLOW]" + itemComponent.name, main.skin)
        );

        lookDialogList.addActor(
            new Label("[LIGHT_GRAY]" + itemComponent.description, main.skin)
        );
      }

      Entity enemyAtLocation = main.entityHelpers.getEnemyAt(position);

      if (enemyAtLocation != null) {
        showLookDialog = true;
        AttributesComponent attributesComponent =
            enemyAtLocation.getComponent(AttributesComponent.class);

        lookDialogList.addActor(
            new Label("[RED]" + attributesComponent.name, main.skin)
        );

        lookDialogList.addActor(
            new Label("[LIGHT_GRAY]" + attributesComponent.description, main.skin)
        );
      }

      if (lookDialogShowing) {
        if (!showLookDialog) {
          lookDialogShowing = false;
          lookDialog.hide(null);
        }
      } else {
        if (showLookDialog) {
          lookDialogShowing = true;
          lookDialog.show(stage, null);

          lookDialog.setPosition(
              Math.round((stage.getWidth() - lookDialog.getWidth()) / 2), 65
          );
        }
      }
    }
  }
}
