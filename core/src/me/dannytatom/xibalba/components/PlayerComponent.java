package me.dannytatom.xibalba.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import me.dannytatom.xibalba.abilities.Ability;

import org.xguzm.pathfinding.grid.GridCell;

public class PlayerComponent implements Component {
  public final Array<String> identifiedItems = new Array<>();
  public List<GridCell> path = null;
  public Vector2 target = null;
  public Entity lastHitEntity = null;
  public FocusedAction focusedAction = null;
  public Entity focusedEntity = null;
  public Ability targetingAbility = null;
  public int lowestDepth = 1;
  public int totalHits = 0;
  public int totalMisses = 0;
  public int totalDamageDone = 0;
  public int totalKills = 0;
  public int totalDamageReceived = 0;
  public int totalDamageHealed = 0;

  public PlayerComponent() {

  }

  public enum FocusedAction {
    MELEE, THROWING, RANGED
  }
}
