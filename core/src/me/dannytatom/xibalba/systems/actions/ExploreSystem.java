package me.dannytatom.xibalba.systems.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.actions.ExploreComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class ExploreSystem extends EntitySystem {
  private ImmutableArray<Entity> entities;

  public ExploreSystem() {

  }

  /**
   * Triggers when this system is added to Ashley engine.
   *
   * @param engine An instance of the engine
   */
  public void addedToEngine(Engine engine) {
    entities = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, ExploreComponent.class).get()
    );
  }

  /**
   * Update all entities with the Explore component.
   *
   * @param deltaTime Time since last frame.
   */
  public void update(float deltaTime) {
    for (Entity entity : entities) {
      ExploreComponent explore = ComponentMappers.explore.get(entity);

      // If the player sees an enemy, stop
      if (WorldManager.entityHelpers.enemyInSight(entity)) {
        entity.remove(ExploreComponent.class);
        WorldManager.state = WorldManager.State.PLAYING;

        return;
      }

      // If there is nowhere else to go, stop
      if (WorldManager.world.getCurrentMap().dijkstra.exploreGoals.size == 0) {
        entity.remove(ExploreComponent.class);
        WorldManager.state = WorldManager.State.PLAYING;

        return;
      }

      // Get new path if we're done
      if (explore.path.size == 0) {
        WorldManager.world.getCurrentMap().dijkstra.updatePlayerExplore();

        explore.path = WorldManager.world.getCurrentMap().dijkstra.findExplorePath(
            ComponentMappers.position.get(WorldManager.player).pos
        );
      }

      AttributesComponent attributes = ComponentMappers.attributes.get(entity);

      // Walk it out!
      if (attributes.energy >= MovementComponent.COST) {
        // Start walking
        Vector2 cell = explore.path.get(0);

        entity.add(new MovementComponent(cell));
        explore.path.removeIndex(0);
      }
    }
  }
}
