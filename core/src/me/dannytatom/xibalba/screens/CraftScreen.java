package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.yaml.ItemData;
import me.dannytatom.xibalba.world.WorldManager;
import org.apache.commons.lang3.text.WordUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CraftScreen implements Screen {
  private final Main main;
  private final Stage stage;
  private final Table table;
  private final HashMap<String, ItemData> recipes;
  private final VerticalGroup recipeGroup;
  private final I18NBundle i18n;

  public CraftScreen(Main main) {
    this.main = main;

    stage = new Stage(new FitViewport(960, 540));
    i18n = Main.assets.get("i18n/xibalba", I18NBundle.class);

    Yaml yaml = new Yaml(new Constructor(ItemData.class));
    FileHandle directoryHandle = Gdx.files.internal("data/items");
    recipes = new HashMap<>();

    for (FileHandle item : directoryHandle.list()) {
      ItemData data = (ItemData) yaml.load(item.reader());

      if (data.craftable) {
        recipes.put(item.nameWithoutExtension(), data);
      }
    }

    table = new Table();
    table.setFillParent(true);
    table.left().top();
    stage.addActor(table);

    Table titleTable = new Table();

    HorizontalGroup titleGroup = new HorizontalGroup();
    titleGroup.space(10);
    titleTable.add(titleGroup).pad(10).width(Gdx.graphics.getWidth() - 20);

    ActionButton closeButton = new ActionButton("Q", null);
    closeButton.setKeys(Input.Keys.Q);
    closeButton.setAction(table, () -> main.setScreen(Main.playScreen));
    titleGroup.addActor(closeButton);

    Label title = new Label("Craft", Main.skin);
    titleGroup.addActor(title);

    recipeGroup = new VerticalGroup().top().left().columnLeft();

    Table craftTable = new Table();
    craftTable.add(recipeGroup).pad(10).top().left().width(Gdx.graphics.getWidth() / 2);

    table.add(titleTable);
    table.row();
    table.add(craftTable).left();

    setupRecipes();

    Gdx.input.setInputProcessor(stage);
    stage.setKeyboardFocus(table);
  }

  @Override
  public void show() {

  }

  private void setupRecipes() {
    recipeGroup.clear();

    int i = 0;
    for (Map.Entry<String, ItemData> entry : recipes.entrySet()) {
      Entity item = WorldManager.entityFactory.createItem(entry.getKey(), new Vector2(0, 0));
      ItemComponent itemDetails = ComponentMappers.item.get(item);

      if (WorldManager.itemHelpers.hasComponentsForItem(WorldManager.player, item)) {
        // If you look at the docs for Input.Keys, number keys are offset by 7
        // (e.g. 0 = 7, 1 = 8, etc)

        ActionButton button = new ActionButton(i + 1, itemDetails.name);
        button.setKeys(i + 8);
        button.setAction(table, () -> {
          int amountToSpawn;

          if (itemDetails.craftedRange.size() > 1) {
            amountToSpawn = MathUtils.random(itemDetails.craftedRange.get(0), itemDetails.craftedRange.get(1));
          } else {
            amountToSpawn = itemDetails.craftedRange.get(0);
          }

          for (int j = 0; j < amountToSpawn; j++) {
            itemDetails.quality = WorldManager.itemHelpers.qualityFromComponents(WorldManager.player, item);
            itemDetails.stoneMaterial = WorldManager.itemHelpers.materialFromComponents(WorldManager.player, item);

            WorldManager.itemHelpers.addToInventory(WorldManager.player, item, false);
            WorldManager.log.add("inventory.crafted", WorldManager.itemHelpers.getName(WorldManager.player, item));
          }

          for (ItemComponent.RequiredComponent requiredComponent : itemDetails.requiredComponents) {
            ItemComponent requiredComponentDetails = ComponentMappers.item.get(requiredComponent.item);
            WorldManager.itemHelpers.removeComponentsFromInventory(
              WorldManager.player, requiredComponentDetails.name, requiredComponent.amount
            );

            WorldManager.executeTurn = true;

            setupRecipes();
          }
        });

        recipeGroup.addActor(button);

        i++;
      } else {
        recipeGroup.addActor(new Label(itemDetails.name, Main.skin));
      }

      recipeGroup.addActor(new Label(
        "[DARK_GRAY]" + WordUtils.wrap(itemDetails.description, 140), Main.skin
      ));

      ArrayList<String> materialList = new ArrayList<>();
      for (ItemComponent.RequiredComponent requiredComponent : itemDetails.requiredComponents) {
        ItemComponent requiredComponentDetails = ComponentMappers.item.get(requiredComponent.item);
        materialList.add(requiredComponent.amount + " " + requiredComponentDetails.name);
      }

      recipeGroup.addActor(new Label(
        "[DARK_GRAY]Requires: []" + String.join(", ", materialList), Main.skin
      ));

      recipeGroup.addActor(new Label("", Main.skin));
    }
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

    stage.act(delta);
    stage.draw();
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
}
