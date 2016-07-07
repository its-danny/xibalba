package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

public class DecorationComponent implements Component {
  public boolean blocks;

  public DecorationComponent(boolean blocks) {
    this.blocks = blocks;
  }
}
