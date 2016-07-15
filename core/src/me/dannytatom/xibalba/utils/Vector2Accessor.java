package me.dannytatom.xibalba.utils;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Vector2;

public class Vector2Accessor implements TweenAccessor<Vector2> {
  public static final int TYPE_XY = 1;

  @Override
  public int getValues(Vector2 target, int tweenType, float[] returnValues) {
    switch (tweenType) {
      case TYPE_XY:
        returnValues[0] = target.x;
        returnValues[1] = target.y;
        return 2;
      default:
        assert false;
        return -1;
    }
  }

  @Override
  public void setValues(Vector2 target, int tweenType, float[] newValues) {
    switch (tweenType) {
      case TYPE_XY:
        target.x = newValues[0];
        target.y = newValues[1];
        break;
      default:
        assert false;
        break;
    }
  }
}
