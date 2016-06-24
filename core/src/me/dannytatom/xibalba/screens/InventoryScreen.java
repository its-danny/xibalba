package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;

import java.util.ArrayList;
import java.util.Objects;

public class InventoryScreen implements Screen {
  private final Main main;

  private final Stage stage;

  private final VerticalGroup inventoryGroup;
  private final ArrayList<Entity> items;
  private int selected = 0;

  /**
   * View and manage inventory.
   *
   * @param main Instance of the main class
   */
  public InventoryScreen(Main main) {
    this.main = main;

    items = main.player.getComponent(InventoryComponent.class).items;

    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    inventoryGroup = new VerticalGroup().left();

    table.add(createHeader()).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() - 20);
    table.row();
    table.add(inventoryGroup).pad(0, 10, 10, 10).left();

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(
        Colors.get("CAVE_BACKGROUND").r,
        Colors.get("CAVE_BACKGROUND").g,
        Colors.get("CAVE_BACKGROUND").b,
        Colors.get("CAVE_BACKGROUND").a
    );

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    renderInventory();

    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
      if (selected < items.size() - 1) {
        selected += 1;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
      if (selected > 0) {
        selected -= 1;
      }
    }

    if (items.size() >= 1) {
      ItemComponent item = items.get(selected).getComponent(ItemComponent.class);

      if (Gdx.input.isKeyJustPressed(Input.Keys.H) && item.actions.get("canHold")) {
        main.equipmentHelpers.holdItem(main.player, items.get(selected));
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.W) && item.actions.get("canWear")) {
        main.equipmentHelpers.wearItem(main.player, items.get(selected));
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.T) && item.actions.get("canThrow")) {
        items.get(selected).getComponent(ItemComponent.class).throwing = true;

        main.state = Main.State.TARGETING;
        main.setScreen(main.playScreen);
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
        main.equipmentHelpers.removeItem(main.player, items.get(selected));
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
        main.inventoryHelpers.dropItem(main.player, items.get(selected));
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      main.setScreen(new CharacterScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
      main.setScreen(new HelpScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      main.setScreen(main.playScreen);
    }
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  private VerticalGroup createHeader() {
    String header = "[DARK_GRAY][ ";
    header += "[CYAN]1[LIGHT_GRAY] ";
    header += main.player.getComponent(AttributesComponent.class).name;
    header += "[DARK_GRAY] | ";
    header += "[CYAN]2[WHITE] Inventory";
    header += "[DARK_GRAY] | ";
    header += "[CYAN]3[LIGHT_GRAY] Help";
    header += "[DARK_GRAY] ]";

    VerticalGroup group = new VerticalGroup().center();
    group.addActor(new Label(header, main.skin));

    return group;
  }

  private void renderInventory() {
    inventoryGroup.clear();

    for (int i = 0; i < items.size(); i++) {
      Entity entity = items.get(i);
      ItemComponent item = entity.getComponent(ItemComponent.class);

      String name;

      if (i == selected) {
        name = "[DARK_GRAY]> [WHITE]" + item.name + "[]";
      } else {
        name = "[DARK_GRAY]" + item.name + "[]";
      }

      if (main.equipmentHelpers.isEquip(main.player, entity)) {
        String location = main.equipmentHelpers.getLocation(main.player, entity);

        if (Objects.equals(location, "right hand") || Objects.equals(location, "left hand")) {
          name += " [YELLOW](holding in " + location + ")[]";
        } else {
          name += " [YELLOW](wearing on " + location + ")[]";
        }
      }

      inventoryGroup.addActor(new Label(name, main.skin));

      if (i == selected) {
        Array<String> actions = new Array<>();

        if (item.actions.get("canHold") && !main.equipmentHelpers.isEquip(main.player, entity)) {
          actions.add("[CYAN]H[]old");
        }

        if (item.actions.get("canWear") && !main.equipmentHelpers.isEquip(main.player, entity)) {
          actions.add("[CYAN]W[]ear");
        }

        if (item.actions.get("canThrow") && !main.equipmentHelpers.isEquip(main.player, entity)) {
          actions.add("[CYAN]T[]hrow");
        }

        if (item.actions.get("canUse") && !main.equipmentHelpers.isEquip(main.player, entity)) {
          actions.add("[CYAN]U[]se");
        }

        if (main.equipmentHelpers.isEquip(main.player, entity)) {
          actions.add("[CYAN]R[]emove");
        }

        if (!main.equipmentHelpers.isEquip(main.player, entity)) {
          actions.add("[CYAN]D[]rop");
        }

        Array<String> stats = new Array<>();

        if (item.attributes.get("hitDamage") != null) {
          stats.add("[DARK_GRAY]Hit Damage: [LIGHT_GRAY]" + item.attributes.get("hitDamage"));
        }

        if (item.attributes.get("throwDamage") != null) {
          stats.add("[DARK_GRAY]Throw Damage: [LIGHT_GRAY]" + item.attributes.get("throwDamage"));
        }

        if (item.attributes.get("defense") != null) {
          stats.add("[DARK_GRAY]Defense: [LIGHT_GRAY]" + item.attributes.get("defense"));
        }

        inventoryGroup.addActor(new Label("[LIGHT_GRAY]" + item.description + "[]", main.skin));
        inventoryGroup.addActor(new Label(stats.toString(" "), main.skin));
        inventoryGroup.addActor(new Label(actions.toString(" "), main.skin));
      }
    }
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
