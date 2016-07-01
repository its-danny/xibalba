package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

public class BodyComponent implements Component {
  public HashMap<String, Integer> parts;

  public BodyComponent(HashMap<String, Integer> parts) {
    this.parts = parts;
  }
}
