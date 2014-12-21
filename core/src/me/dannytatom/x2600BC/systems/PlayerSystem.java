package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.components.PlayerComponent;
import me.dannytatom.x2600BC.map.Map;

public class PlayerSystem extends IteratingSystem {
  private Map map;

  public PlayerSystem(Map map) {
    super(Family.one(PlayerComponent.class).get());

    this.map = map;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {

  }
}
