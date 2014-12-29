package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.EntityHelpers;

public class PlayerInput implements InputProcessor {
  private final Main game;
  private final Map map;
  private final EntityHelpers entityHelpers;
  private final Entity player;

  public PlayerInput(Main game, Map map, EntityHelpers entityHelpers) {
    this.game = game;
    this.map = map;
    this.entityHelpers = entityHelpers;

    this.player = entityHelpers.getPlayer();
  }

  @Override
  public boolean keyDown(int keycode) {
    AttributesComponent attributes = player.getComponent(AttributesComponent.class);
    PositionComponent position = player.getComponent(PositionComponent.class);

    switch (keycode) {
      case Keys.BACKSLASH:
        game.debug ^= true;
        break;
      case Keys.Z:
        game.executeTurn = true;
        break;
      case Keys.K:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x, position.pos.y + 1));
        break;
      case Keys.U:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y + 1));
        break;
      case Keys.L:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y));
        break;
      case Keys.N:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y - 1));
        break;
      case Keys.J:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x, position.pos.y - 1));
        break;
      case Keys.B:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y - 1));
        break;
      case Keys.H:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y));
        break;
      case Keys.Y:
        moveOrAttack(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y + 1));
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

  private void moveOrAttack(int energy, Vector2 pos) {
    if (map.isWalkable(pos) && energy >= MovementComponent.COST) {
      player.add(new MovementComponent(pos));
      game.executeTurn = true;
    } else if (entityHelpers.isEnemy(map.getEntityAt(pos)) && energy >= MeleeComponent.COST) {
      player.add(new MeleeComponent(map.getEntityAt(pos)));
      game.executeTurn = true;
    }
  }
}
