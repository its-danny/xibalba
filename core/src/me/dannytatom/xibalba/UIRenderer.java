package me.dannytatom.xibalba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class UIRenderer {
  private final Stage stage;
  private final Skin skin;
  private final ActionLog logger;
  private final VerticalGroup list;

  public UIRenderer(ActionLog logger) {
    this.logger = logger;

    stage = new Stage(new ScreenViewport());
    skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

    list = new VerticalGroup();
    list.setDebug(true);
    list.setWidth(300);
    list.pad(10);
    list.left();
    list.reverse();

    stage.addActor(list);
  }

  public void render(float delta) {
    float alpha = 1;

    list.clearChildren();

    for (int i = 0; i < logger.items.size(); i++) {
      Label label = new Label(logger.items.get(i), skin);
      label.setColor(1, 1, 1, alpha);

      list.addActor(label);

      alpha -= .15;
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
