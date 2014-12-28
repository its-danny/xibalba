package me.dannytatom.xibalba.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;

public class PlayerFactory {
  private final AssetManager assets;

  public PlayerFactory(AssetManager assets) {
    this.assets = assets;
  }

  /**
   * Spawn an entity at a given location.
   *
   * @param position Position to spawn
   * @return The entity
   */
  public Entity spawn(Vector2 position) {
    Entity player = new Entity();

    player.add(new PlayerComponent());
    player.add(new PositionComponent(position));
    player.add(new MovementComponent());
    player.add(new VisualComponent(assets.get("sprites/player.png")));
    player.add(new AttributesComponent("Player", 100, 3, 100));

    return player;
  }
}
