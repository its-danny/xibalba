package me.dannytatom.xibalba.world.generators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

import me.dannytatom.xibalba.world.MapCell;

public class CaveGenerator {
  private final int width;
  private final int height;
  public MapCell.Type[][] geometry;
  private MapCell.Type[][] flooded;

  /**
   * Generates a cave.
   *
   * @param width  How wide the world should be in cells
   * @param height How long the world should be in cells
   */
  public CaveGenerator(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Starts the cave generation.
   */
  public void generate() {
    initialize();

    geometry = blank();

    float numberOfSteps = 5;

    for (int i = 0; i < numberOfSteps; i++) {
      geometry = step();
    }

    emptyGeometryEdges();
    maybeTryAgain();
  }

  private void initialize() {
    geometry = new MapCell.Type[width][height];

    for (MapCell.Type[] row : geometry) {
      Arrays.fill(row, MapCell.Type.WALL);
    }

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        float chanceToStartAlive = 0.4f;
        if (MathUtils.random() < chanceToStartAlive) {
          geometry[x][y] = MapCell.Type.FLOOR;
        }
      }
    }
  }

  private MapCell.Type[][] step() {
    MapCell.Type[][] newGeo = geometry.clone();

    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[0].length; y++) {
        int neighbours = countLivingNeighbours(x, y);

        if (geometry[x][y] == MapCell.Type.FLOOR) {
          float deathLimit = 3;
          newGeo[x][y] = neighbours >= deathLimit ? MapCell.Type.FLOOR : MapCell.Type.WALL;
        } else {
          float birthLimit = 4;
          newGeo[x][y] = neighbours > birthLimit ? MapCell.Type.FLOOR : MapCell.Type.WALL;
        }
      }
    }

    return newGeo;
  }

  private MapCell.Type[][] blank() {
    MapCell.Type[][] newGeo = geometry.clone();

    int rows = 2;
    int start = MathUtils.round(geometry[0].length / 2) - rows;

    for (int x = 0; x < geometry.length; x++) {
      for (int y = start; y < start + (rows - 1); y++) {
        newGeo[x][y] = MapCell.Type.WALL;
      }
    }

    return newGeo;
  }

  /**
   * Edge of the geometry should always be inaccessible.
   */
  private void emptyGeometryEdges() {
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[x].length; y++) {
        if (x == 0 || y == 0 || x == 1 || y == 1) {
          geometry[x][y] = MapCell.Type.WALL;
        }

        if (x == geometry.length - 1 || y == geometry[x].length - 1
            || x == geometry.length - 2 || y == geometry[x].length - 2) {
          geometry[x][y] = MapCell.Type.WALL;
        }
      }
    }
  }

  private void maybeTryAgain() {
    flooded = new MapCell.Type[width][height];

    for (MapCell.Type[] row : flooded) {
      Arrays.fill(row, MapCell.Type.WALL);
    }

    search:
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[0].length; y++) {
        if (geometry[x][y] == MapCell.Type.FLOOR) {
          floodFill(x, y);
          break search;
        }
      }
    }

    geometry = flooded;

    int openCount = 0;

    //noinspection ForLoopReplaceableByForEach
    for (int x = 0; x < geometry.length; x++) {
      for (int y = 0; y < geometry[0].length; y++) {
        if (geometry[x][y] == MapCell.Type.FLOOR) {
          openCount += 1;
        }
      }
    }

    if (openCount < (width * height) / 6) {
      generate();
    } else {
      Gdx.app.log("CaveGenerator", "Cave with " + openCount + " tiles open");
    }
  }

  private void floodFill(int cellX, int cellY) {
    if (geometry[cellX][cellY] == MapCell.Type.FLOOR
        && flooded[cellX][cellY] == MapCell.Type.WALL) {
      flooded[cellX][cellY] = MapCell.Type.FLOOR;
    } else {
      return;
    }

    floodFill(cellX + 1, cellY);
    floodFill(cellX - 1, cellY);
    floodFill(cellX, cellY + 1);
    floodFill(cellX, cellY - 1);
  }

  private int countLivingNeighbours(int cellX, int cellY) {
    int count = 0;

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        int neighbourX = cellX + i;
        int neighbourY = cellY + j;

        if (i != 0 || j != 0) {
          if (neighbourX < 0 || neighbourY < 0
              || neighbourX >= geometry.length || neighbourY >= geometry[0].length) {
            count += 1;
          } else if (geometry[neighbourX][neighbourY] == MapCell.Type.FLOOR) {
            count += 1;
          }
        }
      }
    }

    return count;
  }
}
