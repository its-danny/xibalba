package me.dannytatom.xibalba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashMap;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.abilities.Ability;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

import org.apache.commons.lang3.text.WordUtils;

public class AbilitiesScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private final Table table;
  private final VerticalGroup abilitiesGroup;
  private final HashMap<String, Ability> abilities;
  private final PlayerComponent playerDetails;

  /**
   * View and use your abilities.
   *
   * @param main Instance of Main
   */
  public AbilitiesScreen(Main main) {
    this.main = main;

    abilities = ComponentMappers.abilities.get(WorldManager.player).abilities;
    playerDetails = ComponentMappers.player.get(WorldManager.player);

    stage = new Stage(new FitViewport(960, 540));

    table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    Table titleTable = new Table();

    HorizontalGroup titleGroup = new HorizontalGroup();
    titleGroup.space(10);
    titleTable.add(titleGroup).pad(10).width(Gdx.graphics.getWidth() - 20);

    ActionButton closeButton = new ActionButton("Q", null);
    closeButton.setKeys(Input.Keys.Q);
    closeButton.setAction(table, () -> main.setScreen(Main.playScreen));
    titleGroup.addActor(closeButton);

    Label title = new Label("Abilities", Main.skin);
    titleGroup.addActor(title);

    abilitiesGroup = new VerticalGroup().top().left().columnLeft();

    Table abilitiesTable = new Table();
    abilitiesTable.add(abilitiesGroup).pad(10).top().left().width(Gdx.graphics.getWidth() / 2);

    table.add(titleTable);
    table.row();
    table.add(abilitiesTable).left();

    setupAbilities();

    Gdx.input.setInputProcessor(stage);
    stage.setKeyboardFocus(table);
  }

  @Override
  public void show() {

  }

  private void setupAbilities() {
    abilitiesGroup.clear();

    int i = 0;
    for (Ability ability : abilities.values()) {
      if (ability.type == Ability.Type.PASSIVE) {
        abilitiesGroup.addActor(new Label(ability.name + " [LIGHT_GRAY]Passive", Main.skin));
      } else if (ability.counter < ability.recharge) {
        abilitiesGroup.addActor(new Label(ability.name, Main.skin));
      } else {
        // If you look at the docs for Input.Keys, number keys are offset by 7
        // (e.g. 0 = 7, 1 = 8, etc)

        ActionButton button = new ActionButton(
            i + 1,
            ability.name + " [LIGHT_GRAY]Usable every " + ability.recharge + " turns"
        );

        button.setKeys(i + 8);
        button.setAction(table, () -> {
          if (WorldManager.state != WorldManager.State.FOCUSED) {
            if (ability.targetRequired && WorldManager.state != WorldManager.State.TARGETING) {
              playerDetails.targetingAbility = ability;
              WorldManager.inputHelpers.startTargeting(WorldManager.TargetState.ABILITY);
            } else {
              ability.act(WorldManager.player, WorldManager.player);
            }

            main.setScreen(Main.playScreen);
          }
        });

        abilitiesGroup.addActor(button);

        i++;
      }

      abilitiesGroup.addActor(new Label(
          "[DARK_GRAY]" + WordUtils.wrap(ability.description, 140), Main.skin
      ));

      if (ability.counter < ability.recharge) {
        abilitiesGroup.addActor(new Label(
            "[DARK_GRAY]" + (ability.recharge - ability.counter) + " turns left", Main.skin
        ));
      }

      abilitiesGroup.addActor(new Label("", Main.skin));
    }
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(
        Colors.get("screenBackground").r,
        Colors.get("screenBackground").g,
        Colors.get("screenBackground").b,
        Colors.get("screenBackground").a
    );

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
