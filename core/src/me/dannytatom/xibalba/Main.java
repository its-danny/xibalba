package me.dannytatom.xibalba;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import me.dannytatom.xibalba.screens.MainMenuScreen;
import me.dannytatom.xibalba.utils.CombatHelpers;
import me.dannytatom.xibalba.utils.EntityHelpers;
import me.dannytatom.xibalba.utils.EquipmentHelpers;
import me.dannytatom.xibalba.utils.InventoryHelpers;
import me.dannytatom.xibalba.utils.SkillHelpers;

public class Main extends Game {
  public State state;
  public AssetManager assets;
  public BitmapFont font;
  public ActionLog log;
  public CombatHelpers combatHelpers;
  public EntityHelpers entityHelpers;
  public InventoryHelpers inventoryHelpers;
  public EquipmentHelpers equipmentHelpers;
  public SkillHelpers skillHelpers;
  public Screen playScreen;
  public Entity player;
  public boolean debug = false;
  public boolean executeTurn = false;

  /**
   * Setup & load the main menu.
   */
  public void create() {
    // Load custom font
    assets = new AssetManager();
    FreeTypeFontGenerator generator =
        new FreeTypeFontGenerator(Gdx.files.internal("ui/Inconsolata.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter =
        new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 16;
    font = generator.generateFont(parameter);
    generator.dispose();

    // Setup text colors
    Colors.put("LIGHT_GRAY", parseColor("c2c2c2"));
    Colors.put("DARK_GRAY", parseColor("666666"));
    Colors.put("CYAN", parseColor("5bb9c7"));
    Colors.put("RED", parseColor("cc4141"));
    Colors.put("DARK_PURPLE", parseColor("241d26"));
    Colors.put("LIGHT_PURPLE", parseColor("706274"));

    // Hide mouse cursor
    Gdx.input.setCursorCatched(true);

    // Start the main menu
    setScreen(new MainMenuScreen(this));
  }

  /**
   * Hex to RGBA.
   *
   * @param hex The color to parse
   * @return A new Color object
   */
  private Color parseColor(String hex) {
    String s1 = hex.substring(0, 2);
    int v1 = Integer.parseInt(s1, 16);
    float f1 = (float) v1 / 255f;
    String s2 = hex.substring(2, 4);
    int v2 = Integer.parseInt(s2, 16);
    float f2 = (float) v2 / 255f;
    String s3 = hex.substring(4, 6);
    int v3 = Integer.parseInt(s3, 16);
    float f3 = (float) v3 / 255f;
    return new Color(f1, f2, f3, 1);
  }

  public enum State {
    PLAYING, TARGETING
  }
}