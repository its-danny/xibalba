package me.dannytatom.x2600BC.generators;

import com.badlogic.gdx.math.MathUtils;
import me.dannytatom.x2600BC.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CaveGenerator {
    int width;
    int height;
    int[][] map;

    public CaveGenerator(int width, int height) {
        this.width = width;
        this.height = height;

        this.map = new int[width][height];

        for (int[] row : map) {
            Arrays.fill(row, 1);
        }
    }

    public int[][] generate() {
        initialize();

        for (int i = 0; i < 4; i++) {
            firstShapingStep();
        }

        for (int i = 0; i < 3; i++) {
            secondShapingStep();
        }

        emptyEdges();
        makeWalls();

        return map;
    }

    // Start off with all ground, then create emptiness randomly
    // (43% chance)
    private void initialize() {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (MathUtils.random() < 0.43f) {
                    map[x][y] = Constants.EMPTINESS;
                }
            }
        }
    }

    // A tile becomes empty if it's already empty and 4 or more of its nine neighbours are empty,
    // or if it is not empty and 5 or more neighbours are or the tile is in open space
    private void firstShapingStep() {
        int[][] newMap = new int[width][height];

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int neighbours1 = emptyNeighbours(1, x, y);
                int neighbours2 = emptyNeighbours(2, x, y);

                if (map[x][y] == Constants.EMPTINESS) {
                    if (neighbours1 >= 4) {
                        newMap[x][y] = Constants.EMPTINESS;
                    } else {
                        newMap[x][y] = Constants.GROUND;
                    }
                } else {
                    if (neighbours1 >= 5 || neighbours2 <= 2) {
                        newMap[x][y] = Constants.EMPTINESS;
                    } else {
                        newMap[x][y] = Constants.GROUND;
                    }
                }
            }
        }

        map = newMap;
    }

    // Same as #firstShapingStep, except we don't care about open space
    private void secondShapingStep() {
        int[][] newMap = new int[width][height];

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int neighbours = emptyNeighbours(1, x, y);

                if (map[x][y] == Constants.EMPTINESS) {
                    if (neighbours >= 4) {
                        newMap[x][y] = Constants.EMPTINESS;
                    } else {
                        newMap[x][y] = Constants.GROUND;
                    }
                } else {
                    if (neighbours >= 5) {
                        newMap[x][y] = Constants.EMPTINESS;
                    } else {
                        newMap[x][y] = Constants.GROUND;
                    }
                }
            }
        }

        map = newMap;
    }

    // Edge of the map should always be inaccessible
    private void emptyEdges() {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (x == 0 || y == 0) {
                    map[x][y] = Constants.EMPTINESS;
                }

                if (x == map.length - 1 || y == map[x].length - 1) {
                    map[x][y] = Constants.EMPTINESS;
                }
            }
        }
    }

    // If a space is empty & has at least some ground near it,
    // it's a wall. If the one before it is a front wall, it's a top wall.
    // Otherwise it's a front wall.
    private void makeWalls() {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int neighbours = groundNeighbours(x, y);

                if (map[x][y] == Constants.EMPTINESS && neighbours > 0) {
                    if (y - 1 > 0 && (map[x][y - 1] == Constants.WALL_FRONT || map[x][y - 1] == Constants.WALL_TOP)) {
                        map[x][y] = Constants.WALL_TOP;
                    } else {
                        map[x][y] = Constants.WALL_FRONT;
                    }
                }
            }
        }
    }

    // Returns number of empty neighbours around (x, y) within
    // the amount of spaces given
    private int emptyNeighbours(int amount, int x, int y) {
        int count = 0;

        for (int i = -amount; i < amount + 1; i++) {
            for (int j = -amount; j < amount + 1; j++) {
                int nx = x + i;
                int ny = y + j;

                if (i != 0 || j != 0) {
                    if (nx < 0 || ny < 0 || nx >= map.length || ny >= map[0].length) {
                        count += 1;
                    } else if (map[nx][ny] == Constants.EMPTINESS) {
                        count += 1;
                    }
                }
            }
        }

        return count;
    }

    // Returns number of ground neighbours around (x, y)
    private int groundNeighbours(int x, int y) {
        int count = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int nx = x + i;
                int ny = y + j;

                if (i != 0 || j != 0) {
                    if (nx >= 0 && ny >= 0 && nx < map.length && ny < map[0].length) {
                        if (map[nx][ny] == Constants.GROUND) {
                            count += 1;
                        }
                    }
                }
            }
        }

        return count;
    }

    // For now this just finds the first open space
    public Map<String, Integer> findPlayerStart() {
        Map<String, Integer> space = new HashMap<>();

        search:
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == Constants.GROUND) {
                    space.put("x", x);
                    space.put("y", y);

                    break search;
                }
            }
        }

        return space;
    }

    // Keep looking at random cells until you find one
    // with nothing on it
    public Map<String, Integer> findMobStart() {
        Map<String, Integer> space = new HashMap<>();
        int x;
        int y;

        do {
            x = MathUtils.random(0, map.length);
            y = MathUtils.random(0, map[x].length);
        } while (map[x][y] != Constants.GROUND);

        space.put("x", x);
        space.put("y", y);

        return space;
    }
}
