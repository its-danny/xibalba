package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.screens.CharacterScreen;
import me.dannytatom.xibalba.screens.HelpScreen;
import me.dannytatom.xibalba.screens.PauseScreen;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class PlayerInput implements InputProcessor {
  private final Main main;
  private final OrthographicCamera worldCamera;

  private boolean holdingShift = false;

  /**
   * Handle player input.
   *
   * @param main Instance of the main class
   */
  public PlayerInput(Main main, OrthographicCamera worldCamera) {
    this.main = main;
    this.worldCamera = worldCamera;
  }

  @Override
  public boolean keyDown(int keycode) {
    AttributesComponent attributes = ComponentMappers.attributes.get(main.player);
    PositionComponent position = ComponentMappers.position.get(main.player);
    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);

    switch (keycode) {
      case Keys.Z:
        main.executeTurn = true;
        break;
      case Keys.C:
        main.setScreen(new CharacterScreen(main));
        break;
      case Keys.SLASH:
        if (holdingShift) {
          main.setScreen(new HelpScreen(main));
        }
        break;
      case Keys.K:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(0, 1));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(0, 1), false);
        }
        break;
      case Keys.U:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, 1));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(1, 1), false);
        }
        break;
      case Keys.L:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, 0));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(1, 0), false);
        }
        break;
      case Keys.N:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x + 1, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(1, -1));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(1, -1), false);
        }
        break;
      case Keys.J:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(0, -1));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(0, -1), false);
        }
        break;
      case Keys.B:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y - 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, -1));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(-1, -1), false);
        }
        break;
      case Keys.H:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, 0));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(-1, 0), false);
        }
        break;
      case Keys.Y:
        if (main.state == Main.State.PLAYING) {
          handleMovement(attributes.energy, new Vector2(position.pos.x - 1, position.pos.y + 1));
        } else if (main.state == Main.State.TARGETING) {
          handleTargeting(new Vector2(-1, 1));
        } else if (main.state == Main.State.LOOKING) {
          handleLooking(new Vector2(-1, 1), false);
        }
        break;
      case Keys.S:
        if (main.state == Main.State.PLAYING) {
          main.state = Main.State.LOOKING;
        }
        break;
      case Keys.R:
        if (main.state == Main.State.PLAYING) {
          Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);

          if (primaryWeapon != null) {
            ItemComponent itemDetails = ComponentMappers.item.get(primaryWeapon);

            if (itemDetails.usesAmmunition) {
              if (main.inventoryHelpers.hasAmmunitionOfType(main.player,
                  itemDetails.ammunitionType)) {
                main.state = Main.State.TARGETING;
              } else {
                main.log.add("You aren't carrying any ammunition for this");
              }
            } else {
              main.log.add("This weapon doesn't take ammunition");
            }
          }
        }
        break;
      case Keys.T: {
        if (main.state == Main.State.PLAYING) {
          Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);

          if (primaryWeapon != null) {
            ItemComponent itemDetails = ComponentMappers.item.get(primaryWeapon);

            if (itemDetails.actions.get("canThrow")) {
              itemDetails.throwing = true;

              main.state = Main.State.TARGETING;
            } else {
              main.log.add("You can't throw that");
            }
          }
        }
        break;
      }
      case Keys.D: {
        if (main.state == Main.State.PLAYING) {
          Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);

          if (primaryWeapon != null) {
            main.inventoryHelpers.dropItem(main.player, primaryWeapon);
          }
        }
        break;
      }
      case Keys.Q:
        playerDetails.target = null;
        playerDetails.lookingPath = null;
        playerDetails.targetingPath = null;

        if (main.state == Main.State.MOVING) {
          main.player.remove(MouseMovementComponent.class);
          main.player.remove(MovementComponent.class);
        }

        main.state = Main.State.PLAYING;
        break;
      case Keys.SPACE:
        if (main.state == Main.State.TARGETING) {
          if (main.inventoryHelpers.getThrowingItem(main.player) == null) {
            handleRange();
          } else {
            handleThrow();
          }
        }
        break;
      case Keys.SHIFT_LEFT:
      case Keys.SHIFT_RIGHT:
        holdingShift = true;
        break;
      case Keys.ESCAPE:
        main.setScreen(new PauseScreen(main));
        break;
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
      default:
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
    if (main.state == Main.State.PLAYING) {
      Vector2 mousePosition = main.mousePositionToWorld(worldCamera);

      if (holdingShift) {
        Entity enemy = main.entityHelpers.getEnemyAt(mousePosition);

        if (enemy != null && main.mapHelpers.isNearPlayer(enemy, 1)) {
          ComponentMappers.player.get(main.player).focusedAction =
              PlayerComponent.FocusedAction.MELEE;
          main.state = Main.State.FOCUSED;
          main.focusedEntity = enemy;
        }

        return true;
      } else {
        if (ComponentMappers.mouseMovement.get(main.player) == null) {
          if (main.mapHelpers.cellExists(mousePosition)
              && !main.mapHelpers.getCell(mousePosition.x, mousePosition.y).hidden) {
            main.player.add(new MouseMovementComponent());

            main.state = Main.State.MOVING;
            main.executeTurn = true;

            return true;
          }
        }
      }
    } else if (main.state == Main.State.TARGETING) {
      if (main.inventoryHelpers.getThrowingItem(main.player) == null) {
        handleRange();
      } else {
        handleThrow();
      }
    }

    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);
    PositionComponent playerPosition = ComponentMappers.position.get(main.player);
    Vector2 mousePosition = main.mousePositionToWorld(worldCamera);
    Vector2 relativeToPlayer = mousePosition.cpy().sub(playerPosition.pos);

    if (main.state == Main.State.PLAYING) {
      playerDetails.target = null;
      playerDetails.lookingPath = null;

      if (main.mapHelpers.cellExists(mousePosition)) {
        handleLooking(relativeToPlayer, true);

        return true;
      }
    } else if (main.state == Main.State.LOOKING) {
      playerDetails.target = null;
      playerDetails.lookingPath = null;

      if (main.mapHelpers.cellExists(mousePosition)) {
        handleLooking(relativeToPlayer, false);

        return true;
      }
    } else if (main.state == Main.State.TARGETING) {
      playerDetails.target = null;
      playerDetails.targetingPath = null;

      if (main.mapHelpers.cellExists(mousePosition)) {
        handleTargeting(relativeToPlayer);

        return true;
      }
    }

    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    if (amount == 1 && worldCamera.zoom < 1) {
      worldCamera.zoom += 0.2f;
    } else if (amount == -1 && worldCamera.zoom > 0.4f) {
      worldCamera.zoom -= 0.2f;
    }

    Gdx.app.log("PlayerInput", "Zoom Level: " + worldCamera.zoom + "");

    return true;
  }

  /**
   * Handles player movement.
   *
   * @param energy How much energy the player has
   * @param pos    The position we're attempting to move to
   */
  private void handleMovement(int energy, Vector2 pos) {
    if (holdingShift) {
      Entity enemy = main.entityHelpers.getEnemyAt(pos);

      if (enemy != null) {
        main.state = Main.State.FOCUSED;
        main.focusedEntity = enemy;
      }
    } else {
      if (energy >= MovementComponent.COST) {
        main.player.add(new MovementComponent(pos));

        main.executeTurn = true;
      }
    }

    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);

    playerDetails.target = null;
    playerDetails.lookingPath = null;
    playerDetails.targetingPath = null;
  }

  private void handleTargeting(Vector2 pos) {
    main.mapHelpers.createTargetingPath(
        ComponentMappers.position.get(main.player).pos, pos
    );
  }

  private void handleLooking(Vector2 pos, boolean careAboutWalls) {
    main.mapHelpers.createLookingPath(
        ComponentMappers.position.get(main.player).pos, pos, careAboutWalls
    );
  }

  private void handleThrow() {
    AttributesComponent attributes = ComponentMappers.attributes.get(main.player);

    if (attributes.energy < RangeComponent.COST) {
      return;
    }

    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);

    if (holdingShift) {
      Entity enemy = main.entityHelpers.getEnemyAt(playerDetails.target);

      if (enemy != null) {
        playerDetails.focusedAction = PlayerComponent.FocusedAction.THROWING;
        main.state = Main.State.FOCUSED;
        main.focusedEntity = enemy;
      }
    } else {
      main.combatHelpers.preparePlayerForThrowing(playerDetails.target, "body");
      main.executeTurn = true;

      main.state = Main.State.PLAYING;
    }

    playerDetails.target = null;
    playerDetails.targetingPath = null;
  }

  private void handleRange() {
    AttributesComponent attributes = ComponentMappers.attributes.get(main.player);

    if (attributes.energy < RangeComponent.COST) {
      return;
    }

    PlayerComponent playerDetails = ComponentMappers.player.get(main.player);

    if (holdingShift) {
      Entity enemy = main.entityHelpers.getEnemyAt(playerDetails.target);

      if (enemy != null) {
        playerDetails.focusedAction = PlayerComponent.FocusedAction.RANGED;
        main.state = Main.State.FOCUSED;
        main.focusedEntity = enemy;
      }
    } else {
      main.combatHelpers.preparePlayerForRanged(playerDetails.target, "body");
      main.executeTurn = true;

      main.state = Main.State.PLAYING;
    }

    playerDetails.target = null;
    playerDetails.targetingPath = null;
  }
}
