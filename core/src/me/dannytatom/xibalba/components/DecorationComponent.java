package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class DecorationComponent implements Component {
  public final boolean blocks;

  /**
   * Flags the entity as a decoration.
   *
   * @param blocks Does it block movement?
   */
  public DecorationComponent(boolean blocks) {
    this.blocks = blocks;
  }
}
