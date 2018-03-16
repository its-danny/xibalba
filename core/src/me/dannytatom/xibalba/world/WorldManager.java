package me.dannytatom.xibalba.world;

import aurelienribon.tweenengine.Tween;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import me.dannytatom.xibalba.ActionLog;
import me.dannytatom.xibalba.helpers.CombatHelpers;
import me.dannytatom.xibalba.helpers.EntityHelpers;
import me.dannytatom.xibalba.helpers.InputHelpers;
import me.dannytatom.xibalba.helpers.ItemHelpers;
import me.dannytatom.xibalba.helpers.MapHelpers;
import me.dannytatom.xibalba.systems.AbilitiesSystem;
import me.dannytatom.xibalba.systems.AttributesSystem;
import me.dannytatom.xibalba.systems.BrainSystem;
import me.dannytatom.xibalba.systems.DeathSystem;
import me.dannytatom.xibalba.systems.MouseMovementSystem;
import me.dannytatom.xibalba.systems.TileEffectSystem;
import me.dannytatom.xibalba.systems.actions.ExploreSystem;
import me.dannytatom.xibalba.systems.actions.MeleeSystem;
import me.dannytatom.xibalba.systems.actions.MovementSystem;
import me.dannytatom.xibalba.systems.actions.RangeSystem;
import me.dannytatom.xibalba.systems.statuses.BleedingSystem;
import me.dannytatom.xibalba.systems.statuses.BurningSystem;
import me.dannytatom.xibalba.systems.statuses.CharmedSystem;
import me.dannytatom.xibalba.systems.statuses.CrippledSystem;
import me.dannytatom.xibalba.systems.statuses.DrowningSystem;
import me.dannytatom.xibalba.systems.statuses.EncumberedSystem;
import me.dannytatom.xibalba.systems.statuses.PoisonedSystem;
import me.dannytatom.xibalba.systems.statuses.SickSystem;
import me.dannytatom.xibalba.systems.statuses.StuckSystem;
import me.dannytatom.xibalba.systems.statuses.WetSystem;
import me.dannytatom.xibalba.utils.EntityFactory;

public class WorldManager {
  public static Engine engine;
  public static ActionLog log;
  public static World world;
  public static State state;
  public static TargetState targetState;
  public static Array<Tween> tweens;
  public static EntityFactory entityFactory;
  public static InputHelpers inputHelpers;
  public static MapHelpers mapHelpers;
  public static CombatHelpers combatHelpers;
  public static EntityHelpers entityHelpers;
  public static ItemHelpers itemHelpers;
  public static Entity player;
  public static Entity god;
  public static boolean executeTurn;
  public static int turnCount;

  /**
   * Setup a whole bunch of shit.
   */
  public static void setup() {
    engine = new Engine();
    log = new ActionLog();
    world = new World();
    tweens = new Array<>();

    entityFactory = new EntityFactory();
    inputHelpers = new InputHelpers();
    mapHelpers = new MapHelpers();
    entityHelpers = new EntityHelpers();
    itemHelpers = new ItemHelpers();
    combatHelpers = new CombatHelpers();

    executeTurn = false;
    turnCount = 0;

    // Setup engine (systems are run in order added)
    engine.addSystem(new AttributesSystem());
    engine.addSystem(new AbilitiesSystem());
    engine.addSystem(new MouseMovementSystem());
    engine.addSystem(new ExploreSystem());
    engine.addSystem(new BrainSystem());
    engine.addSystem(new RangeSystem());
    engine.addSystem(new MeleeSystem());
    engine.addSystem(new MovementSystem());
    engine.addSystem(new TileEffectSystem());
    engine.addSystem(new EncumberedSystem());
    engine.addSystem(new CharmedSystem());
    engine.addSystem(new CrippledSystem());
    engine.addSystem(new BleedingSystem());
    engine.addSystem(new BurningSystem());
    engine.addSystem(new DrowningSystem());
    engine.addSystem(new StuckSystem());
    engine.addSystem(new PoisonedSystem());
    engine.addSystem(new SickSystem());
    engine.addSystem(new WetSystem());
    engine.addSystem(new DeathSystem());
  }

  public enum State {
    PLAYING, TARGETING, LOOKING, FOCUSED, MOVING, GOING_DOWN, GOING_UP, WAITING, DEAD
  }

  public enum TargetState {
    ABILITY, RELEASE, THROW
  }
}
