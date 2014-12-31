package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;

public class PlayerInput implements InputProcessor {
  private final Main game;
  private final ActionLog actionLog;
  private final Map map;
  private final EntityHelpers entityHelpers;
  private final InventoryHelpers inventoryHelpers;
  private final Entity player;

  public PlayerInput(Main game, ActionLog actionLog, Map map, EntityHelpers entityHelpers, InventoryHelpers inventoryHelpers) {
    this.game = game;
    this.actionLog = actionLog;
    this.map = map;
    this.entityHelpers = entityHelpers;
    this.inventoryHelpers = inventoryHelpers;

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
        doWhatNow(attributes.energy, new Vector2(position.pos.x, position.pos.y + 1));
        break;
      case Keys.U:
        doWhatNow(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y + 1));
        break;
      case Keys.L:
        doWhatNow(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y));
        break;
      case Keys.N:
        doWhatNow(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y - 1));
        break;
      case Keys.J:
        doWhatNow(attributes.energy, new Vector2(position.pos.x, position.pos.y - 1));
        break;
      case Keys.B:
        doWhatNow(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y - 1));
        break;
      case Keys.H:
        doWhatNow(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y));
        break;
      case Keys.Y:
        doWhatNow(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y + 1));
        break;
      case Keys.E:
        inventoryHelpers.wieldItem();
        break;
      default:
        if (inventoryHelpers.findItem(keycode) != null) {
          inventoryHelpers.toggleShowItem(inventoryHelpers.findItem(keycode));
        }
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

  private void doWhatNow(int energy, Vector2 pos) {
    if (map.isWalkable(pos)) {
      if (energy >= MovementComponent.COST) {
        player.add(new MovementComponent(pos));

        game.executeTurn = true;
      }
    } else {
      Entity thing = map.getEntityAt(pos);

      if (entityHelpers.isItem(thing) && energy >= MovementComponent.COST) {
        inventoryHelpers.addItem(thing);
        player.add(new MovementComponent(pos));

        actionLog.add("You pick up a " + thing.getComponent(ItemComponent.class).name);

        game.executeTurn = true;
      } else if (entityHelpers.isEnemy(thing) && energy >= MeleeComponent.COST) {
        player.add(new MeleeComponent(thing));

        game.executeTurn = true;
      }
    }
  }
}
