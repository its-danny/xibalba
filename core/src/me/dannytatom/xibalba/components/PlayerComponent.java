package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class PlayerComponent implements Component {
  public final Array<String> identifiedItems = new Array<>();
  public List<GridCell> path = null;
  public Vector2 target = null;
  public FocusedAction focusedAction = null;
  public Entity focusedEntity = null;

  public PlayerComponent() {

  }

  public enum FocusedAction {
    MELEE, THROWING, RANGED
  }
}
