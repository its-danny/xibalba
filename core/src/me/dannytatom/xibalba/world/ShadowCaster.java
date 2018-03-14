package me.dannytatom.xibalba.world;

public class ShadowCaster {
  private int width;
  private int height;
  private int startX;
  private int startY;
  private float[][] lightMap;
  private float[][] resistanceMap;
  private float radius;

  /**
   * http://www.roguebasin.com/index.php?title=Improved_Shadowcasting_in_Java
   *
   * <p>Calculates the Field Of View for the provided world from the given x, y coordinates. Returns
   * a light map for a result where the values represent a percentage of fully lit.
   *
   * <p>A value equal to or below 0means that cell is not in the field of view, whereas a value
   * equal to or above 1 means that cell is in the field of view.
   *
   * @param resistanceMap the grid of cells to calculate on where 0 is transparent and 1 is opaque
   * @param startX        the horizontal component of the starting location
   * @param startY        the vertical component of the starting location
   * @param radius        the maximum distance to draw the FOV
   * @return the computed light grid
   */
  public float[][] calculateFov(float[][] resistanceMap, int startX, int startY, float radius) {
    this.startX = startX;
    this.startY = startY;
    this.radius = radius;
    this.resistanceMap = resistanceMap;

    width = resistanceMap.length;
    height = resistanceMap[0].length;
    lightMap = new float[width][height];

    float force = 1;
    lightMap[startX][startY] = force; // light the starting cell

    for (Direction d : Direction.DIAGONALS) {
      castLight(1, 1.0f, 0.0f, 0, d.deltaX, d.deltaY, 0);
      castLight(1, 1.0f, 0.0f, d.deltaX, 0, 0, d.deltaY);
    }

    return lightMap;
  }

  private void castLight(int row, float start, float end, int xx, int xy, int yx, int yy) {
    float newStart = 0.0f;

    if (start < end) {
      return;
    }

    boolean blocked = false;

    for (int distance = row; distance <= radius && !blocked; distance++) {
      int deltaY = -distance;

      for (int deltaX = -distance; deltaX <= 0; deltaX++) {
        int currentX = startX + deltaX * xx + deltaY * xy;
        int currentY = startY + deltaX * yx + deltaY * yy;

        float leftSlope = (deltaX - 0.5f) / (deltaY + 0.5f);
        float rightSlope = (deltaX + 0.5f) / (deltaY - 0.5f);

        if (
            !(currentX >= 0 && currentY >= 0 && currentX < this.width && currentY < this.height)
                || start < rightSlope
            ) {
          continue;
        } else if (end > leftSlope) {
          break;
        }

        // Check if it's within the lightable area and light if needed
        if (radius(deltaX, deltaY) <= radius) {
          float bright = (1 - (radius(deltaX, deltaY) / radius));
          lightMap[currentX][currentY] = bright;
        }

        if (blocked) {
          // Previous cell was a blocking one

          if (resistanceMap[currentX][currentY] >= 1) {
            // Hit a wall

            newStart = rightSlope;
          } else {
            blocked = false;
            start = newStart;
          }
        } else {
          if (resistanceMap[currentX][currentY] >= 1 && distance < radius) {
            // Hit a wall within sight line
            blocked = true;
            castLight(distance + 1, start, leftSlope, xx, xy, yx, yy);
            newStart = rightSlope;
          }
        }
      }
    }
  }

  private float radius(float dx, float dy) {
    return (float) Math.sqrt(dx * dx + dy * dy);
  }

  // This was stolen from:
  //
  // https://github.com/SquidPony/SquidLib/blob/master/src/squidpony/squidgrid/util/DirectionIntercardinal.java
  private enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0),
    UP_LEFT(-1, -1), UP_RIGHT(1, -1),
    DOWN_LEFT(-1, 1), DOWN_RIGHT(1, 1),
    NONE(0, 0);

    static final Direction[] DIAGONALS = {UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};

    final int deltaX;
    final int deltaY;

    Direction(int deltaX, int deltaY) {
      this.deltaX = deltaX;
      this.deltaY = deltaY;
    }
  }
}
