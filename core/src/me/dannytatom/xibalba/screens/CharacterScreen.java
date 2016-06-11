package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.*;

import java.util.ArrayList;

public class CharacterScreen implements Screen {
    private final Main main;

    private Skin skin;
    private Stage stage;

    /**
     * View and manage skills.
     *
     * @param main Instance of the main class
     */
    public CharacterScreen(Main main) {
        this.main = main;

        stage = new Stage();

        skin = new Skin();
        skin.add("Inconsolata", this.main.font, BitmapFont.class);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/uiskin.json"));
        skin.getFont("default-font").getData().markupEnabled = true;

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
        group.addActor(new Label(header, skin));

        return group;
    }

    private VerticalGroup createStats() {
        VerticalGroup group = new VerticalGroup().left();
        AttributesComponent attrs = main.player.getComponent(AttributesComponent.class);

        // Health

        String color = "";

        if (attrs.health / attrs.maxHealth <= 0.5f) {
            color = "[RED]";
        } else {
            color = "[WHITE]";
        }

        group.addActor(
                new Label(
                        "[LIGHT_GRAY]Health:[] " + color + attrs.health
                                + "[LIGHT_GRAY]/" + attrs.maxHealth, skin
                )
        );

        // Toughness & damage

        int damage = attrs.damage;
        Entity wielded = main.equipmentHelpers.getWeapon();

        if (wielded != null) {
            ItemComponent ic = wielded.getComponent(ItemComponent.class);
            damage += ic.attributes.get("damage");
        }

        group.addActor(
                new Label(
                        "[LIGHT_GRAY]Toughness: []" + attrs.toughness
                                + ", [LIGHT_GRAY]Damage: []" + damage, skin
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
            group.addActor(new Label(trait.name + " [LIGHT_GRAY]" + trait.description, skin));
        }

        for (Entity entity : defects) {
            DefectComponent defect = entity.getComponent(DefectComponent.class);
            group.addActor(new Label(defect.name + " [LIGHT_GRAY]" + defect.description, skin));
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

        return new Label(str + " [WHITE]" + skill, skin);
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
