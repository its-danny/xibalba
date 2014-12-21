package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BrainComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.ai.TargetComponent;
import me.dannytatom.xibalba.components.ai.WanderComponent;
import me.dannytatom.xibalba.map.Map;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class BrainSystem extends IteratingSystem {
  private Map map;

  /**
   * THA CONTROL CENTER. Handles AI states.
   *
   * @param map The map we're currently on
   */
  public BrainSystem(Map map) {
    super(Family.all(BrainComponent.class, AttributesComponent.class, PositionComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    Vector2 playerPosition = map.getPlayerPosition();
    PositionComponent position = ComponentMappers.position.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    entity.remove(WanderComponent.class);
    entity.remove(TargetComponent.class);

    if (map.isNearPlayer(position.x, position.y, attributes.vision)) {
      if (attributes.energy >= 100) {
        entity.add(new TargetComponent((int) playerPosition.x, (int) playerPosition.y));
      }
    } else {
      if (attributes.energy >= 100) {
        entity.add(new WanderComponent());
      }
    }
  }
}
