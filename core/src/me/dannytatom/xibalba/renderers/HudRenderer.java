package me.dannytatom.xibalba.renderers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.screens.CharacterScreen;
import me.dannytatom.xibalba.screens.MainMenuScreen;
import me.dannytatom.xibalba.screens.PauseScreen;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.YamlToAbility;
import me.dannytatom.xibalba.world.MapCell;
import me.dannytatom.xibalba.world.WorldManager;
import org.apache.commons.lang3.text.WordUtils;

public class HudRenderer {
  public final Stage stage;
  private final Main main;
  private final Viewport viewport;

  private final Entity player;
  private final PlayerComponent playerDetails;
  private final AttributesComponent playerAttributes;
  private final PositionComponent playerPosition;

  private final Table bottomTable;

  private final VerticalGroup playerInfo;
  private final VerticalGroup enemyInfo;
  private final VerticalGroup debugInfo;
  private final VerticalGroup actionLog;
  private final Table focusedTable;
  private final VerticalGroup buttonsAndAreaDetails;
  private final VerticalGroup areaDetails;
  private final Table abilityButtons;
  private final Table menuButtons;
  private Dialog deathDialog;
  private boolean deathDialogShowing = false;

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

    player = WorldManager.player;
    playerDetails = ComponentMappers.player.get(player);
    playerAttributes = ComponentMappers.attributes.get(player);
    playerPosition = ComponentMappers.position.get(player);

    Table topTable = new Table();
    topTable.top().left();
    topTable.setFillParent(true);
    stage.addActor(topTable);


    playerInfo = new VerticalGroup().left();
    enemyInfo = new VerticalGroup().center();
    debugInfo = new VerticalGroup().right();

    int width = Gdx.graphics.getWidth() / 3 - 20;

    topTable.add(playerInfo).pad(10, 10, 10, 10).width(width).top();
    topTable.add(enemyInfo).pad(10, 10, 10, 10).width(width).top();
    topTable.add(debugInfo).pad(10, 10, 10, 10).width(width).top();

    bottomTable = new Table();
    bottomTable.bottom();
    bottomTable.setFillParent(true);
    stage.addActor(bottomTable);

    actionLog = new VerticalGroup().left();
    focusedTable = new Table().center();
    buttonsAndAreaDetails = new VerticalGroup().right();

    areaDetails = new VerticalGroup().left().pad(0, 0, 5, 0);
    buttonsAndAreaDetails.addActor(areaDetails);

    abilityButtons = new Table().right().pad(0, 0, 5, 0);
    setupAbilitiesButtons();
    menuButtons = new Table().right();
    setupMenuButtons();

    bottomTable.add(actionLog).pad(10, 10, 10, 10).width(width).bottom();
    bottomTable.add(focusedTable).pad(10, 10, 10, 10).width(width).top();
    bottomTable.add(buttonsAndAreaDetails).pad(10, 10, 10, 10).width(width).bottom();

    setupDeathDialog();

    stage.setKeyboardFocus(bottomTable);
  }

  /**
   * Do some rendering.
   *
   * @param delta Elapsed time
   */
  public void render(float delta) {
    updatePlayerInfo();
    updateEnemyInfo();
    updateDebugInfo();
    updateActionLog();
    updateFocused();
    updateAreaDetails();
    updateAbilitiesButtons();

    if (WorldManager.state == WorldManager.State.DEAD && !deathDialogShowing) {
      showDeathDialog();
    }

    stage.act(delta);
    stage.draw();
  }

  private void setupAbilitiesButtons() {
    Array<YamlToAbility> abilities = ComponentMappers.abilities.get(player).abilities;

    for (int i = 0; i < abilities.size; i++) {
      YamlToAbility ability = abilities.get(i);

      // If you look at the docs for Input.Keys, number keys are offset by 7
      // (e.g. 0 = 7, 1 = 8, etc)
      ActionButton button = new ActionButton(i + 1, null);
      button.setKeys(i + 8);
      button.setAction(bottomTable, () -> {
        if (WorldManager.state != WorldManager.State.FOCUSED) {
          WorldManager.abilityHelpers.doAbility(player, ability);
        }
      });

      if (i == abilities.size - 1) {
        abilityButtons.add(button).pad(0, 5, 0, 0);
      } else {
        abilityButtons.add(button).pad(0, 5, 0, 5);
      }
    }

    buttonsAndAreaDetails.addActor(abilityButtons);
  }

  private void setupMenuButtons() {
    ActionButton characterButton = new ActionButton("C", playerAttributes.name);
    characterButton.setKeys(Input.Keys.C);
    characterButton.setAction(bottomTable, () -> main.setScreen(new CharacterScreen(main)));
    menuButtons.add(characterButton).pad(0, 5, 0, 5);

    ActionButton restButton = new ActionButton("Z", "Rest");
    restButton.setKeys(Input.Keys.Z);
    restButton.setAction(bottomTable, () -> WorldManager.executeTurn = true);
    menuButtons.add(restButton).pad(0, 5, 0, 5);

    ActionButton pauseButton = new ActionButton("ESC", "Pause");
    pauseButton.setKeys(Input.Keys.ESCAPE);
    pauseButton.setAction(bottomTable, () -> main.setScreen(new PauseScreen(main)));
    menuButtons.add(pauseButton).pad(0, 5, 0, 0);

    buttonsAndAreaDetails.addActor(menuButtons);
  }

  private void setupDeathDialog() {
    deathDialog = new Dialog("", Main.skin) {
      public void result(Object obj) {
        if (obj.equals(true)) {
          Main.playScreen.dispose();
          main.setScreen(new MainMenuScreen(main));
        } else {
          Gdx.app.exit();
        }
      }
    };

    deathDialog.button("[DARK_GRAY][[CYAN] ENTER [DARK_GRAY]][WHITE] Return to Main Menu", true);
    deathDialog.key(Input.Keys.ENTER, true);
    deathDialog.button("[DARK_GRAY][[CYAN] Q [DARK_GRAY]][WHITE] Quit", false);
    deathDialog.key(Input.Keys.Q, false);
    deathDialog.pad(10);
  }

  private void updatePlayerInfo() {
    String name = playerAttributes.name;

    if (WorldManager.state == WorldManager.State.LOOKING) {
      name += " [DARK_GRAY][LOOKING][]";
    } else if (WorldManager.state == WorldManager.State.TARGETING) {
      name += " [DARK_GRAY][TARGETING][]";
    } else if (WorldManager.state == WorldManager.State.FOCUSED) {
      name += " [DARK_GRAY][FOCUSED][]";
    }

    if (playerInfo.getChildren().size == 0) {
      playerInfo.addActor(new Label(name, Main.skin));
      playerInfo.addActor(new Label(createEntityHealth(player), Main.skin));
      playerInfo.addActor(new Label(createEntityDivineFavor(player), Main.skin));
      playerInfo.addActor(new Label(createEntityOxygen(player), Main.skin));
      playerInfo.addActor(new Label(createEntityStatus(player), Main.skin));
    } else {
      Label playerNameLabel = (Label) playerInfo.getChildren().get(0);
      playerNameLabel.setText(name);
      Label playerHealthLabel = (Label) playerInfo.getChildren().get(1);
      playerHealthLabel.setText(createEntityHealth(player));
      Label playerDivineFavorLabel = (Label) playerInfo.getChildren().get(2);
      playerDivineFavorLabel.setText(createEntityDivineFavor(player));
      Label playerOxygenLabel = (Label) playerInfo.getChildren().get(3);
      playerOxygenLabel.setText(createEntityOxygen(player));
      Label playerStatusLabel = (Label) playerInfo.getChildren().get(4);
      playerStatusLabel.setText(createEntityStatus(player));
    }
  }

  private void updateEnemyInfo() {
    if (playerDetails.lastHitEntity == null) {
      enemyInfo.clear();
    } else {
      AttributesComponent enemyAttributes
          = ComponentMappers.attributes.get(playerDetails.lastHitEntity);
      String name = enemyAttributes.name;

      if (enemyInfo.getChildren().size == 0) {
        enemyInfo.addActor(new Label(name, Main.skin));
        enemyInfo.addActor(new Label(createEntityHealth(playerDetails.lastHitEntity), Main.skin));
        enemyInfo.addActor(new Label(createEntityOxygen(playerDetails.lastHitEntity), Main.skin));
        enemyInfo.addActor(new Label(createEntityStatus(playerDetails.lastHitEntity), Main.skin));
      } else {
        Label enemyNameLabel = (Label) enemyInfo.getChildren().get(0);
        enemyNameLabel.setText(name);
        Label enemyHealthLabel = (Label) enemyInfo.getChildren().get(1);
        enemyHealthLabel.setText(createEntityHealth(playerDetails.lastHitEntity));
        Label enemyOxygenLabel = (Label) enemyInfo.getChildren().get(2);
        enemyOxygenLabel.setText(createEntityOxygen(playerDetails.lastHitEntity));
        Label enemyStatusLabel = (Label) enemyInfo.getChildren().get(3);
        enemyStatusLabel.setText(createEntityStatus(playerDetails.lastHitEntity));
      }
    }
  }

  private void updateDebugInfo() {
    String playerInfo = "[DARK_GRAY]" + WorldManager.world.getCurrentMap().time.time.toString()
        + ", Depth " + (WorldManager.world.currentMapIndex + 1)
        + ", Turn " + WorldManager.turnCount
        + ", " + (int) playerPosition.pos.x + ":" + (int) playerPosition.pos.y;
    String performanceInfo = "[DARK_GRAY]v0.1.0 FPS " + Gdx.graphics.getFramesPerSecond();

    if (debugInfo.getChildren().size == 0) {
      debugInfo.addActor(new Label(playerInfo, Main.skin));
      debugInfo.addActor(new Label(performanceInfo, Main.skin));
    } else {
      Label playerInfoLabel = (Label) debugInfo.getChildren().get(0);
      playerInfoLabel.setText(playerInfo);
      Label performanceInfoLabel = (Label) debugInfo.getChildren().get(1);
      performanceInfoLabel.setText(performanceInfo);
    }
  }

  private void updateActionLog() {
    for (int i = 0; i < WorldManager.log.actions.size(); i++) {
      String action = WordUtils.wrap(WorldManager.log.actions.get(i), 50);

      if (actionLog.getChildren().size <= i) {
        Label label = new Label(action, Main.skin);
        label.setColor(1f, 1f, 1f, i == 0 ? 1f : 0.5f);

        actionLog.addActor(label);
      } else {
        Label label = (Label) actionLog.getChildren().get(i);
        label.setColor(1f, 1f, 1f, i == 0 ? 1f : 0.5f);
        label.setText(action);
      }
    }
  }

  private void updateFocused() {
    if (WorldManager.state == WorldManager.State.FOCUSED) {
      if (focusedTable.getChildren().size == 0) {
        BodyComponent body = ComponentMappers.body.get(playerDetails.focusedEntity);

        int actionNumber = 0;
        for (String part : body.parts.keySet()) {
          actionNumber++;

          // If you look at the docs for Input.Keys, number keys are offset by 7
          // (e.g. 0 = 7, 1 = 8, etc)
          ActionButton button = new ActionButton(actionNumber, WordUtils.capitalize(part));
          button.setKeys(actionNumber + 7);
          button.setAction(bottomTable, () -> handleFocusedAttack(part));
          focusedTable.add(button).pad(5, 0, 0, 0);

          if ((actionNumber & 1) == 0) {
            focusedTable.row();
          }
        }
      }
    } else {
      if (focusedTable.getChildren().size > 0) {
        focusedTable.clear();
      }
    }
  }

  private void updateAreaDetails() {
    if (areaDetails.getChildren().size == 0) {
      areaDetails.addActor(new Label(null, Main.skin));
      areaDetails.addActor(new Label(null, Main.skin));
    }

    if (playerDetails.target == null) {
      if (!WorldManager.mapHelpers.cellExists(playerPosition.pos)) {
        return;
      }

      MapCell cell = WorldManager.mapHelpers.getCell(playerPosition.pos.x, playerPosition.pos.y);
      String cellDescription = "You stand on " + cell.description;

      Label placeholder = (Label) areaDetails.getChildren().get(0);
      placeholder.setText(null);
      Label cellDescriptionLabel = (Label) areaDetails.getChildren().get(1);
      cellDescriptionLabel.setText(cellDescription);
    } else {
      if (!WorldManager.mapHelpers.cellExists(playerDetails.target)) {
        return;
      }

      MapCell cell
          = WorldManager.mapHelpers.getCell(playerDetails.target.x, playerDetails.target.y);
      String cellDescription;

      if (cell.forgotten) {
        cellDescription = "You remember seeing " + cell.description;
      } else {
        cellDescription = "You see " + cell.description;
      }

      Entity entity
          = WorldManager.mapHelpers.getEntityAt(playerDetails.target.x, playerDetails.target.y);

      if (!WorldManager.entityHelpers.canSee(WorldManager.player, entity)) {
        entity = null;
      }

      String entityName = null;
      String entityDescription = null;

      if (entity == null) {
        Label placeholder = (Label) areaDetails.getChildren().get(0);
        placeholder.setText(null);
        Label cellDescriptionLabel = (Label) areaDetails.getChildren().get(1);
        cellDescriptionLabel.setText(cellDescription);
      } else {
        if (ComponentMappers.item.has(entity)) {
          entityName = "[YELLOW]" + WorldManager.itemHelpers.getName(player, entity);

          if (WorldManager.itemHelpers.isIdentified(player, entity)) {
            entityDescription = WordUtils.wrap(ComponentMappers.item.get(entity).description, 50);
          } else {
            entityDescription = "You're not sure what this does";
          }
        } else if (ComponentMappers.enemy.has(entity)) {
          AttributesComponent enemyAttributes = ComponentMappers.attributes.get(entity);

          BrainComponent brain = ComponentMappers.brain.get(entity);
          entityName = "[RED]" + enemyAttributes.name
              + " [DARK_GRAY]" + brain.stateMachine.getCurrentState().name();

          entityDescription = WordUtils.wrap(enemyAttributes.description, 50);
        }

        Label entityNameLabel = (Label) areaDetails.getChildren().get(0);
        entityNameLabel.setText(entityName);
        Label entityDescriptionLabel = (Label) areaDetails.getChildren().get(1);
        entityDescriptionLabel.setText(entityDescription);
      }
    }
  }

  private void updateAbilitiesButtons() {
    Array<YamlToAbility> abilities = ComponentMappers.abilities.get(player).abilities;

    for (int i = 0; i < abilities.size; i++) {
      YamlToAbility ability = abilities.get(i);
      ActionButton button = (ActionButton) abilityButtons.getChildren().get(i);

      if (ability.counter != ability.recharge) {
        button.setLabel(ability.name + " [DARK_GRAY]" + ability.counter + "/" + ability.recharge);
        button.setDisabled(true);
      } else {
        button.setLabel(ability.name);
        button.setDisabled(false);
      }
    }
  }

  private void showDeathDialog() {
    Table table = deathDialog.getContentTable();
    table.pad(0);
    table.add(new Label("YOU HAVE FAILED", Main.skin)).center().row();

    String depth = "[LIGHT_GRAY]You made it to depth[] " + playerDetails.lowestDepth;
    table.add(new Label(depth, Main.skin)).left().row();

    String hits = "[LIGHT_GRAY]You hit enemies[] " + playerDetails.totalHits
        + "[LIGHT_GRAY] times and missed[] " + playerDetails.totalMisses;
    table.add(new Label(hits, Main.skin)).left().row();

    String damage = "[LIGHT_GRAY]You did[] " + playerDetails.totalDamageDone
        + "[LIGHT_GRAY] damage, took[] " + playerDetails.totalDamageReceived
        + "[LIGHT_GRAY], and healed[] " + playerDetails.totalDamageHealed;
    table.add(new Label(damage, Main.skin)).left().row();

    String kills = "[LIGHT_GRAY]You killed[] "
        + playerDetails.totalKills + "[LIGHT_GRAY] enemies";
    table.add(new Label(kills, Main.skin)).left().row();

    deathDialog.getButtonTable().pad(5, 0, 0, 0);
    deathDialogShowing = true;
    deathDialog.show(stage);

    deathDialog.setPosition(
        deathDialog.getX(), deathDialog.getY() + (deathDialog.getY() / 2)
    );
  }

  private String createEntityHealth(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    String healthTextColor = attributes.health < attributes.maxHealth / 2 ? "[RED]" : "[WHITE]";
    String healthText = healthTextColor + attributes.health
        + "[LIGHT_GRAY]/" + attributes.maxHealth;
    String healthBar = "[LIGHT_GRAY]HP [";

    for (int i = 0; i < MathUtils.floor(attributes.maxHealth / 10); i++) {
      if (attributes.health < (i * 10)) {
        healthBar += "[DARK_GRAY]x";
      } else {
        healthBar += "[WHITE]x";
      }
    }

    healthBar += "[LIGHT_GRAY]]";

    return healthBar + " " + healthText;
  }

  private String createEntityDivineFavor(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    String divineFavorColor;

    if (attributes.divineFavor <= 0) {
      divineFavorColor = "[RED]";
    } else if (attributes.divineFavor / 100 <= 0.5f) {
      divineFavorColor = "[YELLOW]";
    } else {
      divineFavorColor = "[WHITE]";
    }

    String divineFavorText = divineFavorColor + attributes.divineFavor + "[LIGHT_GRAY]/100";
    String divineFavorBar = "[LIGHT_GRAY]DF [";

    for (int i = 0; i < MathUtils.floor(100 / 10); i++) {
      if (attributes.divineFavor < (i * 10)) {
        divineFavorBar += "[DARK_GRAY]x";
      } else {
        divineFavorBar += "[WHITE]x";
      }
    }

    divineFavorBar += "[LIGHT_GRAY]]";

    return divineFavorBar + " " + divineFavorText;
  }

  private String createEntityOxygen(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    String oxygenTextColor = attributes.oxygen < attributes.maxOxygen / 2 ? "[RED]" : "[CYAN]";
    String oxygenText = oxygenTextColor + attributes.oxygen
        + "[LIGHT_GRAY]/" + attributes.maxOxygen;
    String oxygenBar = "[LIGHT_GRAY]OX [";

    for (int i = 0; i < MathUtils.floor(attributes.maxOxygen / 4); i++) {
      if (attributes.oxygen < (i * 4)) {
        oxygenBar += "[DARK_GRAY]x";
      } else {
        oxygenBar += "[CYAN]x";
      }
    }

    oxygenBar += "[LIGHT_GRAY]]";

    return oxygenBar + " " + oxygenText;
  }

  private String createEntityStatus(Entity entity) {
    Array<String> statuses = new Array<>();

    if (ComponentMappers.encumbered.has(player)) {
      statuses.add("[DARK_GRAY]ENCUMBERED[]");
    }

    if (ComponentMappers.crippled.has(entity)) {
      statuses.add("[DARK_GRAY]CRIPPLED[]");
    }

    if (ComponentMappers.bleeding.has(entity)) {
      statuses.add("[DARK_GRAY]BLEEDING[]");
    }

    if (ComponentMappers.poisoned.has(entity)) {
      statuses.add("[DARK_GRAY]POISONED[]");
    }

    if (ComponentMappers.drowning.has(entity)) {
      statuses.add("[DARK_GRAY]DROWNING[]");
    }

    if (ComponentMappers.stuck.has(entity)) {
      statuses.add("[DARK_GRAY]STUCK[]");
    }

    return statuses.toString("[LIGHT_GRAY],[] ");
  }

  private void handleFocusedAttack(String part) {
    Gdx.app.log("HudRenderer", "Focusing attack on " + part);

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

  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }
}
