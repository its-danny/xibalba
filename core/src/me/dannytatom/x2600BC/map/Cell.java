package me.dannytatom.x2600BC.map;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Cell {
  public Sprite sprite;
  public boolean isBlocked;

  /**
   * Holds map cell data.
   *
   * @param sprite    tile sprite
   * @param isBlocked whether or not an entity can move onto this cell
   */
  public Cell(Sprite sprite, boolean isBlocked) {
    this.sprite = sprite;
    this.isBlocked = isBlocked;
  }
}
