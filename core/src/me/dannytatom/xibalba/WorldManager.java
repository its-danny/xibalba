package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.helpers.CombatHelpers;
import me.dannytatom.xibalba.helpers.EffectsHelpers;
import me.dannytatom.xibalba.helpers.EntityHelpers;
import me.dannytatom.xibalba.helpers.EquipmentHelpers;
import me.dannytatom.xibalba.helpers.InventoryHelpers;
import me.dannytatom.xibalba.helpers.MapHelpers;
import me.dannytatom.xibalba.helpers.SkillHelpers;
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

import java.util.Calendar;
import java.util.TreeMap;

public class WorldManager {
  private static final TreeMap<Integer, String> rnMap = new TreeMap<>();
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
  public static EffectsHelpers effectsHelpers;
  public static Entity player;
  public static boolean executeTurn;
  public static int turnCount;

  /**
   * Setup a whole bunch of shit.
   */
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
    effectsHelpers = new EffectsHelpers();

    executeTurn = false;
    turnCount = 0;

    // Setup engine (systems are run in order added)
    engine.addSystem(new AttributesSystem());
    engine.addSystem(new MouseMovementSystem());
    engine.addSystem(new BrainSystem());
    engine.addSystem(new WanderSystem());
    engine.addSystem(new TargetSystem());
    engine.addSystem(new MovementSystem());
    engine.addSystem(new MeleeSystem());
    engine.addSystem(new RangeSystem());
    engine.addSystem(new CrippledSystem());
    engine.addSystem(new BleedingSystem());

    // Roman numeral map
    rnMap.put(1000, "M");
    rnMap.put(900, "CM");
    rnMap.put(500, "D");
    rnMap.put(400, "CD");
    rnMap.put(100, "C");
    rnMap.put(90, "XC");
    rnMap.put(50, "L");
    rnMap.put(40, "XL");
    rnMap.put(10, "X");
    rnMap.put(9, "IX");
    rnMap.put(5, "V");
    rnMap.put(4, "IV");
    rnMap.put(1, "I");

    // Create player
    player = new Entity();
    player.add(new AttributesComponent(createPlayerName(), "It's you", 100, 10, 4, 4));
  }

  private static String createPlayerName() {
    String preceding;
    String[] names;

    if (MathUtils.randomBoolean()) {
      preceding = intToRoman(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
      names = Gdx.files.internal("data/names/male").readString().split("\\r?\\n");
    } else {
      preceding = "IX";
      names = Gdx.files.internal("data/names/female").readString().split("\\r?\\n");
    }

    return preceding + " " + names[MathUtils.random(0, names.length - 1)];
  }

  private static String intToRoman(int number) {
    int floored = rnMap.floorKey(number);

    if (number == floored) {
      return rnMap.get(number);
    }

    return rnMap.get(floored) + intToRoman(number - floored);
  }

  public enum State {
    PLAYING, TARGETING, LOOKING, MOVING, GOING_DOWN, GOING_UP, FOCUSED
  }
}
