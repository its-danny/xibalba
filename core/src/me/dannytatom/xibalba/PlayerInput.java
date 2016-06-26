package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.screens.CharacterScreen;
import me.dannytatom.xibalba.screens.HelpScreen;
import me.dannytatom.xibalba.screens.InventoryScreen;
import me.dannytatom.xibalba.screens.PauseScreen;

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
    AttributesComponent attributes = main.player.getComponent(AttributesComponent.class);
    PositionComponent position = main.player.getComponent(PositionComponent.class);

    Map map = main.getMap();

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
          map.target = null;
          map.lookingPath = null;

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
      case Keys.T: {
        if (main.state == Main.State.PLAYING) {
          Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);

          if (primaryWeapon != null) {
            ItemComponent itemComponent = primaryWeapon.getComponent(ItemComponent.class);

            if (itemComponent.actions.get("canThrow")) {
              itemComponent.throwing = true;

              main.state = Main.State.TARGETING;
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
        if (main.state == Main.State.SEARCHING) {
          map.target = null;
          map.lookingPath = null;

          main.state = Main.State.PLAYING;
        } else if (main.state == Main.State.TARGETING) {
          map.target = null;
          map.targetingPath = null;

          main.state = Main.State.PLAYING;
        } else if (main.state == Main.State.MOVING) {
          map.target = null;
          map.lookingPath = null;

          main.player.remove(MouseMovementComponent.class);
          main.player.remove(MovementComponent.class);
          main.state = Main.State.PLAYING;
        }
        break;
      case Keys.SPACE:
        if (main.state == Main.State.TARGETING) {
          if (map.targetingPath != null && attributes.energy >= RangeComponent.COST) {
            Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);
            Entity item;
            String skill;

            if (primaryWeapon != null && primaryWeapon.getComponent(ItemComponent.class).usesAmmunition) {
              item = main.inventoryHelpers.getAmmunitionOfType(
                  main.player, primaryWeapon.getComponent(ItemComponent.class).ammunitionType
              );

              skill = primaryWeapon.getComponent(ItemComponent.class).skill;
            } else {
              item = main.inventoryHelpers.getThrowingItem(main.player);
              skill = "throwing";
            }

            main.player.add(new RangeComponent(map.target, item, skill));

            main.executeTurn = true;
          }

          map.target = null;
          map.targetingPath = null;

          main.state = Main.State.PLAYING;
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
    if (main.state == Main.State.PLAYING
        && main.player.getComponent(MouseMovementComponent.class) == null) {
      Vector2 mousePosition = main.mousePositionToWorld(worldCamera);
      Map map = main.getMap();

      if (map.cellExists(mousePosition) && !map.getCell(mousePosition).hidden) {
        main.player.add(new MouseMovementComponent());

        main.state = Main.State.MOVING;
        main.executeTurn = true;

        return true;
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
    if (main.state == Main.State.PLAYING) {
      Map map = main.getMap();

      map.target = null;
      map.lookingPath = null;

      PositionComponent playerPosition = main.player.getComponent(PositionComponent.class);
      Vector2 mousePosition = main.mousePositionToWorld(worldCamera);
      Vector2 relativeToPlayer = mousePosition.cpy().sub(playerPosition.pos);

      if (map.cellExists(mousePosition)) {
        map.createLookingPath(
            main.player.getComponent(PositionComponent.class).pos, relativeToPlayer, true
        );

        return true;
      }
    }

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
   *
   * <p>Actually move, pick up item on the cell (then move), or melee attack enemy in cell
   *
   * @param energy How much energy the player has
   * @param pos    The position we're attempting to move to
   */
  private void handleMovement(int energy, Vector2 pos) {
    Map map = main.getMap();

    map.target = null;
    map.lookingPath = null;

    if (map.isWalkable(pos)) {
      if (energy >= MovementComponent.COST) {
        main.player.add(new MovementComponent(pos));

        main.executeTurn = true;
      }
    } else {
      Entity thing = map.getEntityAt(pos);

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
    main.getMap().createTargetingPath(
        main.player.getComponent(PositionComponent.class).pos, pos
    );
  }

  private void handleSearching(Vector2 pos) {
    main.getMap().createLookingPath(
        main.player.getComponent(PositionComponent.class).pos, pos, false
    );
  }
}
