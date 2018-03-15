package me.dannytatom.xibalba.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class InputHelpers {
  private ArrayList<Entity> enemiesAround;

  public InputHelpers() {
    enemiesAround = new ArrayList<>();
  }

  /**
   * Decide where to place cursor when the player has switched to targeting.
   */
  public void startTargeting(WorldManager.TargetState targetState) {
    WorldManager.state = WorldManager.State.TARGETING;
    WorldManager.targetState = targetState;

    enemiesAround = WorldManager.mapHelpers.getEnemiesInPlayerVision();

    PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

    if (playerDetails.lastHitEntity != null) {
      PositionComponent lastHitEntityPosition
          = ComponentMappers.position.get(playerDetails.lastHitEntity);
      handleTargeting(lastHitEntityPosition.pos.cpy().sub(playerPosition.pos));
    } else if (enemiesAround.size() > 0) {
      PositionComponent closestPosition = ComponentMappers.position.get(enemiesAround.get(0));
      handleTargeting(closestPosition.pos.cpy().sub(playerPosition.pos));
    } else {
      playerDetails.target = null;
      playerDetails.path = null;
    }
  }

  /**
   * Create targeting path.
   *
   * @param target Target position.
   */
  public void handleTargeting(Vector2 target) {
    PositionComponent playerPosition = ComponentMappers.position.get(WorldManager.player);

    WorldManager.mapHelpers.createTargetingPath(playerPosition.pos, target);
  }
}
