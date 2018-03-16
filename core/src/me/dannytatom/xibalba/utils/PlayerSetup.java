package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Calendar;
import java.util.TreeMap;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.abilities.Ability;
import me.dannytatom.xibalba.components.AbilitiesComponent;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.DefectsComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.GodComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.TraitsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.effects.Charm;
import me.dannytatom.xibalba.effects.JumpOverEnemy;
import me.dannytatom.xibalba.effects.Knockback;
import me.dannytatom.xibalba.utils.yaml.GodData;
import me.dannytatom.xibalba.world.WorldManager;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class PlayerSetup {
  private static final TreeMap<Integer, String> rnMap = new TreeMap<>();

  public final AttributesComponent attributes;
  public final SkillsComponent skills;
  public final Array<String> traits;
  public final Array<String> defects;
  public GodData godData;

  public String color;
  public String name;

  /**
   * Setup player defaults. This is used during character creation.
   */
  public PlayerSetup() {
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

    color = "FFFFFF";
    generateName();

    attributes = new AttributesComponent(
        name, "It's you", AttributesComponent.Type.HUMAN,
        100, 10, 5, 4, 4, 4
    );
    skills = new SkillsComponent();
    traits = new Array<>();
    defects = new Array<>();
  }

  private static String intToRoman(int number) {
    int floored = rnMap.floorKey(number);

    if (number == floored) {
      return rnMap.get(number);
    }

    return rnMap.get(floored) + intToRoman(number - floored);
  }

  /**
   * Create the player. </p> This is called when character creation is done and we want to create
   * the actual entity.
   *
   * @return The player entity
   */
  public Entity create() {
    attributes.maxHealth = attributes.toughness * 10;
    attributes.health = attributes.maxHealth;

    attributes.maxOxygen = attributes.toughness * 4;
    attributes.oxygen = attributes.maxOxygen;

    Entity player = new Entity();

    player.add(skills);
    player.add(attributes);

    Vector2 position = WorldManager.mapHelpers.getRandomOpenPositionOnLand(
        WorldManager.world.currentMapIndex
    );

    player.add(new PositionComponent(position));

    player.add(
        new VisualComponent(Main.asciiAtlas.createSprite("0004"), position, Main.parseColor(color))
    );

    player.add(new PlayerComponent());

    TreeMap<String, Integer> bodyParts = new TreeMap<>();
    bodyParts.put("head", 10);
    bodyParts.put("body", 8);
    bodyParts.put("left arm", 10);
    bodyParts.put("right arm", 10);
    bodyParts.put("left leg", 10);
    bodyParts.put("right leg", 10);
    player.add(new BodyComponent(bodyParts));

    DefectsComponent defectsComponent = new DefectsComponent();
    player.add(defectsComponent);

    if (this.defects.contains("One arm", false)) {
      defectsComponent.defects.add("One arm");

      BodyComponent body = ComponentMappers.body.get(player);
      body.bodyParts.remove("left arm");

      AttributesComponent attributes = ComponentMappers.attributes.get(player);
      attributes.maxHealth = MathUtils.ceil(attributes.maxHealth - (attributes.maxHealth * .20f));
      attributes.health = attributes.maxHealth;
    }

    if (this.defects.contains("Myopia", false)) {
      defectsComponent.defects.add("Myopia");

      AttributesComponent attributes = ComponentMappers.attributes.get(player);
      attributes.vision = 5;
    }

    TraitsComponent traitsComponent = new TraitsComponent();
    player.add(traitsComponent);

    if (traits.contains("Scout", false)) {
      traitsComponent.traits.add("Scout");

      AttributesComponent attributes = ComponentMappers.attributes.get(player);
      attributes.vision = 20;
    }

    if (traits.contains("Perceptive", false)) {
      traitsComponent.traits.add("Perceptive");

      AttributesComponent attributes = ComponentMappers.attributes.get(player);
      attributes.hearing = attributes.vision * 2;
    }

    if (traits.contains("Carnivore", false)) {
      traitsComponent.traits.add("Carnivore");
    }

    if (traits.contains("Quick", false)) {
      traitsComponent.traits.add("Quick");

      AttributesComponent attributes = ComponentMappers.attributes.get(player);
      attributes.speed = attributes.speed + MathUtils.round(attributes.speed * .5f);
    }

    WorldManager.god = new Entity();
    WorldManager.god.add(
        new GodComponent(
            godData.name,
            godData.hates, godData.likes,
            godData.wrath
        )
    );

    Constructor constructor = new Constructor(Ability.class);
    constructor.addTypeDescription(new TypeDescription(Charm.class, "!Charm"));
    constructor.addTypeDescription(new TypeDescription(JumpOverEnemy.class, "!JumpOverEnemy"));
    constructor.addTypeDescription(new TypeDescription(Knockback.class, "!Knockback"));
    Yaml yaml = new Yaml(constructor);

    AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
    godData.abilities.forEach((String abilityKey) -> {
      Ability ability = (Ability) yaml.load(Main.abilitiesData.get(abilityKey));
      abilitiesComponent.abilities.put(abilityKey, ability);
    });

    Ability jumpOverEnemy = (Ability) yaml.load(Main.abilitiesData.get("jumpOverEnemy"));
    abilitiesComponent.abilities.put("jumpOverEnemy", jumpOverEnemy);
    Ability knockback = (Ability) yaml.load(Main.abilitiesData.get("knockback"));
    abilitiesComponent.abilities.put("knockback", knockback);

    player.add(abilitiesComponent);

    player.add(new InventoryComponent());
    player.add(new EquipmentComponent());

    Entity shield = WorldManager.entityFactory.createItem("shield", new Vector2(0, 0));
    WorldManager.itemHelpers.addToInventory(player, shield, false);
    Entity chippedFlint = WorldManager.entityFactory.createItem("chippedFlint", new Vector2(0, 0));
    WorldManager.itemHelpers.addToInventory(player, chippedFlint, false);
    Entity torch = WorldManager.entityFactory.createItem("torch", new Vector2(0, 0));
    WorldManager.itemHelpers.addToInventory(player, torch, false);

    return player;
  }

  private void generateName() {
    String preceding;
    String[] names;

    if (MathUtils.randomBoolean()) {
      preceding = intToRoman(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
      names = Gdx.files.internal("data/names/male").readString().split("\\r?\\n");
    } else {
      preceding = "IX";
      names = Gdx.files.internal("data/names/female").readString().split("\\r?\\n");
    }

    name = preceding + " " + names[MathUtils.random(0, names.length - 1)];
  }
}
