package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;

public class UIRenderer {
  private final Stage stage;
  private final Skin skin;
  private final FPSLogger fpsLogger;
  private final ActionLog actionLog;
  private final Entity player;
  private final VerticalGroup actionList;
  private final VerticalGroup characterPanel;

  public UIRenderer(ActionLog actionLog, Entity player) {
    this.actionLog = actionLog;
    this.player = player;

    stage = new Stage(new ScreenViewport());
    skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    fpsLogger = new FPSLogger();

    skin.getFont("default-font").setMarkupEnabled(true);

    actionList = new VerticalGroup();
    actionList.setWidth(300);
    actionList.pad(10);
    actionList.left();
    actionList.reverse();

    characterPanel = new VerticalGroup();
    characterPanel.setWidth(300);
    characterPanel.pad(10);
    characterPanel.left();
    characterPanel.setPosition(Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight());

    stage.addActor(actionList);
    stage.addActor(characterPanel);
  }

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

    for (int i = 0; i < actionLog.items.size(); i++) {
      Label label = new Label(actionLog.items.get(i), skin);
      label.setColor(1, 1, 1, alpha);

      actionList.addActor(label);

      alpha -= .15;
    }
  }

  private void renderCharacterPanel() {
    InventoryComponent inventory = player.getComponent(InventoryComponent.class);

    characterPanel.clearChildren();

    for (int i = 0; i < inventory.items.size(); i++) {
      ItemComponent item = inventory.items.get(i).getComponent(ItemComponent.class);
      String name = "[CYAN]" + item.identifier + "[] " + item.name + (item.equipped ? " (equipped) " : " ")
          + "+" + item.attributes.get("damage");

      Label nameLabel = new Label(name, skin);
      nameLabel.setScale(.75f);
      characterPanel.addActor(nameLabel);

      Label descLabel = new Label(item.description, skin);
      descLabel.setScale(.5f);
      descLabel.setColor(Color.DARK_GRAY);
      characterPanel.addActor(descLabel);

      if (item.lookingAt) {
        String actions = "";

        if (item.actions.get("canThrow")) {
          actions += "[WHITE]t[LIGHT_GRAY]hrow   ";
        }

        if (item.actions.get("canWear")) {
          actions += "[WHITE]w[LIGHT_GRAY]ear   ";
        }

        if (item.actions.get("canWield")) {
          actions += "wi[WHITE]e[LIGHT_GRAY]ld   ";
        }

        if (item.actions.get("canUse")) {
          actions += "[WHITE]u[LIGHT_GRAY]se   ";
        }

        Label actionsLabel = new Label(actions, skin);
        actionsLabel.setScale(.5f);
        characterPanel.addActor(actionsLabel);
      }
    }
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  public void dispose() {
    stage.dispose();
  }
}
