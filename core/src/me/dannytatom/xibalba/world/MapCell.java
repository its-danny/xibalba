package me.dannytatom.xibalba.world;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class MapCell {
  public final String description;
  public final Sprite sprite;
  private final Type type;
  public boolean hidden = true;
  public boolean forgotten = false;

  /**
   * Holds world cell data.
   *
   * @param sprite      tile sprite
   * @param type        whether or not an entity can move onto this cell
   * @param description what this cell like?
   */
  MapCell(Sprite sprite, Type type, String description) {
    this.sprite = sprite;
    this.type = type;
    this.description = description;
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

  public boolean isShallowWater() {
    return type == Type.SHALLOW_WATER;
  }

  public boolean isDeepWater() {
    return type == Type.DEEP_WATER;
  }

  public enum Type {
    NOTHING, FLOOR, WALL, SHALLOW_WATER, DEEP_WATER
  }
}
