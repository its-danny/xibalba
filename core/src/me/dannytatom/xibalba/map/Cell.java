package me.dannytatom.xibalba.map;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Cell {
  public final Sprite sprite;
  public final boolean isWall;
  public String description;

  /**
   * Holds map cell data.
   *
   * @param sprite tile sprite
   * @param isWall whether or not an entity can move onto this cell
   */
  public Cell(Sprite sprite, boolean isWall) {
    this.sprite = sprite;
    this.isWall = isWall;

    this.description = "The floor, silly.";
  }
}
