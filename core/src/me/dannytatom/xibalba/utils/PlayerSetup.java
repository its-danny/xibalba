package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.BodyComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.PositionComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.VisualComponent;
import me.dannytatom.xibalba.components.defects.OneArmComponent;
import me.dannytatom.xibalba.components.traits.ScoutComponent;
import me.dannytatom.xibalba.world.WorldManager;

import java.util.Calendar;
import java.util.TreeMap;

public class PlayerSetup {
  private static final TreeMap<Integer, String> rnMap = new TreeMap<>();

  public AttributesComponent attributes;
  public SkillsComponent skills;
  public Array<String> traits;
  public Array<String> defects;

  public String name;

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

    generateName();

    attributes = new AttributesComponent(name, "It's you", 100, 10, 4, 4, 4);
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

  public Entity create() {
    Entity player = new Entity();

    player.add(skills);
    player.add(attributes);

    Vector2 position = WorldManager.mapHelpers.getEntrancePosition();
    player.add(new PositionComponent(position));
    player.add(new VisualComponent(Main.asciiAtlas.createSprite("0004"), position));

    player.add(new PlayerComponent());
    player.add(new InventoryComponent());
    player.add(new EquipmentComponent());

    TreeMap<String, Integer> bodyParts = new TreeMap<>();
    bodyParts.put("head", 10);
    bodyParts.put("body", 8);
    bodyParts.put("left arm", 10);
    bodyParts.put("right arm", 10);
    bodyParts.put("left leg", 10);
    bodyParts.put("right leg", 10);
    player.add(new BodyComponent(bodyParts));

    if (defects.contains(OneArmComponent.name, false)) {
      player.add(new OneArmComponent());

      BodyComponent body = ComponentMappers.body.get(player);
      body.parts.remove("left arm");

      AttributesComponent attributes = ComponentMappers.attributes.get(player);
      attributes.maxHealth = MathUtils.ceil(attributes.maxHealth - (attributes.maxHealth * .20f));
      attributes.health = attributes.maxHealth;
    }

    if (traits.contains(ScoutComponent.name, false)) {
      player.add(new ScoutComponent());

      AttributesComponent attributes = ComponentMappers.attributes.get(player);
      attributes.maxVision = 120;
      attributes.vision = 120;
    }

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
