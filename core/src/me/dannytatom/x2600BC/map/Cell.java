package me.dannytatom.x2600BC.map;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Cell {
  public Sprite sprite;
  public boolean isWall;

  /**
   * Holds map cell data.
   *
   * @param sprite tile sprite
   * @param isWall whether or not an entity can move onto this cell
   */
  public Cell(Sprite sprite, boolean isWall) {
    this.sprite = sprite;
    this.isWall = isWall;
  }
}
