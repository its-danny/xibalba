package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.dannytatom.xibalba.components.AttributesComponent;
import me.dannytatom.xibalba.components.ItemComponent;

public class UiRenderer {
  private final Main main;

  private Engine engine;
  private Skin skin;
  private Stage stage;
  private VerticalGroup attributes;
  private VerticalGroup inventory;
  private VerticalGroup log;

  /**
   * Renders the UI.
   *
   * @param main   Instance of the Main class
   * @param engine Ashley engine
   */
  public UiRenderer(Main main, Engine engine) {
    this.main = main;
    this.engine = engine;

    skin = new Skin();
    skin.add("Inconsolata", this.main.font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));
    skin.getFont("default-font").getData().markupEnabled = true;

    stage = new Stage(new FitViewport(1366, 768));

    Table sidePanel = new Table();
    sidePanel.setFillParent(true);
    sidePanel.right().top();

    stage.addActor(sidePanel);

    attributes = new VerticalGroup().left();
    sidePanel.add(attributes).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 4 - 20);
    sidePanel.row();
    inventory = new VerticalGroup().left();
    sidePanel.add(inventory).pad(0, 10, 10, 10).width(Gdx.graphics.getWidth() / 4 - 20);

    Table topPanel = new Table();
    topPanel.setFillParent(true);
    topPanel.top().left();

    stage.addActor(topPanel);

    log = new VerticalGroup().left();
    topPanel.add(log).pad(10, 10, 10, 10).width(Gdx.graphics.getWidth() / 4 * 3 - 20);
  }

  /**
   * Render shit, duh.
   *
   * @param delta Elapsed time
   */
  public void render(float delta) {
    renderLog();
    renderAttributes();
    renderEquipment();

    stage.act(delta);
    stage.draw();
  }

  private void renderLog() {
    float alpha = 1;

    log.clear();

    for (int i = 0; i < main.log.things.size(); i++) {
      Label label = new Label(main.log.things.get(i), skin);
      label.setColor(1, 1, 1, alpha);

      log.addActor(label);

      alpha -= .15;
    }
  }

  private void renderAttributes() {
    AttributesComponent attrs = main.player.getComponent(AttributesComponent.class);

    attributes.clear();

    // Player name
    attributes.addActor(new Label(attrs.name, skin));

    // Health
    String color = "";

    if (attrs.health / attrs.maxHealth <= 0.5f) {
      color = "[RED]";
    } else {
      color = "[WHITE]";
    }

    attributes.addActor(
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

    attributes.addActor(
        new Label(
            "[LIGHT_GRAY]Toughness: []" + attrs.toughness
                + ", [LIGHT_GRAY]Damage: []" + damage, skin
        )
    );
  }

  private void renderEquipment() {
    inventory.clear();

    Entity wielded = main.equipmentHelpers.getWeapon();

    if (wielded != null) {
      ItemComponent item = wielded.getComponent(ItemComponent.class);

      inventory.addActor(
          new Label(
              "[LIGHT_GRAY]Wielding:[] " + item.name,
              skin
          )
      );
    }
  }

  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  public void dispose() {
    stage.dispose();
  }
}
