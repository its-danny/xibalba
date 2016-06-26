package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.MouseMovementComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.map.Map;
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

  public void update(float deltaTime) {
    for (Entity entity : entities) {
      entity.remove(MovementComponent.class);

      Map map = main.getMap();

      // Remove mouse movement component once path is empty
      if (map.lookingPath == null || map.lookingPath.isEmpty()) {
        entity.remove(MouseMovementComponent.class);
        main.state = Main.State.PLAYING;
      } else {
        // Start walking.
        // If the path becomes blocked, reset the path.
        GridCell cell = map.lookingPath.get(0);

        if (cell.isWalkable()) {
          entity.add(new MovementComponent(new Vector2(cell.getX(), cell.getY())));

          List<GridCell> newPath = new ArrayList<>(map.lookingPath);
          newPath.remove(cell);

          map.lookingPath = newPath;
        } else {
          map.lookingPath = null;
        }
      }
    }
  }
}
