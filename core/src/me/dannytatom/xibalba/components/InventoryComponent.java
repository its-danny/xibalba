package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

public class InventoryComponent implements Component {
  public final ArrayList<Entity> items = new ArrayList<>();
}
