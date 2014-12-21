package me.dannytatom.xibalba.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.xibalba.components.PlayerComponent;

public class PlayerSystem extends IteratingSystem {
  public PlayerSystem() {
    super(Family.one(PlayerComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {

  }
}
