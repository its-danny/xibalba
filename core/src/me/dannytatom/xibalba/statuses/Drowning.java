package me.dannytatom.xibalba.statuses;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;

public class Drowning {
  public Drowning() {

  }

  /**
   * What to do each turn.
   *
   * @param entity The entity that's drownings
   */
  public void onTurn(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    attributes.health -= 5;

    if (attributes.vision > 1) {
      attributes.vision -= 1;
    }
  }

  /**
   * Whether we should remove the bleeding component or not.
   *
   * @param entity The entity that's drowning
   *
   * @return Whether or not to remove 'em
   */
  public boolean shouldRemove(Entity entity) {
    PositionComponent position = ComponentMappers.position.get(entity);

    return !WorldManager.mapHelpers.getCell(position.pos.x, position.pos.y).isDeepWater();
  }
}