package me.dannytatom.xibalba.world;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MapCell {
  public final Sprite sprite;
  public Tween tween;
  public String description;
  public Type type;
  public Covered covered;
  public boolean hidden = true;
  public boolean forgotten = false;
  public boolean onFire = false;

  /**
   * Holds world cell data.
   *
   * @param sprite      tile sprite
   * @param type        whether or not an entity can move onto this cell
   * @param description what this cell like?
   */
  MapCell(Sprite sprite, Type type, String description, Tween tween) {
    this.sprite = sprite;
    this.type = type;
    this.covered = Covered.NOTHING;
    this.description = description;
    this.tween = tween;
  }

  MapCell(Sprite sprite, Type type, String description) {
    this(sprite, type, description, null);
  }

  public boolean isNothing() {
    return type == Type.NOTHING;
  }

  public boolean isFloor() {
    return type == Type.FLOOR;
  }

  public boolean isWall() {
    return type == Type.WALL;
  }

  public boolean isWater() {
    return type == Type.SHALLOW_WATER || type == Type.DEEP_WATER;
  }

  public boolean isDeepWater() {
    return type == Type.DEEP_WATER;
  }

  public boolean hasBlood() {
    return covered == Covered.BLOOD;
  }

  public enum Type {
    NOTHING, FLOOR, WALL, SHALLOW_WATER, DEEP_WATER
  }

  public enum Covered {
    NOTHING, WATER, BLOOD, VOMIT
  }
}
