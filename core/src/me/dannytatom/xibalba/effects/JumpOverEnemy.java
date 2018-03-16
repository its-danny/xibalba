package me.dannytatom.xibalba.effects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Elastic;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MovementComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.SpriteAccessor;
import me.dannytatom.xibalba.world.WorldManager;

public class JumpOverEnemy extends Effect {
  @Override
  public void act(Entity caster, Entity target) {
    AttributesComponent attributes = ComponentMappers.attributes.get(caster);

    if (attributes.energy >= MovementComponent.COST
        && WorldManager.entityHelpers.isNear(caster, target)) {
      PositionComponent casterPosition = ComponentMappers.position.get(caster);
      PositionComponent targetPosition = ComponentMappers.position.get(target);

      float newPositionX = targetPosition.pos.x;
      float newPositionY = targetPosition.pos.y;

      if (casterPosition.pos.x < targetPosition.pos.x) {
        newPositionX += 1;
      } else if (casterPosition.pos.x > targetPosition.pos.x) {
        newPositionX -= 1;
      }

      if (casterPosition.pos.y < targetPosition.pos.y) {
        newPositionY += 1;
      } else if (casterPosition.pos.y > targetPosition.pos.y) {
        newPositionY -= 1;
      }

      Vector2 newPosition = new Vector2(newPositionX, newPositionY);

      if (!WorldManager.mapHelpers.isBlocked(newPosition)) {
        VisualComponent visual = ComponentMappers.visual.get(caster);

        WorldManager.tweens.add(
            Tween.to(visual.sprite, SpriteAccessor.SCALE, .25f).target(
                1.2f, 1.2f
            ).repeatYoyo(1, 0f).ease(Elastic.INOUT)
        );

        WorldManager.tweens.add(
            Tween.to(visual.sprite, SpriteAccessor.XY, .25f).target(
                newPosition.x * Main.SPRITE_WIDTH, newPosition.y * Main.SPRITE_HEIGHT
            ).setCallback((type, source) -> {
              if (type == TweenCallback.COMPLETE) {
                casterPosition.pos.set(newPosition);
              }
            })
        );
      }
    }
  }

  @Override
  public void revoke(Entity caster, Entity target) {

  }
}
