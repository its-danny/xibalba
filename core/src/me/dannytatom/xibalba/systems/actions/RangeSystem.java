package me.dannytatom.xibalba.systems.actions;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.RangeComponent;
import me.dannytatom.xibalba.systems.UsesEnergySystem;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.SpriteAccessor;
import me.dannytatom.xibalba.world.WorldManager;

public class RangeSystem extends UsesEnergySystem {
  public RangeSystem() {
    super(Family.all(RangeComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    RangeComponent range = ComponentMappers.range.get(entity);
    AttributesComponent attributes = ComponentMappers.attributes.get(entity);

    if (range.position != null && !entity.isScheduledForRemoval()) {
      Entity target = WorldManager.mapHelpers.getEnemyAt(range.position);

      if (target != null) {
        WorldManager.combatHelpers.range(
            entity, target, range.bodyPart, range.item, range.skill, range.isFocused
        );
      }

      if (Objects.equals(range.skill, "throwing")) {
        ComponentMappers.item.get(range.item).throwing = false;
      }

      if (target == null) {
        doThrowAnimation(entity, range.item, range.position, false);
      } else {
        doThrowAnimation(entity, range.item, range.position, true);
      }
    }

    attributes.energy -= RangeComponent.COST;
    entity.remove(RangeComponent.class);
  }

  private void doThrowAnimation(Entity entity, Entity item, Vector2 position, boolean destroy) {
    // We have to set the items position before starting the tween since who knows wtf
    // position it had before it ended up in your inventory.
    PositionComponent entityPosition = ComponentMappers.position.get(entity);

    WorldManager.entityHelpers.updatePosition(item, entityPosition.pos.x, entityPosition.pos.y);
    WorldManager.entityHelpers.updateSprite(item, entityPosition.pos.x, entityPosition.pos.y);

    VisualComponent itemVisual = ComponentMappers.visual.get(item);

    WorldManager.tweens.add(Tween.to(itemVisual.sprite, SpriteAccessor.XY, .1f).target(
        position.x * Main.SPRITE_WIDTH, position.y * Main.SPRITE_HEIGHT
    ).setCallback(
        (type, source) -> {
          if (type == TweenCallback.COMPLETE) {
            WorldManager.itemHelpers.drop(entity, item, position, destroy);
          }
        }
    ));
  }
}
