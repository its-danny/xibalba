package me.dannytatom.xibalba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUDRenderer {
    private final Main main;

    private Stage stage;
    private Skin skin;

    private VerticalGroup actionLog;

    public HUDRenderer(Main main, SpriteBatch batch) {
        this.main = main;

        Viewport viewport = new FitViewport(960, 540, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        skin = new Skin();
        skin.add("Inconsolata", this.main.font, BitmapFont.class);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/uiskin.json"));
        skin.getFont("default-font").getData().markupEnabled = true;

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        actionLog = new VerticalGroup().left();
        table.add(actionLog).pad(0, 10, 10, 10).left();

        stage.addActor(table);
    }

    public void render(float delta) {
        actionLog.clear();

        for (int i = 0; i < main.log.things.size(); i++) {
            Label label = new Label(main.log.things.get(i), skin);
            label.setColor(1f, 1f, 1f, (i == 0 ? 1f : 1f / (i + 1)));
            actionLog.addActor(label);
        }

        stage.act(delta);
        stage.draw();
    }
}
