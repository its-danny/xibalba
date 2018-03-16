package me.dannytatom.xibalba.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.EffectsComponent;
import me.dannytatom.xibalba.components.ItemComponent;
import me.dannytatom.xibalba.effects.Bleed;
import me.dannytatom.xibalba.effects.Charm;
import me.dannytatom.xibalba.effects.DealDamage;
import me.dannytatom.xibalba.effects.Effect;
import me.dannytatom.xibalba.effects.Poison;
import me.dannytatom.xibalba.effects.RaiseHealth;
import me.dannytatom.xibalba.effects.StartFire;
import me.dannytatom.xibalba.ui.ActionButton;
import me.dannytatom.xibalba.utils.ComponentMappers;
import me.dannytatom.xibalba.utils.yaml.ItemData;
import me.dannytatom.xibalba.utils.yaml.ItemRequiredComponentData;
import me.dannytatom.xibalba.world.WorldManager;

import org.apache.commons.lang3.text.WordUtils;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class CraftScreen implements Screen {
  private final Stage stage;
  private final Table table;
  private final HashMap<String, ItemData> recipes;
  private final VerticalGroup recipeGroup;

  /**
   * Craft screen.
   *
   * @param main Instance of Main
   */
  public CraftScreen(Main main) {
    stage = new Stage(new FitViewport(960, 540));

    Constructor constructor = new Constructor(ItemData.class);
    constructor.addTypeDescription(new TypeDescription(Bleed.class, "!Bleed"));
    constructor.addTypeDescription(new TypeDescription(Charm.class, "!Charm"));
    constructor.addTypeDescription(new TypeDescription(DealDamage.class, "!DealDamage"));
    constructor.addTypeDescription(new TypeDescription(Poison.class, "!Poison"));
    constructor.addTypeDescription(new TypeDescription(RaiseHealth.class, "!RaiseHealth"));
    constructor.addTypeDescription(new TypeDescription(StartFire.class, "!StartFire"));
    TypeDescription itemDescription = new TypeDescription(ItemData.class);
    itemDescription.putListPropertyType("requiredComponent", ItemRequiredComponentData.class);
    constructor.addTypeDescription(itemDescription);
    Yaml yaml = new Yaml(constructor);
    recipes = new HashMap<>();

    for (Map.Entry<String, String> entry : Main.itemsData.entrySet()) {
      ItemData data = (ItemData) yaml.load(entry.getValue());

      if (data.requiredComponents != null) {
        recipes.put(entry.getKey(), data);
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
      Entity testItem = WorldManager.entityFactory.createItem(entry.getKey(), new Vector2(0, 0));

      if (WorldManager.itemHelpers.hasComponentsForItem(WorldManager.player, testItem)) {
        ItemComponent testItemDetails = ComponentMappers.item.get(testItem);

        // If you look at the docs for Input.Keys, number keys are offset by 7
        // (e.g. 0 = 7, 1 = 8, etc)
        ActionButton button = new ActionButton(i + 1, testItemDetails.name);
        button.setKeys(i + 8);
        button.setAction(table, () -> {
          ArrayList<Entity> components
              = WorldManager.itemHelpers.getComponentsForItem(WorldManager.player, testItem);
          ArrayList<Effect> effects = new ArrayList<>();

          for (Entity component : components) {
            ArrayList<Effect> componentEffects
                = WorldManager.itemHelpers.effectsFromComponent(component);

            if (componentEffects != null) {
              effects.addAll(componentEffects);
            }
          }

          int amountToSpawn;

          if (testItemDetails.craftedRange.size() > 1) {
            amountToSpawn = MathUtils.random(
                testItemDetails.craftedRange.get(0), testItemDetails.craftedRange.get(1)
            );
          } else {
            amountToSpawn = testItemDetails.craftedRange.get(0);
          }

          for (int j = 0; j < amountToSpawn; j++) {
            Entity craftedItem
                = WorldManager.entityFactory.createItem(entry.getKey(), new Vector2(0, 0));
            ItemComponent craftedItemDetails = ComponentMappers.item.get(craftedItem);

            craftedItemDetails.quality
                = WorldManager.itemHelpers.qualityFromComponents(components);
            craftedItemDetails.stoneMaterial
                = WorldManager.itemHelpers.materialFromComponents(components);

            if (craftedItemDetails.attributes.get("defense") != null) {
              if (craftedItemDetails.attributes.get("defense")
                  + craftedItemDetails.quality.getModifier() >= 0) {
                craftedItemDetails.attributes.put(
                    "defense", craftedItemDetails.attributes.get("defense")
                        + craftedItemDetails.quality.getModifier()
                );
              }
            }

            if (!ComponentMappers.effects.has(craftedItem)) {
              craftedItem.add(new EffectsComponent());
            }

            ComponentMappers.effects.get(craftedItem).effects.add(
                effects.get(MathUtils.random(0, effects.size() - 1))
            );

            WorldManager.itemHelpers.addToInventory(WorldManager.player, craftedItem, false);
            WorldManager.log.add(
                "inventory.crafted",
                WorldManager.itemHelpers.getName(WorldManager.player, craftedItem)
            );
          }

          WorldManager.itemHelpers.removeMultipleFromInventory(WorldManager.player, components);
          WorldManager.executeTurn = true;

          setupRecipes();
        });

        recipeGroup.addActor(button);

        recipeGroup.addActor(new Label(
            "[DARK_GRAY]" + WordUtils.wrap(testItemDetails.description, 140),
            Main.skin
        ));

        ArrayList<String> materialList = new ArrayList<>();
        for (ItemComponent.RequiredComponent component : testItemDetails.requiredComponents) {
          ItemComponent requiredComponentDetails
              = ComponentMappers.item.get(component.item);
          materialList.add(component.amount + " " + requiredComponentDetails.name);
        }

        recipeGroup.addActor(new Label(
            "[DARK_GRAY]Requires: []" + String.join(", ", materialList), Main.skin
        ));

        recipeGroup.addActor(new Label("", Main.skin));

        i++;
      }
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
