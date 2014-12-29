package me.dannytatom.xibalba;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class UIRenderer {
  private final Stage stage;

  public UIRenderer() {
    stage = new Stage(new ScreenViewport());
  }

  public void render(float delta) {
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
