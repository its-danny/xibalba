package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
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
  private final ActionLog actionLog;
  private final Entity player;
  private final VerticalGroup actionList;
  private final VerticalGroup inventoryList;

  public UIRenderer(ActionLog actionLog, Entity player) {
    this.actionLog = actionLog;
    this.player = player;

    stage = new Stage(new ScreenViewport());
    skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

    actionList = new VerticalGroup();
    actionList.setWidth(300);
    actionList.pad(10);
    actionList.left();
    actionList.reverse();

    inventoryList = new VerticalGroup();
    inventoryList.setWidth(300);
    inventoryList.pad(10);
    inventoryList.right();
    inventoryList.reverse();
    inventoryList.setPosition(Gdx.graphics.getWidth() - 300, 0);

    stage.addActor(actionList);
    stage.addActor(inventoryList);
  }

  public void render(float delta) {
    InventoryComponent inventory = player.getComponent(InventoryComponent.class);
    float alpha = 1;

    actionList.clearChildren();

    for (int i = 0; i < actionLog.items.size(); i++) {
      Label label = new Label(actionLog.items.get(i), skin);
      label.setColor(1, 1, 1, alpha);

      actionList.addActor(label);

      alpha -= .15;
    }

    inventoryList.clearChildren();

    for (int i = 0; i < inventory.items.size(); i++) {
      ItemComponent item = inventory.items.get(i).getComponent(ItemComponent.class);
      String string = item.name + (item.equipped ? " (equipped)" : "");
      Label label = new Label(string, skin);

      inventoryList.addActor(label);
    }

    stage.act(delta);
    stage.draw();
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  public void dispose() {
    stage.dispose();
  }
}
