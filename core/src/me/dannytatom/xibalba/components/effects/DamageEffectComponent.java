package me.dannytatom.xibalba.components.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class DamageEffectComponent extends Component {
  public Entity starter;
  public int range;
  public int turns;
  public int damage;
  public int currentTurn = 0;

  public DamageEffectComponent(Entity starter, int range, int turns, int damage) {
    this.starter = starter;
    this.range = range;
    this.turns = turns;
    this.damage = damage;
  }
}
