package me.dannytatom.xibalba.map;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Cell {
  public final String description;
  public final boolean isWall;
  public final boolean isNothing;
  public Sprite sprite;
  public boolean hidden = true;
  public boolean forgotten = false;

  /**
   * Holds map cell data.
   *
   * @param sprite tile sprite
   * @param isWall whether or not an entity can move onto this cell
   */
  Cell(Sprite sprite, boolean isWall, boolean isNothing, String description) {
    this.sprite = sprite;
    this.isWall = isWall;
    this.isNothing = isNothing;
    this.description = description;
  }
}
