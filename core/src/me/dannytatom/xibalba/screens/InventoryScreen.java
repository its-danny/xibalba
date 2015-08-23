package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;

import java.util.ArrayList;

public class InventoryScreen implements Screen {
  private final Main main;

  private Skin skin;
  private Stage stage;
  private VerticalGroup inventoryGroup;
  private VerticalGroup detailsGroup;
  private InventoryComponent inventory;
  private int currentIndex;

  /**
   * View and manage inventory.
   *
   * @param main Instance of the main class
   */
  public InventoryScreen(Main main) {
    this.main = main;

    stage = new Stage();

    skin = new Skin();
    skin.add("Inconsolata", this.main.font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));
    skin.getFont("default-font").getData().markupEnabled = true;

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    currentIndex = 0;

    inventory = this.main.player.getComponent(InventoryComponent.class);
    inventoryGroup = new VerticalGroup().left();
    detailsGroup = new VerticalGroup().left();

    table.add(inventoryGroup).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);
    table.add(detailsGroup).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 2 - 20);

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    inventoryGroup.clear();
    detailsGroup.clear();

    ArrayList<Entity> items = inventory.items;

    for (int i = 0; i < items.size(); i++) {
      Entity entity = items.get(i);
      ItemComponent item = entity.getComponent(ItemComponent.class);

      inventoryGroup.addActor(
          new Label(
              "[CYAN]" + item.identifier + " "
                  + (i == currentIndex ? "[WHITE]" : "[LIGHT_GRAY]") + item.name
                  + " [LIGHT_GRAY][" + item.type + "]"
                  + (item.equipped ? " (wielded)" : ""),
              skin
          )
      );

      if (i == currentIndex) {
        detailsGroup.addActor(
            new Label((i == currentIndex ? "[LIGHT_GRAY]" : "[DARK_GRAY]") + item.description, skin)
        );

        detailsGroup.addActor(
            new Label(
                (i == currentIndex ? "[LIGHT_GRAY]" : "[DARK_GRAY]")
                    + (item.attributes.get("damage") != null ? ("[RED]" + item.attributes.get("damage")) : ""),
                skin
            )
        );

        detailsGroup.addActor(
            new Label(
                (item.actions.get("canWield") && !item.equipped ? "wi[CYAN]e[]ld " : "")
                    + (item.equipped ? "unwi[CYAN]e[]ld " : "")
                    + (item.actions.get("canThrow") ? "[CYAN]t[]hrow " : "")
                    + "[CYAN]d[]rop",
                skin
            )
        );
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
      if (currentIndex == inventoryGroup.getChildren().size - 1) {
        currentIndex = 0;
      } else {
        currentIndex += 1;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
      if (currentIndex == 0) {
        currentIndex = inventoryGroup.getChildren().size - 1;
      } else {
        currentIndex -= 1;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (items.get(currentIndex).getComponent(ItemComponent.class).actions.get("canWield")) {
        if (items.get(currentIndex) == main.inventoryHelpers.getWieldedItem()) {
          main.inventoryHelpers.unwieldItem(items.get(currentIndex));
        } else {
          main.inventoryHelpers.wieldItem(items.get(currentIndex));
        }
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
      ItemComponent item = items.get(currentIndex).getComponent(ItemComponent.class);

      if (item.actions.get("canThrow") && main.state == Main.State.PLAYING) {
        item.throwing = true;

        main.state = Main.State.TARGETING;
        main.setScreen(main.playScreen);
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
      main.inventoryHelpers.dropItem(items.get(currentIndex));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      main.setScreen(main.playScreen);
    }
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
