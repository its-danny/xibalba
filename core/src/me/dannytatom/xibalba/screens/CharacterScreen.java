package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.utils.ComponentMappers;

public class CharacterScreen implements Screen {
  private final Main main;
  private final Stage stage;

  /**
   * View and manage skills.
   *
   * @param main Instance of the main class
   */
  public CharacterScreen(Main main) {
    this.main = main;

    stage = new Stage();

    Table table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    table.add(createHeader()).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() - 20);
    table.row();
    table.add(createStats()).pad(0, 10, 10, 10).left();
    table.row();
    table.add(createSkills()).pad(0, 10, 10, 10).left();

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

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
      main.setScreen(new InventoryScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
      main.setScreen(new HelpScreen(main));
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

  private VerticalGroup createHeader() {
    String header = "[DARK_GRAY][ ";
    header += "[CYAN]1[WHITE] ";
    header += ComponentMappers.attributes.get(main.player).name;
    header += "[DARK_GRAY] | ";
    header += "[CYAN]2[LIGHT_GRAY] Inventory";
    header += "[DARK_GRAY] | ";
    header += "[CYAN]3[LIGHT_GRAY] Help";
    header += "[DARK_GRAY] ]";

    VerticalGroup group = new VerticalGroup().center();
    group.addActor(new Label(header, main.skin));

    return group;
  }

  private VerticalGroup createStats() {
    VerticalGroup group = new VerticalGroup().left();
    AttributesComponent attrs = ComponentMappers.attributes.get(main.player);

    // Health

    String color;

    if (attrs.health / attrs.maxHealth <= 0.5f) {
      color = "[RED]";
    } else {
      color = "[WHITE]";
    }

    group.addActor(
        new Label(
            "[LIGHT_GRAY]Health:[] " + color + attrs.health
                + "[LIGHT_GRAY]/" + attrs.maxHealth, main.skin
        )
    );

    // Toughness & strength

    int toughness = attrs.toughness;
    int strength = attrs.strength;

    group.addActor(
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

    group.addActor(
        new Label(
            "[LIGHT_GRAY]DEF " + "[YELLOW]" + toughness + "[DARK_GRAY]d "
                + (defense > 0 ? "[LIGHT_GRAY]+ " + "[GREEN]" + defense + "[DARK_GRAY] " : "")
                + "[LIGHT_GRAY]DMG " + "[CYAN]" + strength + "[DARK_GRAY]d "
                + (damage > 0 ? "[LIGHT_GRAY]+ " + "[RED]" + damage + "[DARK_GRAY]d" : ""),
            main.skin
        )
    );


    return group;
  }

  private VerticalGroup createSkills() {
    VerticalGroup group = new VerticalGroup().left();

    SkillsComponent skills = ComponentMappers.skills.get(main.player);

    if (skills.levels.get("unarmed") > 0) {
      group.addActor(skillLine("Unarmed", skills.levels.get("unarmed")));
    }

    if (skills.levels.get("throwing") > 0) {
      group.addActor(skillLine("Throwing", skills.levels.get("throwing")));
    }

    if (skills.levels.get("slashing") > 0) {
      group.addActor(skillLine("Slashing", skills.levels.get("slashing")));
    }

    if (skills.levels.get("piercing") > 0) {
      group.addActor(skillLine("Piercing", skills.levels.get("piercing")));
    }

    if (skills.levels.get("bashing") > 0) {
      group.addActor(skillLine("Bashing", skills.levels.get("bashing")));
    }

    if (skills.levels.get("archery") > 0) {
      group.addActor(skillLine("Archery", skills.levels.get("archery")));
    }

    return group;
  }

  private Label skillLine(String skill, int level) {
    String str = "[LIGHT_GRAY][[";

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

    str += "[LIGHT_GRAY]]";

    return new Label(str + " [WHITE]" + skill, main.skin);
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
