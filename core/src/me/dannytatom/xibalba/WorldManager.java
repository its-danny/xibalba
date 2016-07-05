package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.utils.CombatHelpers;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.EquipmentHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;
import me.dannytatom.xibalba.utils.MapHelpers;
import me.dannytatom.xibalba.utils.SkillHelpers;

public class WorldManager {
  public static Engine engine;
  public static World world;
  public static State state;
  public static ActionLog log;

  public static MapHelpers mapHelpers;
  public static CombatHelpers combatHelpers;
  public static EntityHelpers entityHelpers;
  public static InventoryHelpers inventoryHelpers;
  public static EquipmentHelpers equipmentHelpers;
  public static SkillHelpers skillHelpers;

  public static Entity player;
  public static boolean executeTurn;
  public static int turnCount;

  public static void setup() {
    engine = new Engine();
    world = new World();
    log = new ActionLog();

    mapHelpers = new MapHelpers();
    entityHelpers = new EntityHelpers();
    inventoryHelpers = new InventoryHelpers();
    equipmentHelpers = new EquipmentHelpers();
    skillHelpers = new SkillHelpers();
    combatHelpers = new CombatHelpers();

    player = new Entity();
    executeTurn = false;
    turnCount = 0;
  }

  public enum State {
    PLAYING, TARGETING, LOOKING, MOVING, FOCUSED
  }
}
