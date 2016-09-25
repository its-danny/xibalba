package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.InventoryComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.defects.OneArmComponent;
import me.dannytatom.xibalba.components.items.ItemEffectsComponent;
import me.dannytatom.xibalba.components.traits.ScoutComponent;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Map;
import java.util.Objects;

public class CharacterScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private AttributesComponent attributes;
  private SkillsComponent skills;
  private InventoryComponent inventory;
  private EquipmentComponent equipment;
  private Table table;
  private VerticalGroup attributesGroup;
  private VerticalGroup skillsGroup;
  private VerticalGroup traitsGroup;
  private VerticalGroup inventoryGroup;
  private VerticalGroup itemDetailsGroup;
  private VerticalGroup equipmentGroup;
  private HorizontalGroup itemActionGroup;
  private ActionButton cancelButton;
  private ActionButton holdButton;
  private ActionButton wearButton;
  private ActionButton throwButton;
  private ActionButton eatButton;
  private ActionButton applyButton;
  private ActionButton confirmApplyButton;
  private ActionButton removeButton;
  private ActionButton dropButton;
  private Entity applyingItem = null;

  private Section sectionSelected = Section.INVENTORY;
  private int itemSelected = 0;

  /**
   * View and manage inventory.
   *
   * @param main Instance of the main class
   */
  public CharacterScreen(Main main) {
    this.main = main;

    stage = new Stage(new FitViewport(960, 540));

    attributes = ComponentMappers.attributes.get(WorldManager.player);
    skills = ComponentMappers.skills.get(WorldManager.player);
    inventory = ComponentMappers.inventory.get(WorldManager.player);
    equipment = ComponentMappers.equipment.get(WorldManager.player);

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
            itemSelected -= 1;

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
          if (itemSelected < inventoryGroup.getChildren().size - 3) {
            itemSelected += 1;

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
    int defense = WorldManager.combatHelpers.getArmorDefense(WorldManager.player);

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

    Entity primaryWeapon = WorldManager.itemHelpers.getRightHand(WorldManager.player);

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

    if (ComponentMappers.oneArm.has(WorldManager.player)) {
      traitsGroup.addActor(
          new Label("[RED]" + OneArmComponent.name + "\n[DARK_GRAY]" + WordUtils.wrap(OneArmComponent.description, 50), Main.skin)
      );
    }

    if (ComponentMappers.scout.has(WorldManager.player)) {
      traitsGroup.addActor(
          new Label("[GREEN]" + ScoutComponent.name + "\n[DARK_GRAY]" + WordUtils.wrap(ScoutComponent.description, 50), Main.skin)
      );
    }
  }

  private void updateInventoryGroup() {
    inventoryGroup.clear();

    inventoryGroup.addActor(new Label("Inventory", Main.skin));
    inventoryGroup.addActor(new Label("", Main.skin));

    for (int i = 0; i < inventory.items.size(); i++) {
      Entity item = inventory.items.get(i);

      inventoryGroup.addActor(new Label(createInventoryItemText(i, item), Main.skin));
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

      if (WorldManager.itemHelpers.isIdentified(WorldManager.player, item)) {
        String description = "[LIGHT_GRAY]" + details.description;
        itemDetailsGroup.addActor(new Label(WordUtils.wrap(description, 50), Main.skin));

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

      boolean itemIsEquipped = WorldManager.itemHelpers.isEquipped(WorldManager.player, item);

      // Where the item is equipped if it is equipped

      if (itemIsEquipped) {
        statsGroup.addActor(
            new Label(
                "[YELLOW]* [LIGHT_GRAY]Using in "
                    + WorldManager.itemHelpers.getLocation(WorldManager.player, item),
                Main.skin
            )
        );

        statsGroup.addActor(new Label("", Main.skin));
      }

      // Item stats

      if (WorldManager.itemHelpers.isIdentified(WorldManager.player, item)) {
        if (details.attributes != null) {
          // Defense

          if (details.attributes.get("defense") != null) {
            String string = "[LIGHT_GRAY]DEF " + "[GREEN]" + details.attributes.get("defense");

            if (!itemIsEquipped) {
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

            if (!itemIsEquipped) {
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

            if (!itemIsEquipped) {
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

            if (!itemIsEquipped) {
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

        ItemEffectsComponent selectedItemEffects = ComponentMappers.itemEffects.get(item);

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

      if (ComponentMappers.oneArm.has(WorldManager.player) && details.twoHanded) {
        restrictionsGroup.addActor(new Label("[RED]You can't hold this due to too few arms.", Main.skin));
      }

      if (ComponentMappers.oneArm.has(WorldManager.player) && Objects.equals(details.location, "left hand")) {
        restrictionsGroup.addActor(new Label("[RED]You don't have a left hand to hold this in.", Main.skin));
      }

      if (restrictionsGroup.getChildren().size > 0) {
        restrictionsGroup.addActor(new Label("", Main.skin));
      }

      // Actions

      if (applyingItem == null) {
        if (details.actions.contains("hold", false) && !itemIsEquipped) {
          if (!ComponentMappers.oneArm.has(WorldManager.player) || !details.twoHanded) {
            itemActionGroup.addActor(holdButton);
          }
        }

        if (details.actions.contains("wear", false) && !itemIsEquipped) {
          if (!ComponentMappers.oneArm.has(WorldManager.player) || !Objects.equals(details.location, "left hand")) {
            itemActionGroup.addActor(wearButton);
          }
        }

        if (details.actions.contains("throw", false)) {
          itemActionGroup.addActor(throwButton);
        }

        if (details.actions.contains("consume", false) && !itemIsEquipped) {
          itemActionGroup.addActor(eatButton);
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

      if (!ComponentMappers.oneArm.has(WorldManager.player) || !Objects.equals(slot.getKey(), "left hand")) {
        equipmentGroup.addActor(
            new Label(createEquipmentSlotText(index, key, item), Main.skin)
        );
      }

      index++;
    }
  }

  private String createInventoryItemText(int index, Entity item) {
    String name = WorldManager.itemHelpers.getName(WorldManager.player, item);

    if (WorldManager.itemHelpers.isEquipped(WorldManager.player, item)) {
      name += " [YELLOW]*";
    }

    if (sectionSelected == Section.INVENTORY && index == itemSelected) {
      return "[DARK_GRAY]> [WHITE]" + name;
    } else {
      return "[LIGHT_GRAY]" + name;
    }
  }

  private String createEquipmentSlotText(int index, String slot, Entity item) {
    String itemName = item == null
        ? " [DARK_GRAY]Nothing"
        : " [WHITE]" + WorldManager.itemHelpers.getName(WorldManager.player, item);

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
        WorldManager.itemHelpers.hold(WorldManager.player, inventory.items.get(itemSelected));

        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    wearButton = new ActionButton("W", "Wear");
    wearButton.setKeys(Input.Keys.W);
    wearButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(wearButton, true)) {
        WorldManager.itemHelpers.wear(WorldManager.player, inventory.items.get(itemSelected));

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
        PlayerComponent player = ComponentMappers.player.get(WorldManager.player);

        player.target = null;
        player.path = null;

        WorldManager.state = WorldManager.State.TARGETING;

        main.setScreen(Main.playScreen);
      }
    });

    eatButton = new ActionButton("E", "Eat");
    eatButton.setKeys(Input.Keys.E);
    eatButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(eatButton, true)) {
        WorldManager.itemHelpers.eat(WorldManager.player, inventory.items.get(itemSelected));

        itemSelected = 0;

        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
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
            WorldManager.player, applyingItem, inventory.items.get(itemSelected)
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
        WorldManager.itemHelpers.remove(WorldManager.player, inventory.items.get(itemSelected));

        updateInventoryGroup();
        updateItemDetailsGroup();
        updateEquipmentGroup();
      }
    });

    dropButton = new ActionButton("D", "Drop");
    dropButton.setKeys(Input.Keys.D);
    dropButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(dropButton, true)) {
        WorldManager.itemHelpers.drop(WorldManager.player, inventory.items.get(itemSelected));

        itemSelected = 0;

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
