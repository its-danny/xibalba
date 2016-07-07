package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.systems.AttributesSystem;
import me.dannytatom.xibalba.systems.MouseMovementSystem;
import me.dannytatom.xibalba.systems.actions.MeleeSystem;
import me.dannytatom.xibalba.systems.actions.MovementSystem;
import me.dannytatom.xibalba.systems.actions.RangeSystem;
import me.dannytatom.xibalba.systems.ai.BrainSystem;
import me.dannytatom.xibalba.systems.ai.TargetSystem;
import me.dannytatom.xibalba.systems.ai.WanderSystem;
import me.dannytatom.xibalba.systems.statuses.BleedingSystem;
import me.dannytatom.xibalba.systems.statuses.CrippledSystem;
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

    // Setup engine (systems are run in order added)
    WorldManager.engine.addSystem(new AttributesSystem());
    WorldManager.engine.addSystem(new MouseMovementSystem());
    WorldManager.engine.addSystem(new BrainSystem());
    WorldManager.engine.addSystem(new WanderSystem());
    WorldManager.engine.addSystem(new TargetSystem());
    WorldManager.engine.addSystem(new MeleeSystem());
    WorldManager.engine.addSystem(new RangeSystem());
    WorldManager.engine.addSystem(new MovementSystem());
    WorldManager.engine.addSystem(new CrippledSystem());
    WorldManager.engine.addSystem(new BleedingSystem());
  }

  public enum State {
    PLAYING, TARGETING, LOOKING, MOVING, FOCUSED
  }
}
