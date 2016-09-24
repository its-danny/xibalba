package me.dannytatom.xibalba.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.ActionLog;
import me.dannytatom.xibalba.helpers.CombatHelpers;
import me.dannytatom.xibalba.helpers.EntityHelpers;
import me.dannytatom.xibalba.helpers.ItemHelpers;
import me.dannytatom.xibalba.helpers.MapHelpers;
import me.dannytatom.xibalba.systems.AttributesSystem;
import me.dannytatom.xibalba.systems.MouseMovementSystem;
import me.dannytatom.xibalba.systems.TileEffectSystem;
import me.dannytatom.xibalba.systems.TimeSystem;
import me.dannytatom.xibalba.systems.actions.MeleeSystem;
import me.dannytatom.xibalba.systems.actions.MovementSystem;
import me.dannytatom.xibalba.systems.actions.RangeSystem;
import me.dannytatom.xibalba.systems.ai.BrainSystem;
import me.dannytatom.xibalba.systems.ai.TargetSystem;
import me.dannytatom.xibalba.systems.ai.WanderSystem;
import me.dannytatom.xibalba.systems.statuses.BleedingSystem;
import me.dannytatom.xibalba.systems.statuses.CrippledSystem;
import me.dannytatom.xibalba.systems.statuses.DrowningSystem;
import me.dannytatom.xibalba.systems.statuses.WetSystem;
import me.dannytatom.xibalba.utils.EntityFactory;

public class WorldManager {
  public static Engine engine;
  public static ActionLog log;
  public static World world;
  public static State state;
  public static EntityFactory entityFactory;
  public static MapHelpers mapHelpers;
  public static CombatHelpers combatHelpers;
  public static EntityHelpers entityHelpers;
  public static ItemHelpers itemHelpers;
  public static Entity player;
  public static boolean executeTurn;
  public static int turnCount;

  /**
   * Setup a whole bunch of shit.
   */
  public static void setup() {
    engine = new Engine();
    log = new ActionLog();
    world = new World();

    entityFactory = new EntityFactory();
    mapHelpers = new MapHelpers();
    entityHelpers = new EntityHelpers();
    itemHelpers = new ItemHelpers();
    combatHelpers = new CombatHelpers();

    executeTurn = false;
    turnCount = 0;

    // Setup engine (systems are run in order added)
    engine.addSystem(new AttributesSystem());
    engine.addSystem(new MouseMovementSystem());
    engine.addSystem(new BrainSystem());
    engine.addSystem(new WanderSystem());
    engine.addSystem(new TargetSystem());
    engine.addSystem(new MovementSystem());
    engine.addSystem(new RangeSystem());
    engine.addSystem(new MeleeSystem());
    engine.addSystem(new TimeSystem());
    engine.addSystem(new TileEffectSystem());
    engine.addSystem(new CrippledSystem());
    engine.addSystem(new BleedingSystem());
    engine.addSystem(new DrowningSystem());
    engine.addSystem(new WetSystem());
  }

  public enum State {
    PLAYING, TARGETING, LOOKING, MOVING, GOING_DOWN, GOING_UP, FOCUSED, WAITING
  }
}
