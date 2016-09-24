package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.PlayerComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.items.ItemEffectsComponent;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.world.WorldManager;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Map;

public class CharacterScreen implements Screen {
  private final Main main;

  private final Stage stage;

  private final Table table;
  private final VerticalGroup attributesGroup;
  private final VerticalGroup skillsGroup;
  private final VerticalGroup inventoryGroup;
  private final Table itemDetails;
  private final VerticalGroup equipmentGroup;
  private final AttributesComponent attributes;
  private final SkillsComponent skills;
  private final EquipmentComponent equipment;
  private final ArrayList<Entity> inventoryItems;
  private final HorizontalGroup itemActionGroup;
  private ArrayList<Label> inventoryItemLabels;
  private ActionButton cancelButton;
  private ActionButton holdButton;
  private ActionButton wearButton;
  private ActionButton throwButton;
  private ActionButton eatButton;
  private ActionButton applyButton;
  private ActionButton confirmApplyButton;
  private ActionButton removeButton;
  private ActionButton dropButton;
  private int itemSelected = 0;
  private int itemHovered = 0;
  private Entity applying = null;

  /**
   * View and manage inventory.
   *
   * @param main Instance of the main class
   */
  public CharacterScreen(Main main) {
    this.main = main;

    attributes = ComponentMappers.attributes.get(WorldManager.player);
    skills = ComponentMappers.skills.get(WorldManager.player);
    equipment = ComponentMappers.equipment.get(WorldManager.player);
    inventoryItems = ComponentMappers.inventory.get(WorldManager.player).items;

    stage = new Stage(new FitViewport(960, 540));

    table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    HorizontalGroup titleGroup = new HorizontalGroup().align(Align.center | Align.left);
    titleGroup.space(10);

    ActionButton backButton = new ActionButton("ESC", null);
    backButton.setKeys(Input.Keys.ESCAPE);
    backButton.setAction(table, () -> main.setScreen(Main.playScreen));
    titleGroup.addActor(backButton);

    Label title = new Label(attributes.name + " Character Sheet", Main.skin);
    titleGroup.addActor(title);

    Table titleTable = new Table();
    titleTable.add(titleGroup).pad(10).width(Gdx.graphics.getWidth() - 20).top().left();

    attributesGroup = new VerticalGroup().align(Align.top | Align.left);
    skillsGroup = new VerticalGroup().align(Align.top | Align.left);

    Table characterTable = new Table();
    characterTable.add(attributesGroup)
        .pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    characterTable.add(skillsGroup)
        .pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    characterTable.add(new VerticalGroup())
        .pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();

    inventoryGroup = new VerticalGroup().align(Align.top | Align.left);
    itemDetails = new Table().align(Align.top | Align.left);
    itemActionGroup = new HorizontalGroup().space(5).align(Align.top | Align.left);
    equipmentGroup = new VerticalGroup().align(Align.top | Align.left);

    Table inventoryTable = new Table();
    inventoryTable.add(inventoryGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    inventoryTable.add(itemDetails).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    inventoryTable.add(equipmentGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();

    table.add(titleTable);
    table.row();
    table.add(characterTable);
    table.row();
    table.add(inventoryTable);

    setupInventoryLabels();
    setupActionButtons();

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

    renderAttributes();
    renderSkills();
    renderInventory();
    renderItemDetails();
    renderEquipment();

    if (Gdx.input.isKeyJustPressed(Input.Keys.J) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      if (itemSelected < inventoryItems.size() - 1) {
        itemSelected += 1;
        itemHovered = itemSelected;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.K) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
      if (itemSelected > 0) {
        itemSelected -= 1;
        itemHovered = itemSelected;
      }
    }

    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  private void setupInventoryLabels() {
    inventoryItemLabels = new ArrayList<>();

    for (int i = 0; i < inventoryItems.size(); i++) {
      int index = i;
      Entity item = inventoryItems.get(i);
      Label label = new Label(
          WorldManager.itemHelpers.getName(WorldManager.player, item), Main.skin
      );
      label.addListener(new ClickListener() {
        @Override
        public void enter(InputEvent event, float positionX, float positionY,
                          int pointer, Actor fromActor) {
          if (itemHovered != index) {
            itemHovered = index;
          }
        }

        @Override
        public void exit(InputEvent event, float positionX, float positionY,
                         int pointer, Actor toActor) {
          if (itemHovered != itemSelected) {
            itemHovered = itemSelected;
          }
        }

        @Override
        public void clicked(InputEvent event, float positionX, float positionY) {
          super.clicked(event, positionX, positionY);

          if (itemSelected != index) {
            itemSelected = index;
          }
        }
      });

      inventoryItemLabels.add(label);
    }
  }

  private void setupActionButtons() {
    cancelButton = new ActionButton("Q", "Cancel");
    cancelButton.setKeys(Input.Keys.Q);
    cancelButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(cancelButton, true)) {
        handleCancel();
      }
    });

    holdButton = new ActionButton("H", "Hold");
    holdButton.setKeys(Input.Keys.H);
    holdButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(holdButton, true)) {
        handleHold();
      }
    });

    wearButton = new ActionButton("W", "Wear");
    wearButton.setKeys(Input.Keys.W);
    wearButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(wearButton, true)) {
        handleWear();
      }
    });

    throwButton = new ActionButton("T", "Throw");
    throwButton.setKeys(Input.Keys.T);
    throwButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(throwButton, true)) {
        handleThrow();
      }
    });

    eatButton = new ActionButton("E", "Eat");
    eatButton.setKeys(Input.Keys.E);
    eatButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(eatButton, true)) {
        handleEat();
      }
    });

    applyButton = new ActionButton("A", "Apply");
    applyButton.setKeys(Input.Keys.A);
    applyButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(applyButton, true)) {
        handleApply();
      }
    });

    confirmApplyButton = new ActionButton("ENTER", "Apply to this");
    confirmApplyButton.setKeys(Input.Keys.ENTER);
    confirmApplyButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(confirmApplyButton, true)) {
        handleConfirmApply();
      }
    });

    removeButton = new ActionButton("R", "Remove");
    removeButton.setKeys(Input.Keys.R);
    removeButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(removeButton, true)) {
        handleRemove();
      }
    });

    dropButton = new ActionButton("D", "Drop");
    dropButton.setKeys(Input.Keys.D);
    dropButton.setAction(table, () -> {
      if (itemActionGroup.getChildren().contains(dropButton, true)) {
        handleDrop();
      }
    });
  }

  private void renderAttributes() {
    attributesGroup.clear();

    attributesGroup.addActor(new Label("[DARK_GRAY]-[] Attributes", Main.skin));
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
            "[LIGHT_GRAY]HP[] " + healthColor + attributes.health
                + "[LIGHT_GRAY]/" + attributes.maxHealth, Main.skin
        )
    );

    // Toughness & strength

    int toughness = attributes.toughness;
    int strength = attributes.strength;

    attributesGroup.addActor(
        new Label(
            "[LIGHT_GRAY]TUF " + "[YELLOW]" + toughness + "[DARK_GRAY]d "
                + "[LIGHT_GRAY]STR " + "[CYAN]" + strength + "[DARK_GRAY]d",
            Main.skin
        )
    );

    // Defense & damage

    int defense = WorldManager.combatHelpers.getArmorDefense(WorldManager.player);
    int damage = 0;

    Entity primaryWeapon = WorldManager.itemHelpers.getRightHand(WorldManager.player);

    if (primaryWeapon != null) {
      damage = ComponentMappers.item.get(primaryWeapon).attributes.get("hitDamage");
    }

    attributesGroup.addActor(
        new Label(
            "[LIGHT_GRAY]DEF " + "[YELLOW]" + toughness + "[DARK_GRAY]d "
                + (defense > 0 ? "[LIGHT_GRAY]+ " + "[GREEN]" + defense + "[DARK_GRAY] " : "")
                + "[LIGHT_GRAY]DMG " + "[CYAN]" + strength + "[DARK_GRAY]d "
                + (damage > 0 ? "[LIGHT_GRAY]+ " + "[RED]" + damage + "[DARK_GRAY]d" : ""),
            Main.skin
        )
    );
  }

  private void renderSkills() {
    skillsGroup.clear();

    skillsGroup.addActor(new Label("[DARK_GRAY]-[] Skills", Main.skin));
    skillsGroup.addActor(new Label("", Main.skin));

    if (skills.levels.get("unarmed") > 0) {
      skillsGroup.addActor(makeSkillLine("Unarmed", skills.levels.get("unarmed")));
    }

    if (skills.levels.get("throwing") > 0) {
      skillsGroup.addActor(makeSkillLine("Throwing", skills.levels.get("throwing")));
    }

    if (skills.levels.get("slashing") > 0) {
      skillsGroup.addActor(makeSkillLine("Slashing", skills.levels.get("slashing")));
    }

    if (skills.levels.get("piercing") > 0) {
      skillsGroup.addActor(makeSkillLine("Piercing", skills.levels.get("piercing")));
    }

    if (skills.levels.get("bashing") > 0) {
      skillsGroup.addActor(makeSkillLine("Bashing", skills.levels.get("bashing")));
    }

    if (skills.levels.get("archery") > 0) {
      skillsGroup.addActor(makeSkillLine("Archery", skills.levels.get("archery")));
    }
  }

  private void renderInventory() {
    inventoryGroup.clear();

    inventoryGroup.addActor(new Label("[DARK_GRAY]-[] Inventory", Main.skin));
    inventoryGroup.addActor(new Label("", Main.skin));

    for (int i = 0; i < inventoryItems.size(); i++) {
      Entity item = inventoryItems.get(i);

      String name;

      if (i == itemSelected) {
        name = "[DARK_GRAY]> [WHITE]"
            + WorldManager.itemHelpers.getName(WorldManager.player, item);
      } else if (i == itemHovered) {
        name = "[LIGHT_GRAY]" + WorldManager.itemHelpers.getName(WorldManager.player, item);
      } else {
        name = "[DARK_GRAY]" + WorldManager.itemHelpers.getName(WorldManager.player, item);
      }

      if (WorldManager.itemHelpers.isEquipped(WorldManager.player, item)) {
        name += " [YELLOW]*";
      }

      Label label = inventoryItemLabels.get(i);
      label.setText(name);

      inventoryGroup.addActor(label);
    }
  }

  private void renderItemDetails() {
    itemDetails.clear();
    itemActionGroup.clear();

    if (!inventoryItems.isEmpty()) {
      VerticalGroup statsGroup = new VerticalGroup().align(Align.top | Align.left);

      itemDetails.add(statsGroup).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
      itemDetails.row();
      itemDetails.add(itemActionGroup).top().left();

      Entity selectedItem = inventoryItems.get(itemSelected);
      ItemComponent selectedItemDetails = ComponentMappers.item.get(selectedItem);

      // Item name and it's location name

      statsGroup.addActor(
          new Label(
              "[DARK_GRAY]-[] "
                  + WorldManager.itemHelpers.getName(WorldManager.player, selectedItem)
                  + " [DARK_GRAY](" + selectedItemDetails.type + ")",
              Main.skin
          )
      );

      statsGroup.addActor(new Label("", Main.skin));

      // Description

      if (WorldManager.itemHelpers.isIdentified(WorldManager.player, selectedItem)) {
        String description = "[LIGHT_GRAY]" + selectedItemDetails.description;
        description = WordUtils.wrap(description, 50);
        Label descriptionLabel = new Label(description, Main.skin);
        statsGroup.addActor(descriptionLabel);

        if (selectedItemDetails.twoHanded) {
          statsGroup.addActor(new Label("", Main.skin));
          statsGroup.addActor(new Label("[LIGHT_GRAY]Two handed", Main.skin));
        }

        statsGroup.addActor(new Label("", Main.skin));
      }

      // Where the item is equipped if it is equipped

      if (WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
        statsGroup.addActor(
            new Label(
                "[YELLOW]* [LIGHT_GRAY]Using in "
                    + WorldManager.itemHelpers.getLocation(WorldManager.player, selectedItem),
                Main.skin
            )
        );

        statsGroup.addActor(new Label("", Main.skin));
      }

      // Item stats

      if (WorldManager.itemHelpers.isIdentified(WorldManager.player, selectedItem)) {
        if (selectedItemDetails.attributes != null) {
          if (selectedItemDetails.attributes.get("defense") != null) {
            String string = "[LIGHT_GRAY]DEF " + "[GREEN]"
                + selectedItemDetails.attributes.get("defense");

            if (!WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
              Entity itemInSlot = equipment.slots.get(selectedItemDetails.location);

              if (itemInSlot != null) {
                ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

                if (itemInSlotDetails.attributes.get("defense") != null) {
                  string += "[DARK_GRAY] -> [GREEN]" + itemInSlotDetails.attributes.get("defense");
                }
              }
            }

            statsGroup.addActor(new Label(string, Main.skin));
          }

          if (selectedItemDetails.attributes.get("hitDamage") != null) {
            String string = "[LIGHT_GRAY]HIT DMG " + "[RED]"
                + selectedItemDetails.attributes.get("hitDamage") + "[DARK_GRAY]d";

            if (!WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
              Entity itemInSlot = equipment.slots.get(selectedItemDetails.location);

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

          if (selectedItemDetails.attributes.get("throwDamage") != null) {
            String string = "[LIGHT_GRAY]THR DMG " + "[RED]"
                + selectedItemDetails.attributes.get("throwDamage") + "[DARK_GRAY]d";

            if (!WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
              Entity itemInSlot = equipment.slots.get(selectedItemDetails.location);

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
        }

        ItemEffectsComponent selectedItemEffects = ComponentMappers.itemEffects.get(selectedItem);

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

            String string = "[DARK_GRAY]" + prettyEvent
                + " [LIGHT_GRAY]" + prettyEffect + " [CYAN]" + split[1];

            statsGroup.addActor(new Label(string, Main.skin));
          }
        }

        statsGroup.addActor(new Label("", Main.skin));
      }

      // Item actions

      if (applying == null) {
        if (selectedItemDetails.actions.contains("hold", false)
            && !WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
          itemActionGroup.addActor(holdButton);
        }

        if (selectedItemDetails.actions.contains("wear", false)
            && !WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
          itemActionGroup.addActor(wearButton);
        }

        if (selectedItemDetails.actions.contains("throw", false)) {
          itemActionGroup.addActor(throwButton);
        }

        if (selectedItemDetails.actions.contains("consume", false)
            && !WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
          itemActionGroup.addActor(eatButton);
        }

        if (selectedItemDetails.actions.contains("apply", false)) {
          itemActionGroup.addActor(applyButton);
        }

        if (WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
          itemActionGroup.addActor(removeButton);
        }

        if (!WorldManager.itemHelpers.isEquipped(WorldManager.player, selectedItem)) {
          itemActionGroup.addActor(dropButton);
        }
      } else {
        itemActionGroup.addActor(cancelButton);

        if (applying != selectedItem && selectedItemDetails.actions.contains("applyTo", false)) {
          itemActionGroup.addActor(confirmApplyButton);
        }
      }
    }
  }

  private void renderEquipment() {
    equipmentGroup.clear();

    equipmentGroup.addActor(new Label("[DARK_GRAY]-[] Equipment", Main.skin));
    equipmentGroup.addActor(new Label("", Main.skin));

    for (java.util.Map.Entry<String, Entity> slot : equipment.slots.entrySet()) {
      String key = WordUtils.capitalize(slot.getKey());
      Entity item = slot.getValue();

      if (item == null) {
        equipmentGroup.addActor(
            new Label("[LIGHT_GRAY]" + key + ": [DARK_GRAY]Nothing", Main.skin)
        );
      } else {
        String slotName = "[LIGHT_GRAY]" + key + ":[] ";
        String itemName = WorldManager.itemHelpers.getName(WorldManager.player, item);

        equipmentGroup.addActor(new Label(slotName + itemName, Main.skin));
      }
    }
  }

  private Label makeSkillLine(String skill, int level) {
    String str = "[DARK_GRAY][[";

    switch (level) {
      case 0:
        str += "[LIGHT_GRAY]xxxxx";
        break;
      case 4:
        str += "[WHITE]x[DARK_GRAY]xxxx";
        break;
      case 6:
        str += "[WHITE]xx[DARK_GRAY]xxx";
        break;
      case 8:
        str += "[WHITE]xxx[DARK_GRAY]xx";
        break;
      case 10:
        str += "[WHITE]xxxx[DARK_GRAY]x";
        break;
      case 12:
        str += "[WHITE]xxxxx";
        break;
      default:
    }

    str += "[DARK_GRAY]]";

    return new Label(
        str + " [LIGHT_GRAY]" + skill + " [DARK_GRAY]([YELLOW]" + level + "[DARK_GRAY]d)", Main.skin
    );
  }

  private void handleCancel() {
    if (applying != null) {
      applying = null;
    }
  }

  private void handleHold() {
    WorldManager.itemHelpers.hold(WorldManager.player, inventoryItems.get(itemSelected));
  }

  private void handleWear() {
    WorldManager.itemHelpers.wear(WorldManager.player, inventoryItems.get(itemSelected));
  }

  private void handleThrow() {
    ComponentMappers.item.get(inventoryItems.get(itemSelected)).throwing = true;

    PlayerComponent playerDetails = ComponentMappers.player.get(WorldManager.player);

    playerDetails.target = null;
    playerDetails.path = null;

    WorldManager.state = WorldManager.State.TARGETING;
    main.setScreen(Main.playScreen);
  }

  private void handleEat() {
    WorldManager.itemHelpers.eat(WorldManager.player, inventoryItems.get(itemSelected));

    itemSelected = 0;
  }

  private void handleApply() {
    applying = inventoryItems.get(itemSelected);
  }

  private void handleConfirmApply() {
    WorldManager.itemHelpers.apply(
        WorldManager.player, applying, inventoryItems.get(itemSelected)
    );

    itemSelected = 0;
    applying = null;
  }

  private void handleRemove() {
    WorldManager.itemHelpers.remove(WorldManager.player, inventoryItems.get(itemSelected));
  }

  private void handleDrop() {
    WorldManager.itemHelpers.drop(WorldManager.player, inventoryItems.get(itemSelected));

    itemSelected = 0;
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
}
