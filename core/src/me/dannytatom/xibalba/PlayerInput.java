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
import me.dannytatom.xibalba.screens.CharacterScreen;
import me.dannytatom.xibalba.screens.HelpScreen;
import me.dannytatom.xibalba.screens.InventoryScreen;
import me.dannytatom.xibalba.screens.PauseScreen;

public class PlayerInput implements InputProcessor {
  private final Main main;

  private boolean holdingShift = false;

  /**
   * Handle player input.
   *
   * @param main Instance of the main class
   */
  public PlayerInput(Main main) {
    this.main = main;
  }

  @Override
  public boolean keyDown(int keycode) {
    AttributesComponent attributes = main.player.getComponent(AttributesComponent.class);
    PositionComponent position = main.player.getComponent(PositionComponent.class);

    switch (keycode) {
      case Keys.Z:
        main.executeTurn = true;
        break;
      case Keys.C:
        main.setScreen(new CharacterScreen(main));
        break;
      case Keys.I:
        main.setScreen(new InventoryScreen(main));
        break;
      case Keys.K:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(0, 1));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(0, 1));
        }
        break;
      case Keys.U:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, 1));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(1, 1));
        }
        break;
      case Keys.L:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, 0));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(1, 0));
        }
        break;
      case Keys.N:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, -1));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(1, -1));
        }
        break;
      case Keys.J:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(0, -1));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(0, -1));
        }
        break;
      case Keys.B:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, -1));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(-1, -1));
        }
        break;
      case Keys.H:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, 0));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(-1, 0));
        }
        break;
      case Keys.Y:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, 1));
        } else if (main.state == Main.State.SEARCHING) {
          handleSearching(new Vector2(-1, 1));
        }
        break;
      case Keys.S:
        if (main.state == Main.State.PLAYING) {
          main.state = Main.State.SEARCHING;
        }
        break;
      case Keys.R:
        if (main.state == Main.State.PLAYING) {
          Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);

          if (primaryWeapon != null) {
            ItemComponent itemComponent = primaryWeapon.getComponent(ItemComponent.class);

            if (itemComponent.usesAmmunition) {
              if (main.inventoryHelpers.hasAmmunitionOfType(main.player, itemComponent.ammunitionType)) {
                main.state = Main.State.TARGETING;
              } else {
                main.log.add("You aren't carrying any ammunition for this");
              }
            }
          }
        }
        break;
      case Keys.Q:
        if (main.state == Main.State.SEARCHING) {
          main.getCurrentMap().target = null;
          main.getCurrentMap().searchingPath = null;

          main.state = Main.State.PLAYING;
        } else if (main.state == Main.State.TARGETING) {
          main.getCurrentMap().target = null;
          main.getCurrentMap().targetingPath = null;

          main.state = Main.State.PLAYING;
        }
        break;
      case Keys.SPACE:
        if (main.state == Main.State.TARGETING) {
          if (main.getCurrentMap().targetingPath != null && attributes.energy >= RangeComponent.COST) {
            Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);
            Entity item = null;
            String skill = null;

            if (primaryWeapon != null && primaryWeapon.getComponent(ItemComponent.class).usesAmmunition) {
              item = main.inventoryHelpers.getAmmunitionOfType(main.player, primaryWeapon.getComponent(ItemComponent.class).ammunitionType);
              skill = primaryWeapon.getComponent(ItemComponent.class).skill;
            } else {
              item = main.inventoryHelpers.getThrowingItem(main.player);
              skill = "throwing";
            }

            main.player.add(new RangeComponent(main.getCurrentMap().target, item, skill));

            main.executeTurn = true;
          }

          main.getCurrentMap().target = null;
          main.getCurrentMap().targetingPath = null;

          main.state = Main.State.PLAYING;
        }
        break;
      case Keys.SLASH:
        if (holdingShift) {
          main.setScreen(new HelpScreen(main));
        }
        break;
      case Keys.SHIFT_LEFT:
      case Keys.SHIFT_RIGHT:
        holdingShift = true;
        break;
      case Keys.ESCAPE:
        main.setScreen(new PauseScreen(main));
      default:
    }

    return true;
  }

  @Override
  public boolean keyUp(int keycode) {
    switch (keycode) {
      case Keys.SHIFT_LEFT:
      case Keys.SHIFT_RIGHT:
        holdingShift = false;
        break;
    }

    return true;
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
   * <p>
   * <p>One of these 3 happens when attempting to move into a cell:
   * - Actually move
   * - Pick up item on the cell, then move
   * - Melee attack enemy in cell
   *
   * @param energy How much energy the player has
   * @param pos    The position we're attempting to move to
   */
  private void handleMovement(int energy, Vector2 pos) {
    if (main.getCurrentMap().isWalkable(pos)) {
      if (energy >= MovementComponent.COST) {
        main.player.add(new MovementComponent(pos));

        main.executeTurn = true;
      }
    } else {
      Entity thing = main.getCurrentMap().getEntityAt(pos);

      if (main.entityHelpers.isItem(thing) && energy >= MovementComponent.COST) {
        if (main.inventoryHelpers.addItem(main.player, thing)) {
          main.log.add("You pick up a " + thing.getComponent(ItemComponent.class).name);
        }

        main.player.add(new MovementComponent(pos));

        main.executeTurn = true;
      } else if (main.entityHelpers.isEnemy(thing) && energy >= MeleeComponent.COST) {
        main.player.add(new MeleeComponent(thing));

        main.executeTurn = true;
      } else if (main.entityHelpers.isExit(thing) && energy >= MovementComponent.COST) {
        // TODO: Switch maps
      }
    }
  }

  private void handleTargeting(Vector2 pos) {
    main.getCurrentMap().createTargetingPath(main.player.getComponent(PositionComponent.class).pos, pos);
  }

  private void handleSearching(Vector2 pos) {
    main.getCurrentMap().createSearchingPath(main.player.getComponent(PositionComponent.class).pos, pos);
  }
}
