package me.dannytatom.xibalba.utils;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteAccessor implements TweenAccessor<Sprite> {
  public static final int ALPHA = 1;
  public static final int XY = 2;
  public static final int COLOR = 3;
  public static final int SCALE = 4;

  @Override
  public int getValues(Sprite target, int tweenType, float[] returnValues) {
    switch (tweenType) {
      case ALPHA:
        returnValues[0] = target.getColor().a;
        return 1;
      case XY:
        returnValues[0] = target.getX();
        returnValues[1] = target.getY();
        return 2;
      case COLOR:
        returnValues[0] = target.getColor().r;
        returnValues[1] = target.getColor().g;
        returnValues[2] = target.getColor().b;
        return 3;
      case SCALE:
        returnValues[0] = target.getScaleX();
        returnValues[1] = target.getScaleY();
        return 4;
      default:
        return -1;
    }
  }

  @Override
  public void setValues(Sprite target, int tweenType, float[] newValues) {
    switch (tweenType) {
      case ALPHA:
        target.setAlpha(newValues[0]);
        break;
      case XY:
        target.setPosition(newValues[0], newValues[1]);
        break;
      case COLOR:
        target.setColor(newValues[0], newValues[1], newValues[2], target.getColor().a);
        break;
      case SCALE:
        target.setScale(newValues[0], newValues[1]);
        break;
      default:
        break;
    }
  }
}
