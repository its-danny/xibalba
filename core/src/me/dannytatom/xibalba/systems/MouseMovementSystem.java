package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.List;

public class MouseMovementSystem extends EntitySystem {
  private final Main main;

  private ImmutableArray<Entity> entities;

  public MouseMovementSystem(Main main) {
    this.main = main;
  }

  public void addedToEngine(Engine engine) {
    entities = engine.getEntitiesFor(Family.all(MouseMovementComponent.class).get());
  }

  /**
   * Get next step in moving path, add a movement component with that position, remove step.
   *
   * @param deltaTime Time between now and previous frame
   */
  public void update(float deltaTime) {
    for (Entity entity : entities) {
      PlayerComponent playerDetails = ComponentMappers.player.get(main.player);

      // Remove mouse movement component once path is empty
      if (playerDetails.lookingPath == null || playerDetails.lookingPath.isEmpty()) {
        entity.remove(MouseMovementComponent.class);
        main.state = Main.State.PLAYING;
      } else {
        // Start walking.
        // If the path becomes blocked, reset the path.
        GridCell cell = playerDetails.lookingPath.get(0);

        entity.add(new MovementComponent(new Vector2(cell.getX(), cell.getY())));

        List<GridCell> newPath = new ArrayList<>(playerDetails.lookingPath);
        newPath.remove(cell);

        playerDetails.lookingPath = newPath;
      }
    }
  }
}
