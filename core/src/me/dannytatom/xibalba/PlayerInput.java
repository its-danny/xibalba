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
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.screens.InventoryScreen;
import me.dannytatom.xibalba.screens.MainMenuScreen;
import me.dannytatom.xibalba.screens.SkillsScreen;

public class PlayerInput implements InputProcessor {
  private final Main main;
  private final Map map;

  /**
   * Handle player input.
   *
   * @param main Instance of the main class
   * @param map  Map
   */
  public PlayerInput(Main main, Map map) {
    this.main = main;
    this.map = map;
  }

  @Override
  public boolean keyDown(int keycode) {
    AttributesComponent attributes = main.player.getComponent(AttributesComponent.class);
    PositionComponent position = main.player.getComponent(PositionComponent.class);

    switch (keycode) {
      case Keys.BACKSLASH:
        main.debug ^= true;
        break;
      case Keys.Z:
        main.executeTurn = true;
        break;
      case Keys.S:
        main.setScreen(new SkillsScreen(main));
        break;
      case Keys.I:
        main.setScreen(new InventoryScreen(main));
        break;
      case Keys.K:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(0, 1));
        }
        break;
      case Keys.U:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, 1));
        }
        break;
      case Keys.L:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, 0));
        }
        break;
      case Keys.N:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, -1));
        }
        break;
      case Keys.J:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(0, -1));
        }
        break;
      case Keys.B:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, -1));
        }
        break;
      case Keys.H:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, 0));
        }
        break;
      case Keys.Y:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, 1));
        }
        break;
      case Keys.Q:
        if (main.state == Main.State.PLAYING) {
          main.getScreen().dispose();
          main.setScreen(new MainMenuScreen(main));
        } else if (main.state == Main.State.TARGETING) {
          map.target = null;
          map.targetingPath = null;

          main.state = Main.State.PLAYING;
        }
        break;
      case Keys.SPACE:
        if (main.state == Main.State.TARGETING) {
          if (map.targetingPath != null && attributes.energy >= RangeComponent.COST) {
            main.player.add(new RangeComponent(map.target));

            main.executeTurn = true;
          }

          map.target = null;
          map.targetingPath = null;

          main.state = Main.State.PLAYING;
        }
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

  /**
   * Handles player movement.
   *
   * <p>One of these 3 happens when attempting to move into a cell:
   * - Actually move
   * - Pick up item on the cell, then move
   * - Melee attack enemy in cell
   *
   * @param energy How much energy the player has
   * @param pos    The position we're attempting to move to
   */
  private void handleMovement(int energy, Vector2 pos) {
    if (map.isWalkable(pos)) {
      if (energy >= MovementComponent.COST) {
        main.player.add(new MovementComponent(pos));

        main.executeTurn = true;
      }
    } else {
      Entity thing = map.getEntityAt(pos);

      if (main.entityHelpers.isItem(thing) && energy >= MovementComponent.COST) {
        if (main.inventoryHelpers.addItem(thing)) {
          main.log.add("You pick up a " + thing.getComponent(ItemComponent.class).name);
        }

        main.player.add(new MovementComponent(pos));

        main.executeTurn = true;
      } else if (main.entityHelpers.isEnemy(thing) && energy >= MeleeComponent.COST) {
        main.player.add(new MeleeComponent(thing));

        main.executeTurn = true;
      }
    }
  }

  private void handleTargeting(Vector2 pos) {
    map.createTargetingPath(main.player.getComponent(PositionComponent.class).pos, pos);
  }
}
