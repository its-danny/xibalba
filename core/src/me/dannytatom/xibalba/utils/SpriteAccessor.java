package me.dannytatom.xibalba.utils;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteAccessor implements TweenAccessor<Sprite> {
  public static final int ALPHA = 1;
  public static final int XY = 2;

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
      default:
        break;
    }
  }
}
