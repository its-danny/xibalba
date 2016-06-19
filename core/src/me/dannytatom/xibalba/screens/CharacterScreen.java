package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.DefectComponent;
import me.dannytatom.xibalba.components.DefectsComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.components.SkillsComponent;
import me.dannytatom.xibalba.components.TraitComponent;
import me.dannytatom.xibalba.components.TraitsComponent;

import java.util.ArrayList;

public class CharacterScreen implements Screen {
  private final Main main;
  private Stage stage;

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
    table.row();
    table.add(createTraits()).pad(0, 10, 10, 10).left();

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
      main.setScreen(new InventoryScreen(main));
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      main.setScreen(main.playScreen);
    }
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  private VerticalGroup createHeader() {
    String header = "[DARK_GRAY][ ";
    header += "[CYAN]1[WHITE] ";
    header += main.player.getComponent(AttributesComponent.class).name;
    header += "[DARK_GRAY] | ";
    header += "[CYAN]2[LIGHT_GRAY] Inventory";
    header += "[DARK_GRAY] ]";

    VerticalGroup group = new VerticalGroup().center();
    group.addActor(new Label(header, main.skin));

    return group;
  }

  private VerticalGroup createStats() {
    VerticalGroup group = new VerticalGroup().left();
    AttributesComponent attrs = main.player.getComponent(AttributesComponent.class);

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

    // Toughness & damage

    int defense = attrs.defense + main.equipmentHelpers.getCombinedDefense(main.player);

    int damage = attrs.damage;
    Entity heldWeapon = main.equipmentHelpers.getPrimaryWeapon(main.player);

    if (heldWeapon != null) {
      ItemComponent ic = heldWeapon.getComponent(ItemComponent.class);
      damage += ic.attributes.get("hitDamage");
    }

    group.addActor(
        new Label(
            "[LIGHT_GRAY]Defense: []" + defense
                + ", [LIGHT_GRAY]Damage: []" + damage, main.skin
        )
    );

    return group;
  }

  private VerticalGroup createSkills() {
    VerticalGroup group = new VerticalGroup().left();

    SkillsComponent skills = this.main.player.getComponent(SkillsComponent.class);

    if (skills.unarmed > 0) {
      group.addActor(skillLine("Unarmed", skills.unarmed));
    }

    if (skills.throwing > 0) {
      group.addActor(skillLine("Throwing", skills.throwing));
    }

    if (skills.slashing > 0) {
      group.addActor(skillLine("Slashing", skills.slashing));
    }

    if (skills.stabbing > 0) {
      group.addActor(skillLine("Stabbing", skills.stabbing));
    }

    return group;
  }

  private VerticalGroup createTraits() {
    VerticalGroup group = new VerticalGroup().left();

    ArrayList<Entity> traits = main.player.getComponent(TraitsComponent.class).traits;
    ArrayList<Entity> defects = main.player.getComponent(DefectsComponent.class).defects;

    for (Entity entity : traits) {
      TraitComponent trait = entity.getComponent(TraitComponent.class);
      group.addActor(new Label(trait.name + " [LIGHT_GRAY]" + trait.description, main.skin));
    }

    for (Entity entity : defects) {
      DefectComponent defect = entity.getComponent(DefectComponent.class);
      group.addActor(new Label(defect.name + " [LIGHT_GRAY]" + defect.description, main.skin));
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
        str += "[WHITE]x[LIGHT_GRAY]xxxx";
        break;
      case 6:
        str += "[WHITE]xx[LIGHT_GRAY]xxx";
        break;
      case 8:
        str += "[WHITE]xxx[LIGHT_GRAY]xx";
        break;
      case 10:
        str += "[WHITE]xxxx[LIGHT_GRAY]x";
        break;
      case 12:
        str += "[WHITE]xxxxx[LIGHT_GRAY]";
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
