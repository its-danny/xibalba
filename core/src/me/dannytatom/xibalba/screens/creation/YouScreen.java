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
import me.dannytatom.xibalba.screens.MainMenuScreen;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.PlayerSetup;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Map;

public class YouScreen implements Screen {

  private int attributePoints;
  private int skillPoints;
  private final Stage stage;
  private final PlayerSetup playerSetup;

  private VerticalGroup attributes;
  private VerticalGroup skills;

  private Section sectionSelected = Section.ATTRIBUTES;
  private int itemSelected = 0;

  private enum Section {
    ATTRIBUTES, SKILLS
  }

  public YouScreen(Main main) {
    attributePoints = 5;
    skillPoints = 15;
    stage = new Stage(new FitViewport(960, 540));
    playerSetup = new PlayerSetup();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    table.pad(10);
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

    attributes = new VerticalGroup().align(Align.top | Align.left);
    skills = new VerticalGroup().align(Align.top | Align.left);

    mainTable.add(attributes).pad(10).width(Gdx.graphics.getWidth() / 2 - 20).top().left();
    mainTable.add(skills).pad(10).width(Gdx.graphics.getWidth() / 2 - 20).top().left();

    ActionButton continueButton = new ActionButton("ENTER", "Review");
    continueButton.setKeys(Input.Keys.ENTER);
    continueButton.setAction(table, () -> main.setScreen(new ReviewScreen(main, playerSetup)));

    table.add(titleTable);
    table.row();
    table.add(mainTable);
    table.row();
    table.add(continueButton).left();

    updateAttributesGroup();
    updateSkillsGroup();

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
      if (sectionSelected == Section.ATTRIBUTES) {
        if (itemSelected > 0) {
          itemSelected -= 1;

          updateAttributesGroup();
        }
      } else if (sectionSelected == Section.SKILLS) {
        if (itemSelected > 0) {
          itemSelected -= 1;

          updateSkillsGroup();
        }
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      if (sectionSelected == Section.ATTRIBUTES) {
        if (itemSelected < attributes.getChildren().size - 6) {
          itemSelected += 1;

          updateAttributesGroup();
        }
      } else if (sectionSelected == Section.SKILLS) {
        if (itemSelected < skills.getChildren().size - 6) {
          itemSelected += 1;

          updateSkillsGroup();
        }
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
      if (sectionSelected == Section.ATTRIBUTES) {
        sectionSelected = Section.SKILLS;
        itemSelected = 0;

        updateAttributesGroup();
        updateSkillsGroup();
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
      if (sectionSelected == Section.SKILLS) {
        sectionSelected = Section.ATTRIBUTES;
        itemSelected = 0;

        updateAttributesGroup();
        updateSkillsGroup();
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
      if (sectionSelected == Section.ATTRIBUTES) {
        increaseAttribute();
        updateAttributesGroup();
      } else if (sectionSelected == Section.SKILLS) {
        increaseSkill();
        updateSkillsGroup();
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
      if (sectionSelected == Section.ATTRIBUTES) {
        decreaseAttribute();
        updateAttributesGroup();
      } else if (sectionSelected == Section.SKILLS) {
        decreaseSkill();
        updateSkillsGroup();
      }
    }

    stage.act(delta);
    stage.draw();
  }

  private void updateAttributesGroup() {
    attributes.clear();

    attributes.addActor(new Label("Attributes", Main.skin));
    attributes.addActor(new Label("", Main.skin));

    Label instructions = new Label("[DARK_GRAY]1 point is 1 die upgrade", Main.skin);
    attributes.addActor(instructions);
    Label pointsLeft = new Label(attributePoints + "[LIGHT_GRAY] points left", Main.skin);
    attributes.addActor(pointsLeft);
    attributes.addActor(new Label("", Main.skin));

    attributes.addActor(new Label(createAttributeText(0, "Strength", playerSetup.attributes.strength), Main.skin));
    attributes.addActor(new Label(createAttributeText(1, "Toughness", playerSetup.attributes.toughness), Main.skin));
  }

  private void updateSkillsGroup() {
    skills.clear();

    skills.addActor(new Label("Skills", Main.skin));
    skills.addActor(new Label("", Main.skin));

    Label instructions = new Label(WordUtils.wrap("[DARK_GRAY]1 point is 1 die upgrade up to the related attribute level, above that is 2 points", 50), Main.skin);
    skills.addActor(instructions);
    Label pointsLeft = new Label(skillPoints + "[LIGHT_GRAY] points left", Main.skin);
    skills.addActor(pointsLeft);
    skills.addActor(new Label("", Main.skin));

    int index = 0;

    for (Map.Entry<String , Integer> entry : playerSetup.skills.levels.entrySet()) {
      String skill = entry.getKey();
      Integer level = entry.getValue();

      skills.addActor(new Label(createSkillText(index, skill, level), Main.skin));

      index++;
    }
  }

  private String createAttributeText(int index, String name, int level) {
    if (sectionSelected == Section.ATTRIBUTES && index == itemSelected) {
      return "[DARK_GRAY]> [WHITE]" + name + " [DARK_GRAY]d[WHITE]" + level;
    } else {
      return "[LIGHT_GRAY]" + name + " [DARK_GRAY]d[WHITE]" + level;
    }
  }

  private String createSkillText(int index, String name, int level) {
    String capitalizedName = WordUtils.capitalize(name);
    String levelString = level == 0 ? " [WHITE]" + level : " [DARK_GRAY]d[WHITE]" + level;
    String tiedTo = " [DARK_GRAY]Strength";

    if (sectionSelected == Section.SKILLS && index == itemSelected) {
      return "[DARK_GRAY]> [WHITE]" + capitalizedName + levelString + tiedTo;
    } else {
      return "[LIGHT_GRAY]" + capitalizedName + levelString + tiedTo;
    }
  }

  private void increaseAttribute() {
    if (attributePoints == 0) {
      return;
    }

    switch (itemSelected) {
      case 0:
        int strengthLevel = playerSetup.attributes.strength;

        if (strengthLevel < 12) {
          playerSetup.attributes.strength = strengthLevel == 0 ? 4 : strengthLevel + 2;
          attributePoints -= 1;
        }
        break;
      case 1:
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
        int strengthLevel = playerSetup.attributes.strength;

        if (strengthLevel > 0) {
          playerSetup.attributes.strength = strengthLevel == 4 ? 0 : strengthLevel - 2;
          attributePoints += 1;
        }
        break;
      case 1:
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
}
