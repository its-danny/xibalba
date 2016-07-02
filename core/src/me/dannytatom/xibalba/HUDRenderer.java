package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.EnemyComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.map.Cell;
import me.dannytatom.xibalba.screens.CharacterScreen;
import me.dannytatom.xibalba.screens.PauseScreen;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.apache.commons.lang3.text.WordUtils;

public class HudRenderer {
  public final Stage stage;
  private final Main main;
  private final Viewport viewport;
  private final PlayerComponent playerDetails;
  private final Table topTable;
  private final Table bottomTable;
  private VerticalGroup actionLog;
  private VerticalGroup areaDetails;
  private Label lookDetails;
  private Dialog lookDialog;
  private VerticalGroup lookDialogGroup;
  private Dialog focusedDialog;
  private Table focusedDialogTable;
  private boolean lookDialogShowing = false;
  private boolean focusedDialogShowing = false;

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
    playerDetails = ComponentMappers.player.get(main.player);

    topTable = new Table();
    topTable.top().left();
    topTable.setFillParent(true);
    stage.addActor(topTable);

    setupTopTable();

    bottomTable = new Table();
    bottomTable.bottom();
    bottomTable.setFillParent(true);
    stage.addActor(bottomTable);

    setupBottomTable();

    stage.setKeyboardFocus(bottomTable);
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
    checkAndRenderFocused();

    stage.act(delta);
    stage.draw();
  }

  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  private void setupTopTable() {
    actionLog = new VerticalGroup().left();
    topTable.add(actionLog).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 4 * 3 - 20).top();
    areaDetails = new VerticalGroup().right();
    topTable.add(areaDetails).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 4 - 20).top();
  }

  private void setupBottomTable() {
    // Bottom buttons

    AttributesComponent playerAttributes = ComponentMappers.attributes.get(main.player);
    ActionButton characterButton = new ActionButton("C", playerAttributes.name, main.skin);
    characterButton.setKeys(Input.Keys.C);
    characterButton.setAction(bottomTable, () -> main.setScreen(new CharacterScreen(main)));

    ActionButton inventoryButton = new ActionButton("I", "Inventory", main.skin);
    inventoryButton.setKeys(Input.Keys.I);
    inventoryButton.setAction(bottomTable, () -> main.setScreen(new CharacterScreen(main)));

    ActionButton restButton = new ActionButton("Z", "Rest", main.skin);
    restButton.setKeys(Input.Keys.Z);
    restButton.setAction(bottomTable, () -> main.executeTurn = true);

    ActionButton pauseButton = new ActionButton("ESC", "Pause", main.skin);
    pauseButton.setKeys(Input.Keys.ESCAPE);
    pauseButton.setAction(bottomTable, () -> main.setScreen(new PauseScreen(main)));

    Table buttons = new Table();
    buttons.add(characterButton).pad(0, 5, 0, 5);
    buttons.add(inventoryButton).pad(0, 5, 0, 5);
    buttons.add(restButton).pad(0, 5, 0, 5);
    buttons.add(pauseButton).pad(0, 5, 0, 5);

    // Look details

    lookDetails = new Label(null, main.skin);
    bottomTable.add(lookDetails).pad(0, 0, 10, 0);
    bottomTable.row();
    bottomTable.add(buttons).pad(0, 0, 10, 0);

    // Look Dialog

    lookDialog = new Dialog("", main.skin);
    lookDialog.pad(5, 5, 10, 10);
    lookDialog.setModal(false);
    lookDialog.setMovable(false);
    lookDialogGroup = new VerticalGroup().left();
    lookDialog.add(lookDialogGroup);

    // Focused Dialog

    focusedDialog = new Dialog("", main.skin);
    focusedDialog.pad(5, 0, 10, 5);
    focusedDialog.setModal(false);
    focusedDialog.setMovable(false);
    focusedDialogTable = new Table().left();
    focusedDialog.add(focusedDialogTable);
  }

  private void renderActionLog() {
    actionLog.clear();

    for (int i = 0; i < main.log.things.size(); i++) {
      String action = WordUtils.wrap(main.log.things.get(i), 100);
      Label label = new Label(action, main.skin);
      label.setColor(1f, 1f, 1f, i == 0 ? 1f : 0.5f);

      actionLog.addActor(label);
    }
  }

  private void renderAreaDetails() {
    areaDetails.clear();

    // Depth
    areaDetails.addActor(
        new Label("[DARK_GRAY]Depth " + (main.world.currentMapIndex + 1) + "[]", main.skin)
    );
    areaDetails.addActor(new Label("", main.skin));

    // Player area

    AttributesComponent playerAttributes = ComponentMappers.attributes.get(main.player);

    String name = playerAttributes.name;

    if (main.state == Main.State.LOOKING) {
      name += " [DARK_GRAY][LOOKING][]";
    } else if (main.state == Main.State.TARGETING) {
      name += " [DARK_GRAY][TARGETING][]";
    } else if (main.state == Main.State.FOCUSED) {
      name += " [DARK_GRAY][FOCUSED][]";
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

    // Enemies visible in area

    ImmutableArray<Entity> enemies =
        main.engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    boolean showingEnemyBreak = false;

    for (Entity enemy : enemies) {
      if (main.entityHelpers.isVisibleToPlayer(enemy)) {
        if (!showingEnemyBreak) {
          areaDetails.addActor(new Label("", main.skin));
          showingEnemyBreak = true;
        }

        AttributesComponent enemyAttributes = ComponentMappers.attributes.get(enemy);

        areaDetails.addActor(new Label("[RED]" + enemyAttributes.name + "[]", main.skin));

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

    // Items visible in area

    ImmutableArray<Entity> items =
        main.engine.getEntitiesFor(Family.all(ItemComponent.class).get());

    boolean showingItemBreak = false;

    for (Entity item : items) {
      if (main.entityHelpers.isVisibleToPlayer(item)) {
        if (!showingItemBreak) {
          areaDetails.addActor(new Label("", main.skin));
          showingItemBreak = true;
        }

        ItemComponent itemDetails = ComponentMappers.item.get(item);

        areaDetails.addActor(new Label("[YELLOW]" + itemDetails.name + "[]", main.skin));
      }
    }
  }

  private void checkAndRenderLookDetails() {
    lookDialogGroup.clear();

    if (playerDetails.target == null) {
      lookDetails.setText("");

      if (lookDialogShowing) {
        lookDialogShowing = false;
        lookDialog.hide(null);
      }
    } else {
      renderLookDetails(playerDetails.target);
    }
  }

  private void renderLookDetails(Vector2 position) {
    Cell cell = main.mapHelpers.getCell(position.x, position.y);

    if (cell.forgotten) {
      lookDetails.setText("You remember seeing " + cell.description + ".");
    } else {
      lookDetails.setText("You see " + cell.description + ".");

      boolean showLookDialog = false;

      Entity item = main.entityHelpers.getItemAt(position);

      if (item != null) {
        showLookDialog = true;
        ItemComponent itemDetails = ComponentMappers.item.get(item);

        lookDialogGroup.addActor(
            new Label("[YELLOW]" + itemDetails.name, main.skin)
        );

        String description = WordUtils.wrap(itemDetails.description, 50);

        lookDialogGroup.addActor(
            new Label("[LIGHT_GRAY]" + description, main.skin)
        );
      }

      Entity enemy = main.entityHelpers.getEnemyAt(position);

      if (enemy != null) {
        showLookDialog = true;
        AttributesComponent enemyAttributes = ComponentMappers.attributes.get(enemy);

        lookDialogGroup.addActor(
            new Label("[RED]" + enemyAttributes.name, main.skin)
        );

        String description = WordUtils.wrap(enemyAttributes.description, 50);

        lookDialogGroup.addActor(
            new Label("[LIGHT_GRAY]" + description, main.skin)
        );

        lookDialogGroup.addActor(new Label("", main.skin));

        String enemyHealthColor;

        if (enemyAttributes.health / enemyAttributes.maxHealth <= 0.5f) {
          enemyHealthColor = "[RED]";
        } else {
          enemyHealthColor = "[WHITE]";
        }

        lookDialogGroup.addActor(
            new Label(
                "[LIGHT_GRAY]HP " + enemyHealthColor + enemyAttributes.health
                    + "[LIGHT_GRAY]/" + enemyAttributes.maxHealth, main.skin
            )
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

  private void checkAndRenderFocused() {
    if (main.state == Main.State.FOCUSED) {
      if (lookDialogShowing) {
        lookDialogShowing = false;
        lookDialog.hide(null);
      }

      if (!focusedDialogShowing) {
        focusedDialogTable.clear();

        BodyComponent body = ComponentMappers.body.get(main.focusedEntity);

        int actionNumber = 0;
        for (String part : body.parts.keySet()) {
          actionNumber++;

          // If you look at the docs for Input.Keys, number keys are offset by 7
          // (e.g. 0 = 7, 1 = 8, etc)
          ActionButton button = new ActionButton(actionNumber, WordUtils.capitalize(part), main.skin);
          button.setKeys(actionNumber + 7);
          button.setAction(focusedDialogTable, () -> handleFocusedAttack(part));

          focusedDialogTable.add(button).pad(0, 5, 0, 5);
        }

        focusedDialogShowing = true;
        focusedDialog.show(stage, null);

        focusedDialog.setPosition(
            Math.round((stage.getWidth() - focusedDialog.getWidth()) / 2), 65
        );

        stage.setKeyboardFocus(focusedDialogTable);
      }
    } else {
      if (focusedDialogShowing) {
        focusedDialogShowing = false;
        focusedDialog.hide(null);
        stage.setKeyboardFocus(bottomTable);
      }
    }
  }

  private void handleFocusedAttack(String part) {
    Gdx.app.log("HudRenderer", "Attacking " + part);

    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);
    PositionComponent focusedPosition = ComponentMappers.position.get(main.focusedEntity);

    if (playerDetails.focusedAction == PlayerComponent.FocusedAction.MELEE) {
      main.combatHelpers.preparePlayerForMelee(main.focusedEntity, part);
    } else if (playerDetails.focusedAction == PlayerComponent.FocusedAction.THROWING) {
      main.combatHelpers.preparePlayerForThrowing(focusedPosition.pos, part);
    } else if (playerDetails.focusedAction == PlayerComponent.FocusedAction.RANGED) {
      main.combatHelpers.preparePlayerForRanged(focusedPosition.pos, part);
    }

    main.state = Main.State.PLAYING;
    main.executeTurn = true;
  }
}
