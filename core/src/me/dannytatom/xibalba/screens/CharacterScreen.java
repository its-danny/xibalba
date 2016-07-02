package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class CharacterScreen implements Screen {
  private final Main main;

  private final Stage stage;

  private final FPSLogger fps;
  private final VerticalGroup statsGroup;
  private final VerticalGroup skillsGroup;
  private final VerticalGroup inventoryGroup;
  private final Table itemDetails;
  private final VerticalGroup equipmentGroup;
  private ArrayList<Label> inventoryItemLabels;
  private TextButton holdButton;
  private TextButton wearButton;
  private TextButton throwButton;
  private TextButton removeButton;
  private TextButton dropButton;
  private ArrayList<Entity> items;
  private HashMap<String, Entity> slots;
  private int selected = 0;
  private int hovered = 0;

  /**
   * View and manage inventory.
   *
   * @param main Instance of the main class
   */
  public CharacterScreen(Main main) {
    this.main = main;

    fps = new FPSLogger();
    items = ComponentMappers.inventory.get(main.player).items;
    slots = ComponentMappers.equipment.get(main.player).slots;

    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    statsGroup = new VerticalGroup().align(Align.top | Align.left);
    skillsGroup = new VerticalGroup().align(Align.top | Align.left);
    inventoryGroup = new VerticalGroup().align(Align.top | Align.left);
    itemDetails = new Table().align(Align.top | Align.left);
    equipmentGroup = new VerticalGroup().align(Align.top | Align.left);

    Table topTable = new Table();
    topTable.add(statsGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    topTable.add(skillsGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    topTable.add(new VerticalGroup()).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();

    Table bottomTable = new Table();
    bottomTable.add(inventoryGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    bottomTable.add(itemDetails).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
    bottomTable.add(equipmentGroup).pad(10).width(Gdx.graphics.getWidth() / 3 - 20).top().left();

    table.add(topTable);
    table.row();
    table.add(bottomTable);

    setupInventoryLabels();
    setupActionButtons();

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(
        Colors.get("CAVE_BACKGROUND").r,
        Colors.get("CAVE_BACKGROUND").g,
        Colors.get("CAVE_BACKGROUND").b,
        Colors.get("CAVE_BACKGROUND").a
    );

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    fps.log();

    renderStats();
    renderSkills();
    renderInventory();
    renderItemDetails();
    renderEquipment();

    if (Gdx.input.isKeyJustPressed(Input.Keys.J) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      if (selected < items.size() - 1) {
        selected += 1;
        hovered = selected;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.K) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
      if (selected > 0) {
        selected -= 1;
        hovered = selected;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      main.setScreen(main.playScreen);
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

    for (int i = 0; i < items.size(); i++) {
      Entity entity = items.get(i);
      ItemComponent item = ComponentMappers.item.get(entity);
      Label label = new Label(item.name, main.skin);
      int index = i;
      label.addListener(new ClickListener() {
        @Override
        public void enter(InputEvent event, float positionX, float positionY,
                          int pointer, Actor fromActor) {
          if (hovered != index) {
            hovered = index;
          }
        }

        @Override
        public void exit(InputEvent event, float positionX, float positionY,
                         int pointer, Actor toActor) {
          if (hovered != selected) {
            hovered = selected;
          }
        }

        @Override
        public void clicked(InputEvent event, float positionX, float positionY) {
          super.clicked(event, positionX, positionY);

          if (selected != index) {
            selected = index;
          }
        }
      });

      inventoryItemLabels.add(label);
    }
  }

  private void setupActionButtons() {
    holdButton = new TextButton(
        "[DARK_GRAY][ [CYAN]H[DARK_GRAY] ][WHITE] Hold", main.skin
    );
    holdButton.pad(5);
    holdButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        holdButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        holdButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        handleHold();
      }
    });

    wearButton = new TextButton(
        "[DARK_GRAY][ [CYAN]W[DARK_GRAY] ][WHITE] Wear", main.skin
    );
    wearButton.pad(5);
    wearButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        wearButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        wearButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        handleWear();
      }
    });

    throwButton = new TextButton(
        "[DARK_GRAY][ [CYAN]T[DARK_GRAY] ][WHITE] Throw", main.skin
    );
    throwButton.pad(5);
    throwButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        throwButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        throwButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        handleThrow();
      }
    });

    removeButton = new TextButton(
        "[DARK_GRAY][ [CYAN]R[DARK_GRAY] ][WHITE] Remove", main.skin
    );
    removeButton.pad(5);
    removeButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        removeButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        removeButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        handleRemove();
      }
    });

    dropButton = new TextButton(
        "[DARK_GRAY][ [CYAN]D[DARK_GRAY] ][WHITE] Drop", main.skin
    );
    dropButton.pad(5);
    dropButton.addListener(new ClickListener() {
      @Override
      public void enter(InputEvent event, float positionX, float positionY,
                        int pointer, Actor fromActor) {
        dropButton.setColor(1, 1, 1, 0.5f);
      }

      @Override
      public void exit(InputEvent event, float positionX, float positionY,
                       int pointer, Actor toActor) {
        dropButton.setColor(1, 1, 1, 1);
      }

      @Override
      public void clicked(InputEvent event, float positionX, float positionY) {
        super.clicked(event, positionX, positionY);
        handleDrop();
      }
    });
  }

  private void renderStats() {
    statsGroup.clear();

    AttributesComponent attributes = ComponentMappers.attributes.get(main.player);

    statsGroup.addActor(new Label("[DARK_GRAY]-[] " + attributes.name, main.skin));
    statsGroup.addActor(new Label("", main.skin));

    // Health

    String healthColor;

    if (attributes.health / attributes.maxHealth <= 0.5f) {
      healthColor = "[RED]";
    } else {
      healthColor = "[WHITE]";
    }

    statsGroup.addActor(
        new Label(
            "[LIGHT_GRAY]HP[] " + healthColor + attributes.health
                + "[LIGHT_GRAY]/" + attributes.maxHealth, main.skin
        )
    );

    // Toughness & strength

    int toughness = attributes.toughness;
    int strength = attributes.strength;

    statsGroup.addActor(
        new Label(
            "[LIGHT_GRAY]TUF " + "[YELLOW]" + toughness + "[DARK_GRAY]d "
                + "[LIGHT_GRAY]STR " + "[CYAN]" + strength + "[DARK_GRAY]d",
            main.skin
        )
    );

    // Defense & damage

    int defense = main.combatHelpers.getArmorDefense(main.player);
    int damage = 0;

    Entity primaryWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);

    if (primaryWeapon != null) {
      damage = ComponentMappers.item.get(primaryWeapon).attributes.get("hitDamage");
    }

    statsGroup.addActor(
        new Label(
            "[LIGHT_GRAY]DEF " + "[YELLOW]" + toughness + "[DARK_GRAY]d "
                + (defense > 0 ? "[LIGHT_GRAY]+ " + "[GREEN]" + defense + "[DARK_GRAY] " : "")
                + "[LIGHT_GRAY]DMG " + "[CYAN]" + strength + "[DARK_GRAY]d "
                + (damage > 0 ? "[LIGHT_GRAY]+ " + "[RED]" + damage + "[DARK_GRAY]d" : ""),
            main.skin
        )
    );
  }

  private void renderSkills() {
    skillsGroup.clear();

    skillsGroup.addActor(new Label("[DARK_GRAY]-[] Skills", main.skin));
    skillsGroup.addActor(new Label("", main.skin));

    SkillsComponent skills = ComponentMappers.skills.get(main.player);

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

    inventoryGroup.addActor(new Label("[DARK_GRAY]-[] Inventory", main.skin));
    inventoryGroup.addActor(new Label("", main.skin));

    for (int i = 0; i < items.size(); i++) {
      Entity entity = items.get(i);
      ItemComponent item = ComponentMappers.item.get(entity);

      String name;

      if (i == selected) {
        name = "[DARK_GRAY]> [WHITE]" + item.name;
      } else if (i == hovered) {
        name = "[LIGHT_GRAY]" + item.name;
      } else {
        name = "[DARK_GRAY]" + item.name;
      }

      if (main.equipmentHelpers.isEquipped(main.player, entity)) {
        name += " [YELLOW]*";
      }

      Label label = inventoryItemLabels.get(i);
      label.setText(name);

      inventoryGroup.addActor(label);
    }
  }

  private void renderItemDetails() {
    itemDetails.clear();

    if (!items.isEmpty()) {
      VerticalGroup statsGroup = new VerticalGroup().align(Align.top | Align.left);
      HorizontalGroup actionsGroup = new HorizontalGroup().space(5).align(Align.top | Align.left);
      itemDetails.add(statsGroup).width(Gdx.graphics.getWidth() / 3 - 20).top().left();
      itemDetails.row();
      itemDetails.add(actionsGroup).top().left();

      Entity selectedItem = items.get(selected);
      ItemComponent selectedItemDetails = ComponentMappers.item.get(selectedItem);

      statsGroup.addActor(
          new Label(
              "[DARK_GRAY]-[] " + selectedItemDetails.name
                  + " [DARK_GRAY](" + selectedItemDetails.type + ")",
              main.skin
          )
      );

      String description = "[LIGHT_GRAY]" + selectedItemDetails.description;
      description = WordUtils.wrap(description, 50);
      Label descriptionLabel = new Label(description, main.skin);
      statsGroup.addActor(descriptionLabel);

      statsGroup.addActor(new Label("", main.skin));

      if (main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
        statsGroup.addActor(
            new Label(
                "[YELLOW]* [LIGHT_GRAY]Using in "
                    + main.equipmentHelpers.getLocation(main.player, selectedItem),
                main.skin
            )
        );

        statsGroup.addActor(new Label("", main.skin));
      }

      EquipmentComponent equipment = ComponentMappers.equipment.get(main.player);

      if (selectedItemDetails.attributes.get("defense") != null) {
        String string = "[LIGHT_GRAY]DEF " + "[GREEN]"
            + selectedItemDetails.attributes.get("defense");

        if (!main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
          Entity itemInSlot = equipment.slots.get(selectedItemDetails.location);

          if (itemInSlot != null) {
            ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

            if (itemInSlotDetails.attributes.get("defense") != null) {
              string += "[DARK_GRAY] -> [GREEN]" + itemInSlotDetails.attributes.get("defense");
            }
          }
        }

        statsGroup.addActor(new Label(string, main.skin));
      }

      if (selectedItemDetails.attributes.get("hitDamage") != null) {
        String string = "[LIGHT_GRAY]HIT DMG " + "[RED]"
            + selectedItemDetails.attributes.get("hitDamage") + "[DARK_GRAY]d";

        if (!main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
          Entity itemInSlot = equipment.slots.get(selectedItemDetails.location);

          if (itemInSlot != null) {
            ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

            if (itemInSlotDetails.attributes.get("hitDamage") != null) {
              string += "[DARK_GRAY] -> [RED]"
                  + itemInSlotDetails.attributes.get("hitDamage") + "[DARK_GRAY]d";
            }
          }
        }

        statsGroup.addActor(new Label(string, main.skin));
      }

      if (selectedItemDetails.attributes.get("throwDamage") != null) {
        String string = "[LIGHT_GRAY]THR DMG " + "[RED]"
            + selectedItemDetails.attributes.get("throwDamage") + "[DARK_GRAY]d";

        if (!main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
          Entity itemInSlot = equipment.slots.get(selectedItemDetails.location);

          if (itemInSlot != null) {
            ItemComponent itemInSlotDetails = ComponentMappers.item.get(itemInSlot);

            if (itemInSlotDetails.attributes.get("throwDamage") != null) {
              string += "[DARK_GRAY] -> [RED]"
                  + itemInSlotDetails.attributes.get("throwDamage") + "[DARK_GRAY]d";
            }
          }
        }

        statsGroup.addActor(new Label(string, main.skin));
      }

      statsGroup.addActor(new Label("", main.skin));

      if (selectedItemDetails.actions.get("canHold")
          && !main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
        actionsGroup.addActor(holdButton);

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
          handleHold();
        }
      }

      if (selectedItemDetails.actions.get("canWear")
          && !main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
        actionsGroup.addActor(wearButton);

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
          handleWear();
        }
      }

      if (selectedItemDetails.actions.get("canThrow")
          && !main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
        actionsGroup.addActor(throwButton);

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
          handleThrow();
        }
      }

      if (main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
        actionsGroup.addActor(removeButton);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
          handleRemove();
        }
      }

      if (!main.equipmentHelpers.isEquipped(main.player, selectedItem)) {
        actionsGroup.addActor(dropButton);

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
          handleDrop();
        }
      }
    }
  }

  private void renderEquipment() {
    equipmentGroup.clear();

    equipmentGroup.addActor(new Label("[DARK_GRAY]-[] Equipment", main.skin));
    equipmentGroup.addActor(new Label("", main.skin));

    for (java.util.Map.Entry<String, Entity> slot : slots.entrySet()) {
      String key = WordUtils.capitalize(slot.getKey());
      Entity item = slot.getValue();

      if (item == null) {
        equipmentGroup.addActor(
            new Label("[LIGHT_GRAY]" + key + ": [DARK_GRAY]Nothing", main.skin)
        );
      } else {
        ItemComponent itemDetails = ComponentMappers.item.get(item);

        String slotName = "[LIGHT_GRAY]" + key + ":[] ";
        String itemName = itemDetails.name;

        equipmentGroup.addActor(new Label(slotName + itemName, main.skin));
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
        str + " [LIGHT_GRAY]" + skill + " [DARK_GRAY]([YELLOW]" + level + "[DARK_GRAY]d)", main.skin
    );
  }

  private void handleHold() {
    main.equipmentHelpers.holdItem(main.player, items.get(selected));
  }

  private void handleWear() {
    main.equipmentHelpers.wearItem(main.player, items.get(selected));
  }

  private void handleThrow() {
    ComponentMappers.item.get(items.get(selected)).throwing = true;

    main.state = Main.State.TARGETING;
    main.setScreen(main.playScreen);
  }

  private void handleRemove() {
    main.equipmentHelpers.removeItem(main.player, items.get(selected));
  }

  private void handleDrop() {
    main.inventoryHelpers.dropItem(main.player, items.get(selected));
    selected = 0;
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
