package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
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
  private final AttributesComponent playerAttributes;
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

    playerDetails = ComponentMappers.player.get(WorldManager.player);
    playerAttributes = ComponentMappers.attributes.get(WorldManager.player);

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

    ActionButton characterButton = new ActionButton("C", playerAttributes.name);
    characterButton.setKeys(Input.Keys.C);
    characterButton.setAction(bottomTable, () -> main.setScreen(new CharacterScreen(main)));

    ActionButton inventoryButton = new ActionButton("I", "Inventory");
    inventoryButton.setKeys(Input.Keys.I);
    inventoryButton.setAction(bottomTable, () -> main.setScreen(new CharacterScreen(main)));

    ActionButton restButton = new ActionButton("Z", "Rest");
    restButton.setKeys(Input.Keys.Z);
    restButton.setAction(bottomTable, () -> WorldManager.executeTurn = true);

    ActionButton pauseButton = new ActionButton("ESC", "Pause");
    pauseButton.setKeys(Input.Keys.ESCAPE);
    pauseButton.setAction(bottomTable, () -> main.setScreen(new PauseScreen(main)));

    Table buttons = new Table();
    buttons.add(characterButton).pad(0, 5, 0, 5);
    buttons.add(inventoryButton).pad(0, 5, 0, 5);
    buttons.add(restButton).pad(0, 5, 0, 5);
    buttons.add(pauseButton).pad(0, 5, 0, 5);

    // Look details

    lookDetails = new Label(null, Main.skin);
    bottomTable.add(lookDetails).pad(0, 0, 10, 0);
    bottomTable.row();
    bottomTable.add(buttons).pad(0, 0, 10, 0);

    // Look Dialog

    lookDialog = new Dialog("", Main.skin);
    lookDialog.pad(5, 5, 10, 10);
    lookDialog.setModal(false);
    lookDialog.setMovable(false);
    lookDialogGroup = new VerticalGroup().left();
    lookDialog.add(lookDialogGroup);

    // Focused Dialog

    focusedDialog = new Dialog("", Main.skin);
    focusedDialog.pad(5, 0, 10, 5);
    focusedDialog.setModal(false);
    focusedDialog.setMovable(false);
    focusedDialogTable = new Table().left();
    focusedDialog.add(focusedDialogTable);
  }

  private void renderActionLog() {
    actionLog.clear();

    for (int i = 0; i < WorldManager.log.things.size(); i++) {
      String action = WordUtils.wrap(WorldManager.log.things.get(i), 100);
      Label label = new Label(action, Main.skin);
      label.setColor(1f, 1f, 1f, i == 0 ? 1f : 0.5f);

      actionLog.addActor(label);
    }
  }

  private void renderAreaDetails() {
    areaDetails.clear();

    // Depth & turn count
    areaDetails.addActor(
        new Label("[DARK_GRAY]Depth " + (WorldManager.world.currentMapIndex + 1)
            + ", Turn " + WorldManager.turnCount, Main.skin)
    );
    areaDetails.addActor(new Label("", Main.skin));

    // Player area

    String name = playerAttributes.name;

    if (WorldManager.state == WorldManager.State.LOOKING) {
      name += " [DARK_GRAY][LOOKING][]";
    } else if (WorldManager.state == WorldManager.State.TARGETING) {
      name += " [DARK_GRAY][TARGETING][]";
    } else if (WorldManager.state == WorldManager.State.FOCUSED) {
      name += " [DARK_GRAY][FOCUSED][]";
    }

    areaDetails.addActor(new Label(name, Main.skin));

    String playerHealthColor;

    if (playerAttributes.health / playerAttributes.maxHealth <= 0.5f) {
      playerHealthColor = "[RED]";
    } else {
      playerHealthColor = "[WHITE]";
    }

    areaDetails.addActor(
        new Label(
            playerHealthColor + playerAttributes.health
                + "[LIGHT_GRAY]/" + playerAttributes.maxHealth, Main.skin
        )
    );

    areaDetails.addActor(
        new Label("[GREEN]" + playerAttributes.energy, Main.skin)
    );

    if (ComponentMappers.crippled.has(WorldManager.player)) {
      areaDetails.addActor(new Label("[DARK_GRAY]CRIPPLED[]", Main.skin));
    }

    if (ComponentMappers.bleeding.has(WorldManager.player)) {
      areaDetails.addActor(new Label("[DARK_GRAY]BLEEDING[]", Main.skin));
    }
  }

  private void checkAndRenderLookDetails() {
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
    lookDialogGroup.clear();

    Cell cell = WorldManager.mapHelpers.getCell(position.x, position.y);

    if (cell.forgotten) {
      lookDetails.setText("You remember seeing " + cell.description + ".");
    } else {
      lookDetails.setText("You see " + cell.description + ".");

      boolean showLookDialog = false;

      Entity item = WorldManager.entityHelpers.getItemAt(position);

      if (item != null) {
        showLookDialog = true;
        ItemComponent itemDetails = ComponentMappers.item.get(item);

        lookDialogGroup.addActor(
            new Label(
                "[YELLOW]" + WorldManager.entityHelpers.getItemName(WorldManager.player, item),
                Main.skin
            )
        );

        if (WorldManager.entityHelpers.itemIsIdentified(WorldManager.player, item)) {
          String description = WordUtils.wrap(itemDetails.description, 50);

          lookDialogGroup.addActor(
              new Label("[LIGHT_GRAY]" + description, Main.skin)
          );
        }
      }

      Entity enemy = WorldManager.entityHelpers.getEnemyAt(position);

      if (enemy != null) {
        showLookDialog = true;
        AttributesComponent enemyAttributes = ComponentMappers.attributes.get(enemy);

        lookDialogGroup.addActor(
            new Label("[RED]" + enemyAttributes.name, Main.skin)
        );

        String description = WordUtils.wrap(enemyAttributes.description, 50);

        lookDialogGroup.addActor(
            new Label("[LIGHT_GRAY]" + description, Main.skin)
        );

        lookDialogGroup.addActor(new Label("", Main.skin));

        String enemyHealthColor;

        if (enemyAttributes.health / enemyAttributes.maxHealth <= 0.5f) {
          enemyHealthColor = "[RED]";
        } else {
          enemyHealthColor = "[WHITE]";
        }

        lookDialogGroup.addActor(
            new Label(
                "[LIGHT_GRAY]HP " + enemyHealthColor + enemyAttributes.health
                    + "[LIGHT_GRAY]/" + enemyAttributes.maxHealth, Main.skin
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
    if (WorldManager.state == WorldManager.State.FOCUSED) {
      if (lookDialogShowing) {
        lookDialogShowing = false;
        lookDialog.hide(null);
      }

      if (!focusedDialogShowing) {
        focusedDialogTable.clear();

        PlayerComponent player = ComponentMappers.player.get(WorldManager.player);
        BodyComponent body = ComponentMappers.body.get(player.focusedEntity);

        int actionNumber = 0;
        for (String part : body.parts.keySet()) {
          actionNumber++;

          // If you look at the docs for Input.Keys, number keys are offset by 7
          // (e.g. 0 = 7, 1 = 8, etc)
          ActionButton button = new ActionButton(actionNumber, WordUtils.capitalize(part));
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
    PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);
    PositionComponent focusedPosition = ComponentMappers.position.get(playerDetails.focusedEntity);

    if (playerDetails.focusedAction == PlayerComponent.FocusedAction.MELEE) {
      WorldManager.combatHelpers.preparePlayerForMelee(playerDetails.focusedEntity, part);
    } else if (playerDetails.focusedAction == PlayerComponent.FocusedAction.THROWING) {
      WorldManager.combatHelpers.preparePlayerForThrowing(focusedPosition.pos, part);
    } else if (playerDetails.focusedAction == PlayerComponent.FocusedAction.RANGED) {
      WorldManager.combatHelpers.preparePlayerForRanged(focusedPosition.pos, part);
    }

    WorldManager.state = WorldManager.State.PLAYING;
    WorldManager.executeTurn = true;
  }
}
