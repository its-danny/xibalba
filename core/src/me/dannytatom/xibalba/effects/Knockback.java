package me.dannytatom.xibalba.effects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.actions.MeleeComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.SpriteAccessor;
import me.dannytatom.xibalba.world.MapCell;
import me.dannytatom.xibalba.world.WorldManager;

public class Knockback extends Effect {
  @Override
  public void act(Entity caster, Entity target) {
    AttributesComponent casterAttributes = ComponentMappers.attributes.get(caster);

    if (casterAttributes.energy >= MeleeComponent.COST
        && WorldManager.entityHelpers.isNear(caster, target)) {
      PositionComponent casterPosition = ComponentMappers.position.get(caster);
      PositionComponent targetPosition = ComponentMappers.position.get(target);

      caster.add(new MeleeComponent(target, "body", false));

      int maxDistance = MathUtils.random(1, 3);

      Vector2 newPosition = getKnockbackPosition(
          casterPosition.pos, targetPosition.pos, maxDistance
      );

      VisualComponent targetVisual = ComponentMappers.visual.get(target);

      WorldManager.tweens.add(
          Tween.to(targetVisual.sprite, SpriteAccessor.XY, .25f).target(
              newPosition.x * Main.SPRITE_WIDTH, newPosition.y * Main.SPRITE_HEIGHT
          ).setCallback((type, source) -> {
            if (type == TweenCallback.COMPLETE) {
              targetPosition.pos.set(newPosition);

              Vector2 behindNewPosition = getCellBehind(casterPosition.pos, targetPosition.pos);

              if (WorldManager.mapHelpers.getCell(behindNewPosition).type == MapCell.Type.WALL
                  || WorldManager.mapHelpers.getEnemyAt(behindNewPosition) != null) {
                Main.cameraShake.shake(.5f, .1f);

                WorldManager.tweens.add(
                    Tween.to(targetVisual.sprite, SpriteAccessor.ALPHA, .05f)
                        .target(.25f).repeatYoyo(1, 0f)
                );

                int damage = MathUtils.random(3, 5);
                WorldManager.entityHelpers.takeDamage(target, damage);
                WorldManager.mapHelpers.makeFloorBloody(targetPosition.pos);

                AttributesComponent targetAttributes = ComponentMappers.attributes.get(target);
                WorldManager.log.add("effects.knockback.damaged", targetAttributes.name, damage);
              }
            }
          })
      );
    }
  }

  @Override
  public void revoke(Entity caster, Entity target) {

  }

  private Vector2 getKnockbackPosition(Vector2 casterPosition, Vector2 targetPosition,
                                       int maxDistance) {
    Vector2 newPosition = targetPosition.cpy();
    Vector2 testPosition = newPosition.cpy();
    boolean blocked = false;
    int currentDistance = 1;

    while (!blocked && currentDistance < maxDistance) {
      if (casterPosition.x < targetPosition.x) {
        testPosition.x += currentDistance;
      } else if (casterPosition.x > targetPosition.x) {
        testPosition.x -= currentDistance;
      }

      if (casterPosition.y < targetPosition.y) {
        testPosition.y += currentDistance;
      } else if (casterPosition.y > targetPosition.y) {
        testPosition.y -= currentDistance;
      }

      currentDistance += 1;

      if (WorldManager.mapHelpers.isBlocked(testPosition)) {
        blocked = true;
      } else {
        newPosition.set(testPosition);
      }
    }

    return newPosition;
  }

  private Vector2 getCellBehind(Vector2 casterPosition, Vector2 targetPosition) {
    Vector2 newPosition = targetPosition.cpy();

    if (casterPosition.x < targetPosition.x) {
      newPosition.x += 1;
    } else if (casterPosition.x > targetPosition.x) {
      newPosition.x -= 1;
    }

    if (casterPosition.y < targetPosition.y) {
      newPosition.y += 1;
    } else if (casterPosition.y > targetPosition.y) {
      newPosition.y -= 1;
    }

    return newPosition;
  }
}
