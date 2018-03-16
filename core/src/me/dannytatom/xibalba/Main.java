package me.dannytatom.xibalba;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

import me.dannytatom.xibalba.screens.LoadingScreen;
import me.dannytatom.xibalba.utils.CameraShake;
import me.dannytatom.xibalba.utils.HandheldCamera;
import me.dannytatom.xibalba.utils.SoundManager;
import me.dannytatom.xibalba.utils.SpriteAccessor;

public class Main extends Game {
  public static final int SPRITE_WIDTH = 10;
  public static final int SPRITE_HEIGHT = 10;
  public static Debug debug;
  public static AssetManager assets;
  public static TextureAtlas asciiAtlas;
  public static HashMap<String, String> abilitiesData;
  public static HashMap<String, String> traitsData;
  public static HashMap<String, String> defectsData;
  public static HashMap<String, String> godsData;
  public static HashMap<String, String> enemiesData;
  public static HashMap<String, String> itemsData;
  public static Skin skin;
  public static Screen playScreen;
  public static TweenManager tweenManager;
  public static SoundManager soundManager;
  public static HandheldCamera handheldCamera;
  public static CameraShake cameraShake;

  /**
   * From mouse position to tile position.
   *
   * @param camera Main world camera
   * @return A vector2 of the hovered tile position
   */
  public static Vector2 mousePositionToWorld(OrthographicCamera camera) {
    Vector3 position = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
    camera.unproject(position);

    return new Vector2(
        Math.round(position.x) / Main.SPRITE_WIDTH,
        Math.round(position.y) / Main.SPRITE_HEIGHT
    );
  }

  /**
   * Hex to RGBA.
   *
   * @param hex The color to parse
   * @return A new Color object
   */
  public static Color parseColor(String hex) {
    if (isHexNumber(hex)) {
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
    } else {
      return Color.WHITE;
    }
  }

  private static boolean isHexNumber(String string) {
    try {
      //noinspection ResultOfMethodCallIgnored
      Long.parseLong(string, 16);

      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  /**
   * Setup & load the main menu.
   */
  public void create() {
    // Debug shit
    debug = new Debug();

    // Load custom font
    assets = new AssetManager();
    FreeTypeFontGenerator generator =
        new FreeTypeFontGenerator(Gdx.files.internal("ui/Aller_Rg.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter =
        new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 12;
    BitmapFont font = generator.generateFont(parameter);
    generator.dispose();

    // Create UI skin
    skin = new Skin();
    skin.add("Aller", font, BitmapFont.class);
    skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
    skin.load(Gdx.files.internal("ui/uiskin.json"));
    skin.getFont("default-font").getData().markupEnabled = true;

    // Setup text colors
    Colors.put("LIGHT_GRAY", parseColor("c2c2c2"));
    Colors.put("DARK_GRAY", parseColor("666666"));
    Colors.put("CYAN", parseColor("67C8CF"));
    Colors.put("RED", parseColor("D67474"));
    Colors.put("YELLOW", parseColor("E0DFB1"));
    Colors.put("GREEN", parseColor("67CF8B"));

    // Environment colors
    Colors.put("forestFloor", parseColor("78AD8A"));
    Colors.put("forestFloorWet", parseColor("70BBAD"));
    Colors.put("forestTree-1", parseColor("67CF8B"));
    Colors.put("forestTree-2", parseColor("77E09B"));
    Colors.put("forestTree-3", parseColor("4AC775"));

    Colors.put("caveFloor-1", parseColor("7A7971"));
    Colors.put("caveFloor-2", parseColor("8C8B82"));
    Colors.put("caveFloor-3", parseColor("696862"));
    Colors.put("caveFloorWet", parseColor("71A48E"));
    Colors.put("caveWall", parseColor("66655C"));

    Colors.put("waterShallowLightBlue", parseColor("67C8CF"));
    Colors.put("waterShallowDarkBlue", parseColor("139EA8"));
    Colors.put("waterDeepLightBlue", parseColor("139EA8"));
    Colors.put("waterDeepDarkBlue", parseColor("0B7880"));

    Colors.put("waterShallowLightGreen", parseColor("67CFAB"));
    Colors.put("waterShallowDarkGreen", parseColor("13A88F"));
    Colors.put("waterDeepLightGreen", parseColor("13A88F"));
    Colors.put("waterDeepDarkGreen", parseColor("0B8074"));

    Colors.put("fire-1", parseColor("ED6161"));
    Colors.put("fire-2", parseColor("EDBE61"));
    Colors.put("fire-3", parseColor("ED9661"));

    // Decoration colors
    Colors.put("stone", Colors.get("LIGHT_GRAY"));
    Colors.put("bridge", parseColor("969482"));

    // Background colors
    Colors.put("screenBackground", parseColor("293033"));
    Colors.put("forestBackground", parseColor("29332F"));
    Colors.put("caveBackground", parseColor("293033"));

    // Tween manager
    tweenManager = new TweenManager();
    Tween.setCombinedAttributesLimit(4);
    Tween.registerAccessor(Sprite.class, new SpriteAccessor());

    // Cameras
    handheldCamera = new HandheldCamera();
    cameraShake = new CameraShake();

    // Start the main menu
    setScreen(new LoadingScreen(this));
  }
}