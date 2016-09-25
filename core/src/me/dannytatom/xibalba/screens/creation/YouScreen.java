package me.dannytatom.xibalba.screens.creation;

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
import me.dannytatom.xibalba.components.defects.OneArmComponent;
import me.dannytatom.xibalba.components.traits.ScoutComponent;
import me.dannytatom.xibalba.screens.MainMenuScreen;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.PlayerSetup;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Map;

public class YouScreen implements Screen {
  private final Stage stage;
  private final PlayerSetup playerSetup;
  private int attributePoints;
  private int skillPoints;
  private int traitPoints;
  private VerticalGroup attributesGroup;
  private VerticalGroup skillsGroup;
  private VerticalGroup defectsGroup;
  private VerticalGroup traitsGroup;

  private Section sectionSelected = Section.ATTRIBUTES;
  private int itemSelected = 0;

  public YouScreen(Main main) {
    attributePoints = 5;
    skillPoints = 10;
    traitPoints = 0;
    stage = new Stage(new FitViewport(960, 540));
    playerSetup = new PlayerSetup();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    Table titleTable = new Table();

    HorizontalGroup titleGroup = new HorizontalGroup().align(Align.center | Align.left);
    titleGroup.space(10);
    titleTable.add(titleGroup).pad(10).width(Gdx.graphics.getWidth() - 20).top().left();

    ActionButton backButton = new ActionButton("Q", "Back");
    backButton.setKeys(Input.Keys.Q);
    backButton.setAction(table, () -> main.setScreen(new MainMenuScreen(main)));
    titleGroup.addActor(backButton);

    Label title = new Label("You", Main.skin);
    titleGroup.addActor(title);

    Label instructions = new Label(
        "[LIGHT_GRAY]Up & down to navigate a list, left & right to switch lists." +
            " x to add a point or trait/defect, z to remove.",
        Main.skin
    );
    titleGroup.addActor(instructions);

    Table mainTable = new Table();

    attributesGroup = new VerticalGroup().align(Align.top | Align.left);
    skillsGroup = new VerticalGroup().align(Align.top | Align.left);
    defectsGroup = new VerticalGroup().align(Align.top | Align.left);
    traitsGroup = new VerticalGroup().align(Align.top | Align.left);

    mainTable.add(attributesGroup).pad(10).width(Gdx.graphics.getWidth() / 2 - 20).top().left();
    mainTable.add(skillsGroup).pad(10).width(Gdx.graphics.getWidth() / 2 - 20).top().left();
    mainTable.row();
    mainTable.add(defectsGroup).pad(10).width(Gdx.graphics.getWidth() / 2 - 20).top().left();
    mainTable.add(traitsGroup).pad(10).width(Gdx.graphics.getWidth() / 2 - 20).top().left();

    ActionButton continueButton = new ActionButton("ENTER", "Enter Your Name");
    continueButton.setKeys(Input.Keys.ENTER);
    continueButton.setAction(table, () -> main.setScreen(new NameScreen(main, playerSetup)));

    table.add(titleTable);
    table.row();
    table.add(mainTable);
    table.row();
    table.add(continueButton).left();

    updateAttributesGroup();
    updateSkillsGroup();
    updateDefectsGroup();
    updateTraitsGroup();

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
        case ATTRIBUTES:
          if (itemSelected > 0) {
            itemSelected -= 1;

            updateAttributesGroup();
          }

          break;
        case SKILLS:
          if (itemSelected > 0) {
            itemSelected -= 1;

            updateSkillsGroup();
          }

          break;
        case DEFECTS:
          if (itemSelected > 0) {
            itemSelected -= 1;

            updateDefectsGroup();
          }

          break;
        case TRAITS:
          if (itemSelected > 0) {
            itemSelected -= 1;

            updateTraitsGroup();
          }

          break;
        default:
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      switch (sectionSelected) {
        case ATTRIBUTES:
          if (itemSelected < attributesGroup.getChildren().size - 6) {
            itemSelected += 1;

            updateAttributesGroup();
          }

          break;
        case SKILLS:
          if (itemSelected < skillsGroup.getChildren().size - 6) {
            itemSelected += 1;

            updateSkillsGroup();
          }

          break;
        case DEFECTS:
          if (itemSelected < defectsGroup.getChildren().size - 5) {
            itemSelected += 1;

            updateDefectsGroup();
          }

          break;
        case TRAITS:
          if (itemSelected < traitsGroup.getChildren().size - 5) {
            itemSelected += 1;

            updateTraitsGroup();
          }

          break;
        default:
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
      switch (sectionSelected) {
        case ATTRIBUTES:
          sectionSelected = Section.SKILLS;
          itemSelected = 0;
          break;
        case SKILLS:
          sectionSelected = Section.DEFECTS;
          itemSelected = 0;
          break;
        case DEFECTS:
          sectionSelected = Section.TRAITS;
          itemSelected = 0;
          break;
        case TRAITS:
          break;
        default:
      }

      updateAttributesGroup();
      updateSkillsGroup();
      updateDefectsGroup();
      updateTraitsGroup();
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
      switch (sectionSelected) {
        case ATTRIBUTES:
          break;
        case SKILLS:
          sectionSelected = Section.ATTRIBUTES;
          itemSelected = 0;
          break;
        case DEFECTS:
          sectionSelected = Section.SKILLS;
          itemSelected = 0;
          break;
        case TRAITS:
          sectionSelected = Section.DEFECTS;
          itemSelected = 0;
          break;
        default:
      }

      updateAttributesGroup();
      updateSkillsGroup();
      updateDefectsGroup();
      updateTraitsGroup();
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
      switch (sectionSelected) {
        case ATTRIBUTES:
          increaseAttribute();
          updateAttributesGroup();
          break;
        case SKILLS:
          increaseSkill();
          updateSkillsGroup();
          break;
        case DEFECTS:
          addDefect();
          updateDefectsGroup();
          updateTraitsGroup();
          break;
        case TRAITS:
          addTrait();
          updateTraitsGroup();
          break;
        default:
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
      switch (sectionSelected) {
        case ATTRIBUTES:
          decreaseAttribute();
          updateAttributesGroup();
          break;
        case SKILLS:
          decreaseSkill();
          updateSkillsGroup();
          break;
        case DEFECTS:
          removeDefect();
          updateDefectsGroup();
          updateTraitsGroup();
          break;
        case TRAITS:
          removeTrait();
          updateTraitsGroup();
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

    Label instructions = new Label("[DARK_GRAY]1 point is 1 die upgrade", Main.skin);
    attributesGroup.addActor(instructions);
    Label pointsLeft = new Label(attributePoints + "[LIGHT_GRAY] points left", Main.skin);
    attributesGroup.addActor(pointsLeft);
    attributesGroup.addActor(new Label("", Main.skin));

    attributesGroup.addActor(new Label(createAttributeText(0, "Agility", playerSetup.attributes.agility), Main.skin));
    attributesGroup.addActor(new Label(createAttributeText(1, "Strength", playerSetup.attributes.strength), Main.skin));
    attributesGroup.addActor(new Label(createAttributeText(2, "Toughness", playerSetup.attributes.toughness), Main.skin));
  }

  private void updateSkillsGroup() {
    skillsGroup.clear();

    skillsGroup.addActor(new Label("Skills", Main.skin));
    skillsGroup.addActor(new Label("", Main.skin));

    Label instructions = new Label(WordUtils.wrap("[DARK_GRAY]1 point is 1 die upgrade up to the related attribute level, above that is 2 points", 70), Main.skin);
    skillsGroup.addActor(instructions);
    Label pointsLeft = new Label(skillPoints + "[LIGHT_GRAY] points left", Main.skin);
    skillsGroup.addActor(pointsLeft);
    skillsGroup.addActor(new Label("", Main.skin));

    int index = 0;

    for (Map.Entry<String, Integer> entry : playerSetup.skills.levels.entrySet()) {
      String skill = entry.getKey();
      Integer level = entry.getValue();

      skillsGroup.addActor(new Label(createSkillText(index, skill, level), Main.skin));

      index++;
    }
  }

  private void updateDefectsGroup() {
    defectsGroup.clear();

    defectsGroup.addActor(new Label("Defects", Main.skin));
    defectsGroup.addActor(new Label("", Main.skin));

    Label instructions = new Label(WordUtils.wrap("[DARK_GRAY]Taking a major defect gives you 2 points, taking a minor gives you 1", 70), Main.skin);
    defectsGroup.addActor(instructions);
    defectsGroup.addActor(new Label("", Main.skin));

    defectsGroup.addActor(
        new Label(
            createDefectText(0, OneArmComponent.name, OneArmComponent.description, OneArmComponent.reward),
            Main.skin)
    );
  }

  private void updateTraitsGroup() {
    traitsGroup.clear();

    traitsGroup.addActor(new Label("Traits", Main.skin));
    traitsGroup.addActor(new Label("", Main.skin));

    Label pointsLeft = new Label(traitPoints + "[LIGHT_GRAY] points left", Main.skin);
    traitsGroup.addActor(pointsLeft);
    traitsGroup.addActor(new Label("", Main.skin));

    traitsGroup.addActor(
        new Label(
            createTraitText(0, ScoutComponent.name, ScoutComponent.description, ScoutComponent.cost),
        Main.skin)
    );
  }

  private String createAttributeText(int index, String name, int level) {
    String details = "";

    switch (name) {
      case "Agility":
        details = "\n[DARK_GRAY]Accuracy: d[LIGHT_GRAY]" + level
            + "[DARK_GRAY], Dodge: d[LIGHT_GRAY]" + level;

        break;
      case "Strength":
        details = "\n[DARK_GRAY]Damage: d[LIGHT_GRAY]" + level;

        break;
      case "Toughness":
        details = "\n[DARK_GRAY]Max Health: [LIGHT_GRAY]" + level * 10
            + "[DARK_GRAY], Max Oxygen: [LIGHT_GRAY]" + level * 4
            + "[DARK_GRAY], Defense: d[LIGHT_GRAY]" + level;

        break;
      default:
    }

    if (sectionSelected == Section.ATTRIBUTES && index == itemSelected) {
      return "[DARK_GRAY]> [WHITE]" + name + " [DARK_GRAY]d[WHITE]" + level + details;
    } else {
      return "[LIGHT_GRAY]" + name + " [DARK_GRAY]d[WHITE]" + level + details;
    }
  }

  private String createSkillText(int index, String name, int level) {
    String capitalizedName = WordUtils.capitalize(name);
    String levelString = level == 0 ? " [WHITE]" + level : " [DARK_GRAY]d[WHITE]" + level;
    String details = "[DARK_GRAY] " + playerSetup.skills.associations.get(name);

    if (sectionSelected == Section.SKILLS && index == itemSelected) {
      return "[DARK_GRAY]> [WHITE]" + capitalizedName + levelString + details;
    } else {
      return "[LIGHT_GRAY]" + capitalizedName + levelString + details;
    }
  }

  private String createDefectText(int index, String name, String description, int reward) {
    String selected = playerSetup.defects.contains(name, false) ? "[WHITE]! " : "";
    String type = reward == 1 ? "Minor" : "Major";
    String desc = WordUtils.wrap(description, 70);

    if (sectionSelected == Section.DEFECTS && index == itemSelected) {
      return selected + "[DARK_GRAY]> [WHITE]" + type + " [WHITE]" + name + "\n[DARK_GRAY]" + desc;
    } else {
      return selected + "[WHITE]" + type + " [LIGHT_GRAY]" + name + "\n[DARK_GRAY]" + desc;
    }
  }

  private String createTraitText(int index, String name, String description, int cost) {
    String selected = playerSetup.traits.contains(name, false) ? "[WHITE]! " : "";
    String type = cost == 1 ? "Minor" : "Major";
    String desc = WordUtils.wrap(description, 70);

    if (sectionSelected == Section.TRAITS && index == itemSelected) {
      return selected + "[DARK_GRAY]> [WHITE]" + type + " [WHITE]" + name + "\n[DARK_GRAY]" + desc;
    } else {
      return selected + "[WHITE]" + type + " [LIGHT_GRAY]" + name + "\n[DARK_GRAY]" + desc;
    }
  }

  private void increaseAttribute() {
    if (attributePoints == 0) {
      return;
    }

    switch (itemSelected) {
      case 0:
        int agilityLevel = playerSetup.attributes.agility;

        if (agilityLevel < 12) {
          playerSetup.attributes.agility = agilityLevel == 0 ? 4 : agilityLevel + 2;
          attributePoints -= 1;
        }

        break;
      case 1:
        int strengthLevel = playerSetup.attributes.strength;

        if (strengthLevel < 12) {
          playerSetup.attributes.strength = strengthLevel == 0 ? 4 : strengthLevel + 2;
          attributePoints -= 1;
        }

        break;
      case 2:
        int toughnessLevel = playerSetup.attributes.toughness;

        if (toughnessLevel < 12) {
          playerSetup.attributes.toughness = toughnessLevel == 0 ? 4 : toughnessLevel + 2;
          attributePoints -= 1;
        }

        break;
      default:
    }
  }

  private void decreaseAttribute() {
    switch (itemSelected) {
      case 0:
        int agilityLevel = playerSetup.attributes.agility;

        if (agilityLevel > 0) {
          playerSetup.attributes.agility = agilityLevel == 4 ? 0 : agilityLevel - 2;
          attributePoints += 1;
        }

        break;
      case 1:
        int strengthLevel = playerSetup.attributes.strength;

        if (strengthLevel > 0) {
          playerSetup.attributes.strength = strengthLevel == 4 ? 0 : strengthLevel - 2;
          attributePoints += 1;
        }

        break;
      case 2:
        int toughnessLevel = playerSetup.attributes.toughness;

        if (toughnessLevel > 0) {
          playerSetup.attributes.toughness = toughnessLevel == 4 ? 0 : toughnessLevel - 2;
          attributePoints += 1;
        }

        break;
      default:
    }
  }

  private void increaseSkill() {
    if (skillPoints == 0) {
      return;
    }

    int index = 0;

    for (String key : playerSetup.skills.levels.keySet()) {
      if (index == itemSelected) {
        int level = playerSetup.skills.levels.get(key);

        if (level < 12) {
          playerSetup.skills.levels.put(key, level == 0 ? 4 : level + 2);

          if (level >= playerSetup.attributes.strength) {
            skillPoints -= 2;
          } else {
            skillPoints -= 1;
          }
        }

        return;
      }

      index++;
    }
  }

  private void decreaseSkill() {
    int index = 0;

    for (String key : playerSetup.skills.levels.keySet()) {
      if (index == itemSelected) {
        int level = playerSetup.skills.levels.get(key);

        if (level > 0) {
          playerSetup.skills.levels.put(key, level == 4 ? 0 : level - 2);

          if (level >= playerSetup.attributes.strength) {
            skillPoints += 2;
          } else {
            skillPoints += 1;
          }
        }

        return;
      }

      index++;
    }
  }

  private void addDefect() {
    switch (itemSelected) {
      case 0:
        if (!playerSetup.defects.contains(OneArmComponent.name, false)) {
          playerSetup.defects.add(OneArmComponent.name);
          traitPoints += OneArmComponent.reward;
        }

        break;
      default:
    }
  }

  private void removeDefect() {
    switch (itemSelected) {
      case 0:
        if (playerSetup.defects.contains(OneArmComponent.name, false)) {
          playerSetup.defects.removeValue(OneArmComponent.name, false);
          traitPoints -= OneArmComponent.reward;
        }

        break;
      default:
    }
  }

  private void addTrait() {
    switch (itemSelected) {
      case 0:
        if (!playerSetup.traits.contains(ScoutComponent.name, false) && traitPoints >= ScoutComponent.cost) {
          playerSetup.traits.add(ScoutComponent.name);
          traitPoints -= ScoutComponent.cost;
        }

        break;
      default:
    }
  }

  private void removeTrait() {
    switch (itemSelected) {
      case 0:
        if (playerSetup.traits.contains(ScoutComponent.name, false)) {
          playerSetup.traits.removeValue(ScoutComponent.name, false);
          traitPoints += ScoutComponent.cost;
        }

        break;
      default:
    }
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

  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  private enum Section {
    ATTRIBUTES, SKILLS, TRAITS, DEFECTS
  }
}
