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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.CorpseComponent;
import me.dannytatom.xibalba.components.EffectsComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.statuses.BleedingComponent;
import me.dannytatom.xibalba.effects.Effect;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.yaml.DefectData;
import me.dannytatom.xibalba.utils.yaml.TraitData;
import me.dannytatom.xibalba.world.WorldManager;

import org.apache.commons.lang3.text.WordUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class CharacterScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private final Entity player;
  private final AttributesComponent attributes;
  private final SkillsComponent skills;
  private final InventoryComponent inventory;
  private final EquipmentComponent equipment;
  private final ArrayList<TraitData> traits;
  private final ArrayList<DefectData> defects;
  private final Table table;
  private final VerticalGroup attributesGroup;
  private final VerticalGroup skillsGroup;
  private final VerticalGroup traitsGroup;
  private final VerticalGroup defectsGroup;
  private final VerticalGroup inventoryGroup;
  private final VerticalGroup itemDetailsGroup;
  private final VerticalGroup equipmentGroup;
  private final Table itemActionTable;
  private final Section sectionSelected = Section.INVENTORY;
  private ActionButton cancelButton;
  private ActionButton holdButton;
  private ActionButton wearButton;
  private ActionButton throwButton;
  private ActionButton eatButton;
  private ActionButton skinButton;
  private ActionButton dismemberButton;
  private ActionButton applyButton;
  private ActionButton confirmApplyButton;
  private ActionButton bandageButton;
  private ActionButton craftButton;
  private ActionButton removeButton;
  private ActionButton dropButton;
  private Entity applyingItem = null;
  private boolean dismembering = false;
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

    traits = new ArrayList<>();
    Yaml traitYaml = new Yaml(new Constructor(TraitData.class));
    for (Map.Entry<String, String> entry : Main.traitsData.entrySet()) {
      traits.add((TraitData) traitYaml.load(entry.getValue()));
    }

    defects = new ArrayList<>();
    Yaml defectYaml = new Yaml(new Constructor(DefectData.class));
    for (Map.Entry<String, String> entry : Main.defectsData.entrySet()) {
      defects.add((DefectData) defectYaml.load(entry.getValue()));
    }

    table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    Table titleTable = new Table();

    HorizontalGroup titleGroup = new HorizontalGroup();
    titleGroup.space(10);
    titleTable.add(titleGroup).pad(10).width(Gdx.graphics.getWidth() - 20);

    ActionButton closeButton = new ActionButton("Q", null);
    closeButton.setKeys(Input.Keys.Q);
    closeButton.setAction(table, () -> main.setScreen(Main.playScreen));
    titleGroup.addActor(closeButton);

    Label title = new Label(attributes.name, Main.skin);
    titleGroup.addActor(title);

    attributesGroup = new VerticalGroup().top().left().columnLeft();
    skillsGroup = new VerticalGroup().top().left().columnLeft();
    traitsGroup = new VerticalGroup().top().left().columnLeft();
    defectsGroup = new VerticalGroup().top().left().columnLeft();
    inventoryGroup = new VerticalGroup().top().left().columnLeft();
    itemDetailsGroup = new VerticalGroup().top().left().columnLeft();
    equipmentGroup = new VerticalGroup().top().left().columnLeft();
    itemActionTable = new Table();

    int topTableSectionSmallWidth = Gdx.graphics.getWidth() / 6 - 20;
    int topTableSectionLargeWidth = Gdx.graphics.getWidth() / 6 * 2 - 20;
    Table topTable = new Table();
    topTable.add(attributesGroup).pad(10).width(topTableSectionSmallWidth).top().left();
    topTable.add(skillsGroup).pad(10).width(topTableSectionSmallWidth).top().left();
    topTable.add(traitsGroup).pad(10).width(topTableSectionLargeWidth).top().left();
    topTable.add(defectsGroup).pad(10).width(topTableSectionLargeWidth).top().left();

    int bottomTableSectionWidth = Gdx.graphics.getWidth() / 3 - 20;
    Table bottomTable = new Table();
    bottomTable.add(inventoryGroup).pad(10).width(bottomTableSectionWidth).top().left();
    bottomTable.add(itemDetailsGroup).pad(10).width(bottomTableSectionWidth).top().left();
    bottomTable.add(equipmentGroup).pad(10).width(bottomTableSectionWidth).top().left();

    table.add(titleTable);
    table.row();
    table.add(topTable);
    table.row();
    table.add(bottomTable);

    setupActionButtons();
    updateAttributesGroup();
    updateSkillsGroup();
    updateTraitsGroup();
    updateDefectsGroup();
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
      dismembering = false;
      applyingItem = null;

      switch (sectionSelected) {
        case INVENTORY:
          if (itemSelected > 0) {
            ItemComponent nextItem
                = ComponentMappers.item.get(inventory.items.get(itemSelected - 1));

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
      dismembering = false;
      applyingItem = null;

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
        new Label(
            "[LIGHT_GRAY]HP " + healthColor + attributes.health
                + "[LIGHT_GRAY]/" + attributes.maxHealth, Main.skin
        )
    );

    // Divine Favor
    String divineFavorColor;

    if (attributes.divineFavor <= 0) {
      divineFavorColor = "[RED]";
    } else if (attributes.divineFavor / 100 <= 0.5f) {
      divineFavorColor = "[YELLOW]";
    } else {
      divineFavorColor = "[WHITE]";
    }

    attributesGroup.addActor(
        new Label(
            "[LIGHT_GRAY]DF " + divineFavorColor + Math.round(attributes.divineFavor)
                + "[LIGHT_GRAY]/100", Main.skin
        )
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

    // Speed
    attributesGroup.addActor(new Label("[LIGHT_GRAY]Speed []" + attributes.speed, Main.skin));

    // Encumbrance
    attributesGroup.addActor(
        new Label(
            "[LIGHT_GRAY]Carrying [WHITE]" + WorldManager.itemHelpers.getTotalWeight(player)
                + "[DARK_GRAY]/" + attributes.strength * 5 + "lb",
            Main.skin
        )
    );

    // Statuses
    Array<String> statuses = new Array<>();

    if (ComponentMappers.encumbered.has(player)) {
      statuses.add("[DARK_GRAY]ENCUMBERED[]");
    }

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

    traitsGroup.addActor(new Label("Traits", Main.skin));
    traitsGroup.addActor(new Label("", Main.skin));

    for (TraitData traitData : this.traits) {
      if (WorldManager.entityHelpers.hasTrait(WorldManager.player, traitData.name)) {
        traitsGroup.addActor(
            new Label(
                "[GREEN]" + traitData.name + "\n[DARK_GRAY]" + WordUtils.wrap(
                    traitData.description, 50
                ), Main.skin
            )
        );
      }
    }
  }

  private void updateDefectsGroup() {
    defectsGroup.clear();

    defectsGroup.addActor(new Label("Defects", Main.skin));
    defectsGroup.addActor(new Label("", Main.skin));

    for (DefectData defectData : this.defects) {
      if (WorldManager.entityHelpers.hasDefect(WorldManager.player, defectData.name)) {
        defectsGroup.addActor(
            new Label(
                "[RED]" + defectData.name + "\n[DARK_GRAY]" + WordUtils.wrap(
                    defectData.description, 50
                ), Main.skin
            )
        );
      }
    }
  }

  private void updateInventoryGroup() {
    inventoryGroup.clear();

    inventoryGroup.addActor(new Label("Inventory", Main.skin));
    inventoryGroup.addActor(new Label("", Main.skin));

    (inventory.items).sort((e1, e2) -> {
      ItemComponent e1i = ComponentMappers.item.get(e1);
      ItemComponent e2i = ComponentMappers.item.get(e2);

      return e1i.name.compareTo(e2i.name);
    });

    stackedItems = new HashMap<>();

    for (int i = 0; i < inventory.items.size(); i++) {
      Entity item = inventory.items.get(i);
      ItemComponent details = ComponentMappers.item.get(item);

      if (Objects.equals(details.type, "ammunition")
          || Objects.equals(details.type, "consumable")
          || Objects.equals(details.type, "bandage")) {
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
        inventoryGroup.addActor(
            new Label(createInventoryItemText(i, item, stackedItems.get(details.name)), Main.skin)
        );

        i = i + stackedItems.get(details.name) - 1;
      } else {
        inventoryGroup.addActor(new Label(createInventoryItemText(i, item, 1), Main.skin));
      }
    }
  }

  private void updateItemDetailsGroup() {
    itemDetailsGroup.clear();
    itemActionTable.clear();

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

        itemDetailsGroup.addActor(new Label("", Main.skin));

        itemDetailsGroup.addActor(
            new Label(
                "[DARK_GRAY]Quality: [LIGHT_GRAY]"
                    + ComponentMappers.item.get(item).quality.toString(),
                Main.skin
            )
        );

        if (WorldManager.itemHelpers.hasStoneMaterial(item)) {
          itemDetailsGroup.addActor(
              new Label(
                  "[DARK_GRAY]Materials: [LIGHT_GRAY]"
                      + WorldManager.itemHelpers.getStoneMaterial(item).toString(),
                  Main.skin
              )
          );
        }

        if (details.twoHanded) {
          itemDetailsGroup.addActor(new Label("", Main.skin));
          itemDetailsGroup.addActor(new Label("[LIGHT_GRAY]Two handed", Main.skin));
        }

        itemDetailsGroup.addActor(new Label("", Main.skin));
      }

      // Weight

      itemDetailsGroup.addActor(new Label("", Main.skin));
      itemDetailsGroup.addActor(new Label("[LIGHT_GRAY]" + details.weight + "lb", Main.skin));

      // Stats & restrictions

      VerticalGroup statsGroup = new VerticalGroup().top().left().columnLeft();
      VerticalGroup restrictionsGroup = new VerticalGroup().top().left().columnLeft();

      itemDetailsGroup.addActor(statsGroup);
      itemDetailsGroup.addActor(restrictionsGroup);
      itemDetailsGroup.addActor(itemActionTable);

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
            String string = "[LIGHT_GRAY]HIT DMG " + "[RED]"
                + details.attributes.get("hitDamage") + "[DARK_GRAY]d";

            if (!itemIsEquipped && details.location != null) {
              Entity itemInSlot = equipment.slots.get(details.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("hitDamage") != null) {
                  string += "[DARK_GRAY] -> [RED]"
                      + itemInSlotDetails.attributes.get("hitDamage") + "[DARK_GRAY]d";
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }

          // Throw damage

          if (details.attributes.get("throwDamage") != null) {
            String string = "[LIGHT_GRAY]THR DMG "
                + "[RED]" + details.attributes.get("throwDamage") + "[DARK_GRAY]d";

            if (!itemIsEquipped && details.location != null) {
              Entity itemInSlot = equipment.slots.get(details.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("throwDamage") != null) {
                  string += "[DARK_GRAY] -> [RED]"
                      + itemInSlotDetails.attributes.get("throwDamage") + "[DARK_GRAY]d";
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }

          // Shot damage

          if (details.attributes.get("shotDamage") != null) {
            String string = "[LIGHT_GRAY]SHOT DMG " + "[RED]"
                + details.attributes.get("shotDamage") + "[DARK_GRAY]d";

            if (!itemIsEquipped && details.location != null) {
              Entity itemInSlot = equipment.slots.get(details.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("shotDamage") != null) {
                  string += "[DARK_GRAY] -> [RED]"
                      + itemInSlotDetails.attributes.get("shotDamage") + "[DARK_GRAY]d";
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }
        }

        // Item effects

        EffectsComponent selectedItemEffects = ComponentMappers.effects.get(item);

        if (selectedItemEffects != null) {
          for (Effect effect : selectedItemEffects.effects) {
            String prettyEvent = WordUtils.capitalize(effect.trigger.toString());
            String prettyEffect = WordUtils.capitalize(effect.type.toString());

            String string = "[DARK_GRAY]" + prettyEvent + " [LIGHT_GRAY]" + prettyEffect;

            statsGroup.addActor(new Label(string, Main.skin));
          }
        }

        statsGroup.addActor(new Label("", Main.skin));
      }

      // Restrictions

      if (WorldManager.entityHelpers.hasDefect(player, "One arm") && details.twoHanded) {
        restrictionsGroup.addActor(
            new Label("[RED]You can't hold this due to too few arms.", Main.skin)
        );
      }

      if (WorldManager.entityHelpers.hasDefect(player, "One arm")
          && Objects.equals(details.location, "left hand")) {
        restrictionsGroup.addActor(
            new Label("[RED]You don't have a left hand to hold this in.", Main.skin)
        );
      }

      if (restrictionsGroup.getChildren().size > 0) {
        restrictionsGroup.addActor(new Label("", Main.skin));
      }

      // Actions

      if (dismembering) {
        Entity corpse = inventory.items.get(itemSelected);
        CorpseComponent body = ComponentMappers.corpse.get(corpse);

        itemActionTable.add(cancelButton).pad(0, 0, 5, 5);

        int actionNumber = 0;

        for (String part : body.bodyParts.keySet()) {
          if (Objects.equals(part, "body")) {
            continue;
          }

          actionNumber++;

          // If you look at the docs for Input.Keys, number keys are offset by 7
          // (e.g. 0 = 7, 1 = 8, etc)
          ActionButton button = new ActionButton(actionNumber, WordUtils.capitalize(part));
          button.setKeys(actionNumber + 7);
          button.setAction(table, () -> {
            if (itemActionTable.getChildren().contains(button, true)) {
              Vector2 position = ComponentMappers.position.get(player).pos;

              Entity limb = WorldManager.entityFactory.createLimb(corpse, part, position);
              WorldManager.world.addEntity(limb);
              WorldManager.itemHelpers.addToInventory(player, limb, false);
              body.bodyParts.remove(part);

              WorldManager.log.add(
                  "inventory.dismembered", part, ComponentMappers.corpse.get(corpse).entity
              );

              if (body.bodyParts.size() == 0
                  || (body.bodyParts.size() == 1 && body.bodyParts.containsKey("body"))) {
                ComponentMappers.item.get(corpse).actions.removeValue("dismember", false);
              }

              dismembering = false;
              itemSelected = 0;

              updateInventoryGroup();
              updateItemDetailsGroup();
            }
          });

          itemActionTable.add(button).pad(0, 0, 5, 5);
        }
      } else if (applyingItem != null) {
        itemActionTable.add(cancelButton).pad(0, 0, 5, 5);

        if (applyingItem != item && details.actions.contains("applyTo", false)) {
          itemActionTable.add(confirmApplyButton).pad(0, 0, 5, 5);
        }
      } else {
        if (details.actions.contains("hold", false) && !itemIsEquipped) {
          if (!WorldManager.entityHelpers.hasDefect(player, "One arm") || !details.twoHanded) {
            itemActionTable.add(holdButton).pad(0, 0, 5, 5);
          }
        }

        if (details.actions.contains("wear", false) && !itemIsEquipped) {
          if (!WorldManager.entityHelpers.hasDefect(player, "One arm")
              || !Objects.equals(details.location, "left hand")) {
            itemActionTable.add(wearButton).pad(0, 0, 5, 5);
          }
        }

        if (details.actions.contains("throw", false)) {
          itemActionTable.add(throwButton).pad(0, 0, 5, 5);
        }

        if (details.actions.contains("consume", false) && !itemIsEquipped) {
          itemActionTable.add(eatButton).pad(0, 0, 5, 5);
        }

        if (details.actions.contains("skin", false) && !itemIsEquipped) {
          itemActionTable.add(skinButton).pad(0, 0, 5, 5);
        }

        if (details.actions.contains("dismember", false) && !itemIsEquipped) {
          itemActionTable.add(dismemberButton).pad(0, 0, 5, 5);
        }

        if (details.actions.contains("bandage", false) && ComponentMappers.bleeding.has(player)) {
          itemActionTable.add(bandageButton).pad(0, 0, 5, 5);
        }

        if (details.type.equals("component")) {
          itemActionTable.add(craftButton).pad(0, 0, 5, 5);
        }

        if (itemActionTable.getChildren().size > 0) {
          itemActionTable.row();
        }

        if (details.actions.contains("apply", false)) {
          itemActionTable.add(applyButton).pad(0, 0, 5, 5);
        }

        if (itemIsEquipped) {
          itemActionTable.add(removeButton).pad(0, 0, 5, 5);
        }

        if (!itemIsEquipped) {
          itemActionTable.add(dropButton).pad(0, 0, 5, 5);
        }

        if (itemActionTable.getChildren().size > 0) {
          itemActionTable.row();
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

      if (!WorldManager.entityHelpers.hasDefect(player, "One arm")
          || !Objects.equals(slot.getKey(), "left hand")) {
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
      if (itemActionTable.getChildren().contains(cancelButton, true)) {
        applyingItem = null;
        dismembering = false;

        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    holdButton = new ActionButton("H", "Hold");
    holdButton.setKeys(Input.Keys.H);
    holdButton.setAction(table, () -> {
      if (itemActionTable.getChildren().contains(holdButton, true)) {
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
      if (itemActionTable.getChildren().contains(wearButton, true)) {
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
      if (itemActionTable.getChildren().contains(throwButton, true)) {
        ComponentMappers.item.get(inventory.items.get(itemSelected)).throwing = true;

        WorldManager.inputHelpers.startTargeting(WorldManager.TargetState.THROW);

        main.setScreen(Main.playScreen);
      }
    });

    eatButton = new ActionButton("E", "Eat");
    eatButton.setKeys(Input.Keys.E);
    eatButton.setAction(table, () -> {
      if (itemActionTable.getChildren().contains(eatButton, true)) {
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
      if (itemActionTable.getChildren().contains(skinButton, true)) {
        Vector2 position = ComponentMappers.position.get(player).pos;
        int amount = MathUtils.random(1, 6);

        for (int i = 0; i < amount; i++) {
          Entity skin = WorldManager.entityFactory.createSkin(
              inventory.items.get(itemSelected), position
          );

          WorldManager.world.addEntity(skin);
          WorldManager.itemHelpers.addToInventory(player, skin, false);
        }

        WorldManager.itemHelpers.drop(player, inventory.items.get(itemSelected), true);
        WorldManager.log.add("inventory.skinned", amount);

        itemSelected = 0;

        updateInventoryGroup();
        updateItemDetailsGroup();
      }
    });

    dismemberButton = new ActionButton("M", "Dismember");
    dismemberButton.setKeys(Input.Keys.M);
    dismemberButton.setAction(table, () -> {
      if (itemActionTable.getChildren().contains(dismemberButton, true)) {
        dismembering = true;

        updateItemDetailsGroup();
      }
    });

    bandageButton = new ActionButton("B", "Bandage");
    bandageButton.setKeys(Input.Keys.B);
    bandageButton.setAction(table, () -> {
      if (itemActionTable.getChildren().contains(bandageButton, true)) {
        player.remove(BleedingComponent.class);
        WorldManager.itemHelpers.drop(player, inventory.items.get(itemSelected), true);
        WorldManager.log.add("inventory.bandaged");

        itemSelected = 0;

        updateAttributesGroup();
        updateInventoryGroup();
        updateItemDetailsGroup();
      }
    });

    craftButton = new ActionButton("F", "Craft");
    craftButton.setKeys(Input.Keys.F);
    craftButton.setAction(table, () -> main.setScreen(new CraftScreen(main)));

    applyButton = new ActionButton("A", "Apply");
    applyButton.setKeys(Input.Keys.A);
    applyButton.setAction(table, () -> {
      if (itemActionTable.getChildren().contains(applyButton, true)) {
        applyingItem = inventory.items.get(itemSelected);

        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    confirmApplyButton = new ActionButton("ENTER", "Apply to this");
    confirmApplyButton.setKeys(Input.Keys.ENTER);
    confirmApplyButton.setAction(table, () -> {
      if (itemActionTable.getChildren().contains(confirmApplyButton, true)) {
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
      if (itemActionTable.getChildren().contains(removeButton, true)) {
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
      if (itemActionTable.getChildren().contains(dropButton, true)) {
        WorldManager.itemHelpers.drop(player, inventory.items.get(itemSelected), false);

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
