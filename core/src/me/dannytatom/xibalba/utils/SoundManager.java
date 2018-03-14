package me.dannytatom.xibalba.utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import me.dannytatom.xibalba.Main;

public class SoundManager {
  private final Array<Sound> unarmed;
  private final Array<Sound> piercing;

  /**
   * Handles sound effects, duh.
   */
  public SoundManager() {
    unarmed = new Array<>();
    unarmed.add(Main.assets.get("sounds/Stab_Punch_Hack_12.wav"));
    unarmed.add(Main.assets.get("sounds/Stab_Punch_Hack_13.wav"));
    unarmed.add(Main.assets.get("sounds/Stab_Punch_Hack_14.wav"));
    unarmed.add(Main.assets.get("sounds/Stab_Punch_Hack_15.wav"));
    unarmed.add(Main.assets.get("sounds/Stab_Punch_Hack_17.wav"));
    unarmed.add(Main.assets.get("sounds/Stab_Punch_Hack_22.wav"));

    piercing = new Array<>();
    piercing.add(Main.assets.get("sounds/Stab_Punch_Hack_09.wav"));
    piercing.add(Main.assets.get("sounds/Stab_Punch_Hack_18.wav"));
    piercing.add(Main.assets.get("sounds/Stab_Punch_Hack_19.wav"));
    piercing.add(Main.assets.get("sounds/Stab_Punch_Hack_63.wav"));
  }

  public void unarmed() {
    Sound sound = unarmed.get(MathUtils.random(0, unarmed.size - 1));
    sound.play(MathUtils.random(.3f, 1f));
  }

  public void slashing() {

  }

  public void piercing() {
    Sound sound = piercing.get(MathUtils.random(0, piercing.size - 1));
    sound.play(MathUtils.random(.3f, 1f));
  }

  public void bashing() {

  }
}
