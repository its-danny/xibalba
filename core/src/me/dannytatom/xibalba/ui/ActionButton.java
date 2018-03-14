package me.dannytatom.xibalba.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

import me.dannytatom.xibalba.Main;

public class ActionButton extends TextButton {
  private final String letter;
  private List<Integer> keys = null;

  /**
   * Create an action button with letter and text.
   *
   * @param letter The key you press to do the action
   * @param text   Button text
   */
  public ActionButton(String letter, String text) {
    super(null, Main.skin);

    this.letter = letter;

    setText(createText(text));
    pad(5);
  }

  /**
   * Create an action button with number and text.
   *
   * @param number The key you press to do the action
   * @param text   Button text
   */
  public ActionButton(int number, String text) {
    super(null, Main.skin);

    this.letter = number + "";

    setText(createText(text));
    pad(5);
  }

  /**
   * Set action for this button to perform.
   *
   * @param parent The actor to attach the listener to
   * @param action The action
   */
  public void setAction(Actor parent, Runnable action) {
    ClickListener clickListener = new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);

        action.run();
      }
    };

    addListener(clickListener);

    InputListener inputListener = new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (keys != null && keys.contains(keycode)) {
          setColor(1, 1, 1, .5f);

          return true;
        }

        return false;
      }

      @Override
      public boolean keyUp(InputEvent event, int keycode) {
        if (keys != null && keys.contains(keycode)) {
          setColor(1, 1, 1, 1);

          action.run();

          return true;
        }

        return false;
      }
    };

    parent.addListener(inputListener);
  }

  /**
   * Set the input keys associated with this action.
   *
   * @param keys List of input keys
   */
  public void setKeys(int... keys) {
    this.keys = new ArrayList<>();

    for (int key : keys) {
      this.keys.add(key);
    }
  }

  private String createText(String text) {
    if (letter != null && text != null) {
      return "[DARK_GRAY][ [CYAN]" + letter + "[DARK_GRAY] ][WHITE] " + text;
    } else if (text == null) {
      return "[DARK_GRAY][ [CYAN]" + letter + "[DARK_GRAY] ]";
    } else {
      return text;
    }
  }
}
