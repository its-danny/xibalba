package me.dannytatom.xibalba.components.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class DamageEffectComponent extends Component {
  public Entity starter;
  public Entity item;
  public int currentTurn = 0;

  public DamageEffectComponent(Entity starter, Entity item) {
    this.starter = starter;
    this.item = item;
  }
}
