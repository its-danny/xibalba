package me.dannytatom.xibalba.statuses;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class Bleeding {
  private int lifeCounter = 0;

  public Bleeding() {

  }

  public void onTurn(Entity entity) {
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);
    attributes.health -= 5;

    lifeCounter += 1;
  }

  public boolean shouldRemove() {
    return lifeCounter == 5;
  }
}
