package me.dannytatom.xibalba.world;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class MapCell {
  public final String description;
  public final boolean isWall;
  public final boolean isNothing;
  public final Sprite sprite;
  public boolean hidden = true;
  public boolean forgotten = false;

  /**
   * Holds world cell data.
   *
   * @param sprite tile sprite
   * @param isWall whether or not an entity can move onto this cell
   */
  MapCell(Sprite sprite, boolean isWall, boolean isNothing, String description) {
    this.sprite = sprite;
    this.isWall = isWall;
    this.isNothing = isNothing;
    this.description = description;
  }
}
