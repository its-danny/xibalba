package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;

import java.util.ArrayList;

public class InventoryScreen implements Screen {
  private final Main main;

  private Stage stage;

  private ArrayList<Entity> items;
  private int selected = 0;
  private VerticalGroup inventoryGroup;

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
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    refillInventory();

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

    if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
      items.get(selected).getComponent(ItemComponent.class).throwing = true;

      main.state = Main.State.TARGETING;
      main.setScreen(main.playScreen);
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      main.setScreen(new CharacterScreen(main));
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
    header += "[DARK_GRAY] ]";

    VerticalGroup group = new VerticalGroup().center();
    group.addActor(new Label(header, main.skin));

    return group;
  }

  private void refillInventory() {
    inventoryGroup.clear();

    for (int i = 0; i < items.size(); i++) {
      Entity entity = items.get(i);
      ItemComponent item = entity.getComponent(ItemComponent.class);

      if (i == selected) {
        inventoryGroup.addActor(new Label("[DARK_GRAY]> [WHITE]" + item.name + "[]", main.skin));
        inventoryGroup.addActor(new Label("[LIGHT_GRAY]" + item.description + "[]", main.skin));
        inventoryGroup.addActor(new Label(
            "[RED]T[]hrow", main.skin));
      } else {
        inventoryGroup.addActor(new Label("[DARK_GRAY]" + item.name + "[]", main.skin));
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
