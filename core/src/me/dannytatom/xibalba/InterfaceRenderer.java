package me.dannytatom.xibalba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;

public class InterfaceRenderer {
  private final Main main;
  private final Stage stage;
  private final Skin skin;
  private final FPSLogger fpsLogger;
  private final VerticalGroup actionList;
  private final VerticalGroup characterPanel;

  /**
   * Renders the UI.
   *
   * @param main Instance of the main class
   */
  public InterfaceRenderer(Main main) {
    this.main = main;

    stage = new Stage(new ScreenViewport());
    fpsLogger = new FPSLogger();

    skin = new Skin();
    skin.add("Inconsolata", main.font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));
    skin.getFont("default-font").getData().markupEnabled = true;

    actionList = new VerticalGroup();
    actionList.setWidth(300);
    actionList.pad(10);
    actionList.left();
    actionList.setPosition(0, Gdx.graphics.getHeight());

    characterPanel = new VerticalGroup();
    characterPanel.setWidth(300);
    characterPanel.pad(10);
    characterPanel.left();
    characterPanel.setPosition(Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight());

    stage.addActor(actionList);
    stage.addActor(characterPanel);
  }

  /**
   * Render shit.
   *
   * @param delta AIRLINES
   */
  public void render(float delta) {
    fpsLogger.log();

    renderActionList();
    renderCharacterPanel();

    stage.act(delta);
    stage.draw();
  }

  private void renderActionList() {
    float alpha = 1;

    actionList.clearChildren();

    for (int i = 0; i < main.log.items.size(); i++) {
      Label label = new Label(main.log.items.get(i), skin);
      label.setColor(1, 1, 1, alpha);

      actionList.addActor(label);

      alpha -= .15;
    }
  }

  private void renderCharacterPanel() {
    characterPanel.clearChildren();

    characterPanel.addActor(
        new Label(main.player.getComponent(AttributesComponent.class).name, skin)
    );
    characterPanel.addActor(renderInventory());
  }

  private VerticalGroup renderInventory() {
    InventoryComponent inventory = main.player.getComponent(InventoryComponent.class);

    VerticalGroup group = new VerticalGroup();
    group.padBottom(10);
    group.left();

    for (int i = 0; i < inventory.items.size(); i++) {
      ItemComponent item = inventory.items.get(i).getComponent(ItemComponent.class);
      String name = "[CYAN]" + item.identifier + "[] "
          + (item.lookingAt ? "[WHITE]" : "[LIGHT_GRAY]") + item.name + (item.lookingAt ? "[]" : "")
          + (item.equipped ? " [LIGHT_GRAY](wielding)[] " : " ");

      Label nameLabel = new Label(name, skin);
      group.addActor(nameLabel);

      Label descLabel = new Label(item.description, skin);
      descLabel.setScale(.5f);
      descLabel.setColor(Color.DARK_GRAY);
      group.addActor(descLabel);

      if (item.lookingAt) {
        if (item.attributes != null) {
          String stats = "[RED]+" + item.attributes.get("damage");
          Label statsLabel = new Label(stats, skin);
          statsLabel.setScale(.5f);
          group.addActor(statsLabel);
        }

        String actions = "";

        if (item.actions.get("canThrow")) {
          actions += "[WHITE]t[LIGHT_GRAY]hrow ";
        }

        if (item.actions.get("canWear")) {
          actions += "[WHITE]w[LIGHT_GRAY]ear ";
        }

        if (item.actions.get("canWield")) {
          actions += "wi[WHITE]e[LIGHT_GRAY]ld ";
        }

        if (item.actions.get("canUse")) {
          actions += "[WHITE]u[LIGHT_GRAY]se ";
        }

        actions += "[WHITE]d[LIGHT_GRAY]rop";

        Label actionsLabel = new Label(actions, skin);
        actionsLabel.setScale(.5f);
        group.addActor(actionsLabel);
      }
    }

    return group;
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  public void dispose() {
    stage.dispose();
  }
}
