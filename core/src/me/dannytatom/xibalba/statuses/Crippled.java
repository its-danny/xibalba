package me.dannytatom.xibalba.statuses;

public class Crippled {
  private int lifeCounter = 0;
  private int turnCounter = 0;

  public Crippled() {

  }

  /**
   * For every turn you take, everything else takes 2 more.
   */
  public void onTurn() {
    if (turnCounter == 2) {
      turnCounter = 0;
      lifeCounter += 1;
    } else {
      turnCounter += 1;
    }
  }

  public boolean canAct() {
    return turnCounter == 0;
  }

  public boolean shouldRemove() {
    return lifeCounter == 5;
  }
}
