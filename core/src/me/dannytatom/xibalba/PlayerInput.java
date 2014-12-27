package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.MovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;

public class PlayerInput implements InputProcessor {
  private final Main game;
  private final Entity player;

  public PlayerInput(Main game, Entity player) {
    this.game = game;
    this.player = player;
  }

  @Override
  public boolean keyDown(int keycode) {
    MovementComponent movement = player.getComponent(MovementComponent.class);
    PositionComponent position = player.getComponent(PositionComponent.class);

    switch (keycode) {
      case Keys.BACKSLASH:
        game.debug ^= true;
        break;
      case Keys.Z:
        game.executeTurn = true;
        break;
      case Keys.K:
        movement.pos = new Vector2(position.pos.x, position.pos.y + 1);
        game.executeTurn = true;
        break;
      case Keys.U:
        movement.pos = new Vector2(position.pos.x + 1, position.pos.y + 1);
        game.executeTurn = true;
        break;
      case Keys.L:
        movement.pos = new Vector2(position.pos.x + 1, position.pos.y);
        game.executeTurn = true;
        break;
      case Keys.N:
        movement.pos = new Vector2(position.pos.x + 1, position.pos.y - 1);
        game.executeTurn = true;
        break;
      case Keys.J:
        movement.pos = new Vector2(position.pos.x, position.pos.y - 1);
        game.executeTurn = true;
        break;
      case Keys.B:
        movement.pos = new Vector2(position.pos.x - 1, position.pos.y - 1);
        game.executeTurn = true;
        break;
      case Keys.H:
        movement.pos = new Vector2(position.pos.x - 1, position.pos.y);
        game.executeTurn = true;
        break;
      case Keys.Y:
        movement.pos = new Vector2(position.pos.x - 1, position.pos.y + 1);
        game.executeTurn = true;
        break;
      default:
    }

    return true;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
