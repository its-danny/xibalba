package me.dannytatom.x2600BC;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Cell {
  public Sprite sprite;
  public boolean blocksMovement;

  /**
   * Holds map cell data.
   *
   * @param sprite         tile sprite
   * @param blocksMovement whether or not an entity can move onto this cell
   */
  public Cell(Sprite sprite, boolean blocksMovement) {
    this.sprite = sprite;
    this.blocksMovement = blocksMovement;
  }
}
