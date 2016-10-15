package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EffectsComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.defects.MyopiaComponent;
import me.dannytatom.xibalba.components.defects.OneArmComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.components.traits.PerceptiveComponent;
import me.dannytatom.xibalba.components.traits.ScoutComponent;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CharacterScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private final Entity player;
  private final AttributesComponent attributes;
  private final SkillsComponent skills;
  private final InventoryComponent inventory;
  private final EquipmentComponent equipment;
  private final Table table;
  private final VerticalGroup attributesGroup;
  private final VerticalGroup skillsGroup;
  private final VerticalGroup traitsGroup;
  private final VerticalGroup inventoryGroup;
  private final VerticalGroup itemDetailsGroup;
  private final VerticalGroup equipmentGroup;
  private final HorizontalGroup itemActionGroup;
  private final Section sectionSelected = Section.INVENTORY;
  private ActionButton cancelButton;
  private ActionButton holdButton;
  private ActionButton wearButton;
  private ActionButton throwButton;
  private ActionButton eatButton;
  private ActionButton skinButton;
  private ActionButton applyButton;
  private ActionButton confirmApplyButton;
  private ActionButton bandageButton;
  private ActionButton removeButton;
  private ActionButton dropButton;
  private Entity applyingItem = null;
  private HashMap<String, Integer> stackedItems;
  private int itemSelected = 0;

  /**
   * View and manage inventory.
   *
   * @param main Instance of the main class
   */
  public CharacterScreen(Main main) {
    this.main = main;

    stage = new Stage(new FitViewport(960, 540));

    player = WorldManager.player;
    attributes = ComponentMappers.attributes.get(player);
    skills = ComponentMappers.skills.get(player);
    inventory = ComponentMappers.inventory.get(player);
    equipment = ComponentMappers.equipment.get(player);

    table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    Table titleTable = new Table();

    HorizontalGroup titleGroup = new HorizontalGroup().align(Align.center | Align.left);
    titleGroup.space(10);
    titleTable.add(titleGroup).pad(10).width(Gdx.graphics.getWidth() - 20).top().left();

    ActionButton closeButton = new ActionButton("ESC", null);
    closeButton.setKeys(Input.Keys.ESCAPE);
    closeButton.setAction(table, () -> main.setScreen(Main.playScreen));
    titleGroup.addActor(closeButton);

    Label title = new Label(attributes.name, Main.skin);
    titleGroup.addActor(title);

    Table mainTable = new Table();

    attributesGroup = new VerticalGroup().align(Align.top | Align.left);
    skillsGroup = new VerticalGroup().align(Align.top | Align.left);
    traitsGroup = new VerticalGroup().align(Align.top | Align.left);
    inventoryGroup = new VerticalGroup().align(Align.top | Align.left);
    itemDetailsGroup = new VerticalGroup().align(Align.top | Align.left);
    equipmentGroup = new VerticalGroup().align(Align.top | Align.left);
    itemActionGroup = new HorizontalGroup().space(5).align(Align.top | Align.left);

    mainTable.add(attributesGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    mainTable.add(skillsGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    mainTable.add(traitsGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    mainTable.row();
    mainTable.add(inventoryGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    mainTable.add(itemDetailsGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    mainTable.add(equipmentGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();

    table.add(titleTable);
    table.row();
    table.add(mainTable);

    setupActionButtons();
    updateAttributesGroup();
    updateSkillsGroup();
    updateTraitsGroup();
    updateInventoryGroup();
    updateItemDetailsGroup();
    updateEquipmentGroup();

    Gdx.input.setInputProcessor(stage);
    stage.setKeyboardFocus(table);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(
        Colors.get("screenBackground").r,
        Colors.get("screenBackground").g,
        Colors.get("screenBackground").b,
        Colors.get("screenBackground").a
    );

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
      switch (sectionSelected) {
        case INVENTORY:
          if (itemSelected > 0) {
            ItemComponent nextItem = ComponentMappers.item.get(inventory.items.get(itemSelected - 1));

            if (stackedItems.get(nextItem.name) == null) {
              itemSelected -= 1;
            } else {
              itemSelected -= stackedItems.get(nextItem.name);
            }

            updateInventoryGroup();
            updateItemDetailsGroup();
          }

          break;
        default:
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      switch (sectionSelected) {
        case INVENTORY:
          if (itemSelected < inventory.items.size() - 1) {
            ItemComponent thisItem = ComponentMappers.item.get(inventory.items.get(itemSelected));

            if (stackedItems.get(thisItem.name) == null) {
              itemSelected += 1;
            } else {
              itemSelected += stackedItems.get(thisItem.name);
            }

            updateInventoryGroup();
            updateItemDetailsGroup();
          }

          break;
        default:
      }
    }

    stage.act(delta);
    stage.draw();
  }

  private void updateAttributesGroup() {
    attributesGroup.clear();

    attributesGroup.addActor(new Label("Attributes", Main.skin));
    attributesGroup.addActor(new Label("", Main.skin));

    // Health

    String healthColor;

    if (attributes.health / attributes.maxHealth <= 0.5f) {
      healthColor = "[RED]";
    } else {
      healthColor = "[WHITE]";
    }

    attributesGroup.addActor(
        new Label("[LIGHT_GRAY]HP " + healthColor + attributes.health + "[LIGHT_GRAY]/" + attributes.maxHealth, Main.skin)
    );

    // Toughness & defense

    int toughness = attributes.toughness;
    int defense = WorldManager.entityHelpers.getArmorDefense(player);

    attributesGroup.addActor(
        new Label(
            "[LIGHT_GRAY]TUF " + "[YELLOW]" + toughness + "[DARK_GRAY]d "
                + "[LIGHT_GRAY]DEF " + "[YELLOW]" + toughness + "[DARK_GRAY]d "
                + (defense > 0 ? "[LIGHT_GRAY]+ " + "[GREEN]" + defense + "[DARK_GRAY] " : ""),
            Main.skin
        )
    );

    // Strength & damage

    int strength = attributes.strength;
    int damage = 0;

    Entity primaryWeapon = WorldManager.itemHelpers.getRightHand(player);

    if (primaryWeapon != null) {
      damage = ComponentMappers.item.get(primaryWeapon).attributes.get("hitDamage");
    }

    attributesGroup.addActor(
        new Label(
            "[LIGHT_GRAY]STR " + "[CYAN]" + strength + "[DARK_GRAY]d "
                + "[LIGHT_GRAY]DMG " + "[CYAN]" + strength + "[DARK_GRAY]d "
                + (damage > 0 ? "[LIGHT_GRAY]+ " + "[RED]" + damage + "[DARK_GRAY]d" : ""),
            Main.skin
        )
    );

    // Statuses

    Array<String> statuses = new Array<>();

    if (ComponentMappers.crippled.has(player)) {
      statuses.add("[DARK_GRAY]CRIPPLED[]");
    }

    if (ComponentMappers.bleeding.has(player)) {
      statuses.add("[DARK_GRAY]BLEEDING[]");
    }

    if (ComponentMappers.poisoned.has(player)) {
      statuses.add("[DARK_GRAY]POISONED[]");
    }

    if (ComponentMappers.drowning.has(player)) {
      statuses.add("[DARK_GRAY]DROWNING[]");
    }

    if (ComponentMappers.stuck.has(player)) {
      statuses.add("[DARK_GRAY]STUCK[]");
    }

    attributesGroup.addActor(
        new Label(statuses.toString("[LIGHT_GRAY],[] "), Main.skin)
    );
  }

  private void updateSkillsGroup() {
    skillsGroup.clear();

    skillsGroup.addActor(new Label("Skills", Main.skin));
    skillsGroup.addActor(new Label("", Main.skin));

    for (Map.Entry<String, Integer> entry : skills.levels.entrySet()) {
      String skill = entry.getKey();
      Integer level = entry.getValue();

      if (level > 0) {
        String name = WordUtils.capitalize(skill);

        skillsGroup.addActor(new Label(
            "[LIGHT_GRAY]" + name + " [WHITE]" + level + "[DARK_GRAY]d", Main.skin
        ));
      }
    }
  }

  private void updateTraitsGroup() {
    traitsGroup.clear();

    traitsGroup.addActor(new Label("Traits & Defects", Main.skin));
    traitsGroup.addActor(new Label("", Main.skin));

    if (ComponentMappers.oneArm.has(player)) {
      traitsGroup.addActor(
          new Label("[RED]" + OneArmComponent.name + "\n[DARK_GRAY]" + WordUtils.wrap(OneArmComponent.description, 50), Main.skin)
      );
    }

    if (ComponentMappers.myopia.has(player)) {
      traitsGroup.addActor(
          new Label("[RED]" + MyopiaComponent.name + "\n[DARK_GRAY]" + WordUtils.wrap(MyopiaComponent.description, 50), Main.skin)
      );
    }

    if (ComponentMappers.scout.has(player)) {
      traitsGroup.addActor(
          new Label("[GREEN]" + ScoutComponent.name + "\n[DARK_GRAY]" + WordUtils.wrap(ScoutComponent.description, 50), Main.skin)
      );
    }

    if (ComponentMappers.perceptive.has(player)) {
      traitsGroup.addActor(
          new Label("[GREEN]" + PerceptiveComponent.name + "\n[DARK_GRAY]" + WordUtils.wrap(PerceptiveComponent.description, 50), Main.skin)
      );
    }
  }

  private void updateInventoryGroup() {
    inventoryGroup.clear();

    inventoryGroup.addActor(new Label("Inventory", Main.skin));
    inventoryGroup.addActor(new Label("", Main.skin));

    Collections.sort(inventory.items, (e1, e2) -> {
      ItemComponent e1i = ComponentMappers.item.get(e1);
      ItemComponent e2i = ComponentMappers.item.get(e2);

      return e1i.name.compareTo(e2i.name);
    });

    stackedItems = new HashMap<>();

    for (int i = 0; i < inventory.items.size(); i++) {
      Entity item = inventory.items.get(i);
      ItemComponent details = ComponentMappers.item.get(item);

      if (Objects.equals(details.type, "ammunition") || Objects.equals(details.type, "consumable") || Objects.equals(details.type, "bandage")) {
        if (stackedItems.containsKey(details.name)) {
          stackedItems.put(details.name, stackedItems.get(details.name) + 1);
        } else {
          stackedItems.put(details.name, 1);
        }
      }
    }

    for (int i = 0; i < inventory.items.size(); i++) {
      Entity item = inventory.items.get(i);
      ItemComponent details = ComponentMappers.item.get(item);

      if (stackedItems.containsKey(details.name)) {
        inventoryGroup.addActor(new Label(createInventoryItemText(i, item, stackedItems.get(details.name)), Main.skin));
        i = i + stackedItems.get(details.name) - 1;
      } else {
        inventoryGroup.addActor(new Label(createInventoryItemText(i, item, 1), Main.skin));
      }
    }
  }

  private void updateItemDetailsGroup() {
    itemDetailsGroup.clear();
    itemActionGroup.clear();

    if (!inventory.items.isEmpty()) {
      Entity item = inventory.items.get(itemSelected);
      ItemComponent details = ComponentMappers.item.get(item);

      itemDetailsGroup.addActor(new Label(details.name, Main.skin));
      itemDetailsGroup.addActor(new Label("", Main.skin));

      // Description

      if (WorldManager.itemHelpers.isIdentified(player, item)) {
        if (details.description != null) {
          String description = "[LIGHT_GRAY]" + details.description;
          itemDetailsGroup.addActor(new Label(WordUtils.wrap(description, 50), Main.skin));
        }

        if (details.twoHanded) {
          itemDetailsGroup.addActor(new Label("", Main.skin));
          itemDetailsGroup.addActor(new Label("[LIGHT_GRAY]Two handed", Main.skin));
        }

        itemDetailsGroup.addActor(new Label("", Main.skin));
      }

      VerticalGroup statsGroup = new VerticalGroup().align(Align.top | Align.left);
      VerticalGroup restrictionsGroup = new VerticalGroup().align(Align.top | Align.left);

      itemDetailsGroup.addActor(statsGroup);
      itemDetailsGroup.addActor(restrictionsGroup);
      itemDetailsGroup.addActor(itemActionGroup);

      boolean itemIsEquipped = WorldManager.itemHelpers.isEquipped(player, item);

      // Where the item is equipped if it is equipped

      if (itemIsEquipped) {
        statsGroup.addActor(
            new Label(
                "[YELLOW]* [LIGHT_GRAY]Using in "
                    + WorldManager.itemHelpers.getLocation(player, item),
                Main.skin
            )
        );

        statsGroup.addActor(new Label("", Main.skin));
      }

      // Item stats

      if (WorldManager.itemHelpers.isIdentified(player, item)) {
        if (details.attributes != null) {
          // Defense

          if (details.attributes.get("defense") != null) {
            String string = "[LIGHT_GRAY]DEF " + "[GREEN]" + details.attributes.get("defense");

            if (!itemIsEquipped && details.location != null) {
              Entity itemInSlot = equipment.slots.get(details.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("defense") != null) {
                  string += "[DARK_GRAY] -> [GREEN]" + itemInSlotDetails.attributes.get("defense");
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }

          // Hit damage

          if (details.attributes.get("hitDamage") != null) {
            String string = "[LIGHT_GRAY]HIT DMG " + "[RED]" + details.attributes.get("hitDamage") + "[DARK_GRAY]d";

            if (!itemIsEquipped && details.location != null) {
              Entity itemInSlot = equipment.slots.get(details.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("hitDamage") != null) {
                  string += "[DARK_GRAY] -> [RED]" + itemInSlotDetails.attributes.get("hitDamage") + "[DARK_GRAY]d";
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }

          // Throw damage

          if (details.attributes.get("throwDamage") != null) {
            String string = "[LIGHT_GRAY]THR DMG " + "[RED]" + details.attributes.get("throwDamage") + "[DARK_GRAY]d";

            if (!itemIsEquipped && details.location != null) {
              Entity itemInSlot = equipment.slots.get(details.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("throwDamage") != null) {
                  string += "[DARK_GRAY] -> [RED]" + itemInSlotDetails.attributes.get("throwDamage") + "[DARK_GRAY]d";
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }

          // Shot damage

          if (details.attributes.get("shotDamage") != null) {
            String string = "[LIGHT_GRAY]SHOT DMG " + "[RED]" + details.attributes.get("shotDamage") + "[DARK_GRAY]d";

            if (!itemIsEquipped && details.location != null) {
              Entity itemInSlot = equipment.slots.get(details.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("shotDamage") != null) {
                  string += "[DARK_GRAY] -> [RED]" + itemInSlotDetails.attributes.get("shotDamage") + "[DARK_GRAY]d";
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }
        }

        // Item effects

        EffectsComponent selectedItemEffects = ComponentMappers.effects.get(item);

        if (selectedItemEffects != null) {
          for (Map.Entry<String, String> entry : selectedItemEffects.effects.entrySet()) {
            String event = entry.getKey();
            String action = entry.getValue();
            String[] split = action.split(":");

            String prettyEvent = WordUtils.capitalize(
                String.join(" ", (CharSequence[]) event.split("(?<=[a-z])(?=[A-Z])"))
            );

            String prettyEffect = WordUtils.capitalize(
                String.join(" ", (CharSequence[]) split[0].split("(?<=[a-z])(?=[A-Z])"))
            );

            String string = "[DARK_GRAY]" + prettyEvent + " [LIGHT_GRAY]" + prettyEffect + " [CYAN]" + split[1];

            statsGroup.addActor(new Label(string, Main.skin));
          }
        }

        statsGroup.addActor(new Label("", Main.skin));
      }

      // Restrictions

      if (ComponentMappers.oneArm.has(player) && details.twoHanded) {
        restrictionsGroup.addActor(new Label("[RED]You can't hold this due to too few arms.", Main.skin));
      }

      if (ComponentMappers.oneArm.has(player) && Objects.equals(details.location, "left hand")) {
        restrictionsGroup.addActor(new Label("[RED]You don't have a left hand to hold this in.", Main.skin));
      }

      if (restrictionsGroup.getChildren().size > 0) {
        restrictionsGroup.addActor(new Label("", Main.skin));
      }

      // Actions

      if (applyingItem == null) {
        if (details.actions.contains("hold", false) && !itemIsEquipped) {
          if (!ComponentMappers.oneArm.has(player) || !details.twoHanded) {
            itemActionGroup.addActor(holdButton);
          }
        }

        if (details.actions.contains("wear", false) && !itemIsEquipped) {
          if (!ComponentMappers.oneArm.has(player) || !Objects.equals(details.location, "left hand")) {
            itemActionGroup.addActor(wearButton);
          }
        }

        if (details.actions.contains("throw", false)) {
          itemActionGroup.addActor(throwButton);
        }

        if (details.actions.contains("consume", false) && !itemIsEquipped) {
          itemActionGroup.addActor(eatButton);
        }

        if (details.actions.contains("skin", false) && !itemIsEquipped) {
          itemActionGroup.addActor(skinButton);
        }

        if (details.actions.contains("bandage", false) && ComponentMappers.bleeding.has(player)) {
          itemActionGroup.addActor(bandageButton);
        }

        if (details.actions.contains("apply", false)) {
          itemActionGroup.addActor(applyButton);
        }

        if (itemIsEquipped) {
          itemActionGroup.addActor(removeButton);
        }

        if (!itemIsEquipped) {
          itemActionGroup.addActor(dropButton);
        }
      } else {
        itemActionGroup.addActor(cancelButton);

        if (applyingItem != item && details.actions.contains("applyTo", false)) {
          itemActionGroup.addActor(confirmApplyButton);
        }
      }
    }
  }

  private void updateEquipmentGroup() {
    equipmentGroup.clear();

    equipmentGroup.addActor(new Label("Equipment", Main.skin));
    equipmentGroup.addActor(new Label("", Main.skin));

    int index = 0;

    for (java.util.Map.Entry<String, Entity> slot : equipment.slots.entrySet()) {
      String key = WordUtils.capitalize(slot.getKey());
      Entity item = slot.getValue();

      if (!ComponentMappers.oneArm.has(player) || !Objects.equals(slot.getKey(), "left hand")) {
        equipmentGroup.addActor(
            new Label(createEquipmentSlotText(index, key, item), Main.skin)
        );
      }

      index++;
    }
  }

  private String createInventoryItemText(int index, Entity item, int amount) {
    String name = WorldManager.itemHelpers.getName(player, item);

    if (WorldManager.itemHelpers.isEquipped(player, item)) {
      name += " [YELLOW]*";
    }

    if (sectionSelected == Section.INVENTORY && index == itemSelected) {
      return "[DARK_GRAY]> [WHITE]" + name + (amount > 1 ? " [WHITE]x" + amount : "");
    } else {
      return "[LIGHT_GRAY]" + name + (amount > 1 ? " [WHITE]x" + amount : "");
    }
  }

  private String createEquipmentSlotText(int index, String slot, Entity item) {
    String itemName = item == null
        ? " [DARK_GRAY]Nothing"
        : " [WHITE]" + WorldManager.itemHelpers.getName(player, item);

    if (sectionSelected == Section.EQUIPMENT && index == itemSelected) {
      return "[DARK_GRAY]> [WHITE]" + slot + itemName;
    } else {
      return "[LIGHT_GRAY]" + slot + itemName;
    }
  }

  private void setupActionButtons() {
    cancelButton = new ActionButton("Q", "Cancel");
    cancelButton.setKeys(Input.Keys.Q);
    cancelButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(cancelButton, true)) {
        if (applyingItem != null) {
          applyingItem = null;

          updateInventoryGroup();
          updateItemDetailsGroup();
          updateEquipmentGroup();
        }
      }
    });

    holdButton = new ActionButton("H", "Hold");
    holdButton.setKeys(Input.Keys.H);
    holdButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(holdButton, true)) {
        WorldManager.itemHelpers.hold(player, inventory.items.get(itemSelected));

        updateAttributesGroup();
        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    wearButton = new ActionButton("W", "Wear");
    wearButton.setKeys(Input.Keys.W);
    wearButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(wearButton, true)) {
        WorldManager.itemHelpers.wear(player, inventory.items.get(itemSelected));

        updateAttributesGroup();
        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    throwButton = new ActionButton("T", "Throw");
    throwButton.setKeys(Input.Keys.T);
    throwButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(throwButton, true)) {
        ComponentMappers.item.get(inventory.items.get(itemSelected)).throwing = true;

        WorldManager.inputHelpers.startTargeting();

        main.setScreen(Main.playScreen);
      }
    });

    eatButton = new ActionButton("E", "Eat");
    eatButton.setKeys(Input.Keys.E);
    eatButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(eatButton, true)) {
        WorldManager.itemHelpers.eat(player, inventory.items.get(itemSelected));

        itemSelected = 0;

        updateAttributesGroup();
        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    skinButton = new ActionButton("S", "Skin");
    skinButton.setKeys(Input.Keys.S);
    skinButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(skinButton, true)) {
        Vector2 position = ComponentMappers.position.get(player).pos;
        int amount = MathUtils.random(1, 6);

        for (int i = 0; i < amount; i++) {
          Entity skin = WorldManager.entityFactory.createSkin(inventory.items.get(itemSelected), position);
          WorldManager.itemHelpers.addToInventory(player, skin, false);
        }

        WorldManager.itemHelpers.destroy(player, inventory.items.get(itemSelected));
        WorldManager.log.add("You got " + amount + " skins from the corpse");

        itemSelected = 0;

        updateInventoryGroup();
        updateItemDetailsGroup();
      }
    });

    bandageButton = new ActionButton("B", "Bandage");
    bandageButton.setKeys(Input.Keys.B);
    bandageButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(bandageButton, true)) {
        player.remove(BleedingComponent.class);
        WorldManager.itemHelpers.destroy(player, inventory.items.get(itemSelected));

        WorldManager.log.add("You have bandaged your bleeding wound");

        itemSelected = 0;

        updateAttributesGroup();
        updateInventoryGroup();
        updateItemDetailsGroup();
      }
    });

    applyButton = new ActionButton("A", "Apply");
    applyButton.setKeys(Input.Keys.A);
    applyButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(applyButton, true)) {
        applyingItem = inventory.items.get(itemSelected);

        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    confirmApplyButton = new ActionButton("ENTER", "Apply to this");
    confirmApplyButton.setKeys(Input.Keys.ENTER);
    confirmApplyButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(confirmApplyButton, true)) {
        WorldManager.itemHelpers.apply(
            player, applyingItem, inventory.items.get(itemSelected)
        );

        itemSelected = 0;
        applyingItem = null;

        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    removeButton = new ActionButton("R", "Remove");
    removeButton.setKeys(Input.Keys.R);
    removeButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(removeButton, true)) {
        WorldManager.itemHelpers.remove(player, inventory.items.get(itemSelected));

        updateAttributesGroup();
        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    dropButton = new ActionButton("D", "Drop");
    dropButton.setKeys(Input.Keys.D);
    dropButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(dropButton, true)) {
        WorldManager.itemHelpers.drop(player, inventory.items.get(itemSelected));

        itemSelected = 0;

        updateAttributesGroup();
        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  private enum Section {
    INVENTORY, EQUIPMENT
  }
}
