package me.dannytatom.xibalba.map;

// https://github.com/SquidPony/SquidLib/blob/master/src/squidpony/squidgrid/util/DirectionIntercardinal.java
public enum Direction {
  UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), UP_LEFT(-1, -1), UP_RIGHT(1, -1), DOWN_LEFT(-1, 1), DOWN_RIGHT(1, 1), NONE(0, 0);

  public static final Direction[] DIAGONALS = {UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};

  public final int deltaX;
  public final int deltaY;

  Direction(int x, int y) {
    this.deltaX = x;
    this.deltaY = y;
  }
}
