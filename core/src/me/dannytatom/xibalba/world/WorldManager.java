package me.dannytatom.xibalba.world;

import aurelienribon.tweenengine.Tween;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.ActionLog;
import me.dannytatom.xibalba.helpers.*;
import me.dannytatom.xibalba.systems.*;
import me.dannytatom.xibalba.systems.actions.ExploreSystem;
import me.dannytatom.xibalba.systems.actions.MeleeSystem;
import me.dannytatom.xibalba.systems.actions.MovementSystem;
import me.dannytatom.xibalba.systems.actions.RangeSystem;
import me.dannytatom.xibalba.systems.statuses.*;
import me.dannytatom.xibalba.utils.EntityFactory;

public class WorldManager {
  public static Engine engine;
  public static ActionLog log;
  public static World world;
  public static State state;
  public static Array<Tween> tweens;
  public static EntityFactory entityFactory;
  public static InputHelpers inputHelpers;
  public static MapHelpers mapHelpers;
  public static CombatHelpers combatHelpers;
  public static EntityHelpers entityHelpers;
  public static ItemHelpers itemHelpers;
  public static AbilityHelpers abilityHelpers;
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
    abilityHelpers = new AbilityHelpers();

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
}
