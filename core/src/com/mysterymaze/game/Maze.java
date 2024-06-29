package com.mysterymaze.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Represents a maze generated using Depth-First Search (DFS) algorithm.
 * Provides methods to generate the maze, check positions, and draw the maze using textures.
 */
public class Maze {
    private final int width;
    private final int height;
    private final int[][] maze;
    private final Random random = new Random();

    /**
     * Constructs a maze with the specified dimensions and initializes it with walls.
     *
     * @param width  The width of the maze
     * @param height The height of the maze
     */
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new int[width][height];
        // Initialize maze with all walls (0 represents walls)
        for (int[] row : maze) {
            Arrays.fill(row, 0);
        }
        generateMaze(1, 1); // Generate the maze starting from (1, 1)
    }

    /**
     * Recursive method to generate the maze using Depth-First Search (DFS).
     *
     * @param x The current x coordinate during generation
     * @param y The current y coordinate during generation
     */
    private void generateMaze(int x, int y) {
        Direction[] directions = Direction.values();
        Collections.shuffle(Arrays.asList(directions), random);

        for (Direction direction : directions) {
            int newX = x + direction.dx * 2;
            int newY = y + direction.dy * 2;

            if (isValid(newX, newY)) {
                // Carve a path
                maze[x + direction.dx][y + direction.dy] = 1;
                maze[newX][newY] = 1;
                generateMaze(newX, newY); // Recursively generate from the new position
            }
        }
    }

    /**
     * Checks if a position is within bounds and not yet carved out.
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @return true if the position is valid (within bounds and not carved out), false otherwise
     */
    private boolean isValid(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1 && maze[x][y] == 0;
    }

    /**
     * Retrieves the maze as a 2D array.
     *
     * @return The maze represented as a 2D integer array
     */
    public int[][] getMaze() {
        return maze;
    }

    /**
     * Retrieves the width of the maze.
     *
     * @return The width of the maze
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the maze.
     *
     * @return The height of the maze
     */
    public int getHeight() {
        return height;
    }

    /**
     * Checks if there is a floor below the specified position.
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @return true if there is a floor below the position, false otherwise
     */
    public boolean hasFloorBelow(int x, int y) {
        return ((y + 1 < height - 1) && (maze[x][y + 1] == 1)) || y == height - 1;
    }

    /**
     * Directions used for maze generation (NORTH, SOUTH, EAST, WEST).
     */
    private enum Direction {
        NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0);

        private final int dx;
        private final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    /**
     * Checks if a position is free (not a wall).
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @return true if the position is free (not a wall), false otherwise
     */
    public boolean isFree(int x, int y) {
        if (x < 0 || y < 0 || x >= maze.length || y >= maze[0].length) {
            return false; // Out of bounds
        }
        return maze[x][y] == 1; // 1 represents a path (free space)
    }

    /**
     * Draws the maze using textures for walls and floor.
     *
     * @param batch     The SpriteBatch used for drawing
     * @param offsetX   The x offset to start drawing from
     * @param offsetY   The y offset to start drawing from
     * @param CELL_SIZE The size of each cell in pixels
     * @param wallTexture1 The texture for walls
     * @param wallTexture2 The texture for walls where there is a floor below
     * @param floorTexture The texture for the floor (path)
     */
    public void drawMaze(SpriteBatch batch, int offsetX, int offsetY, int CELL_SIZE,
                         Texture wallTexture1, Texture wallTexture2, Texture floorTexture) {
        int[][] mazeArray = this.getMaze();
        for (int y = 0; y < mazeArray.length; y++) {
            for (int x = 0; x < mazeArray[y].length; x++) {
                int y1 = offsetY + (mazeArray.length - y - 1) * CELL_SIZE, x1 = offsetX + x * CELL_SIZE;
                if (mazeArray[x][y] == 0) {  // Draw wall
                    if (this.hasFloorBelow(x, y)) {
                        batch.draw(wallTexture2, x1, y1, CELL_SIZE, CELL_SIZE);
                    } else {
                        batch.draw(wallTexture1, x1, y1, CELL_SIZE, CELL_SIZE);
                        // Example of drawing a different wall texture when there's no wall below:
                        // batch.draw(differentWallTexture, x1, y1, CELL_SIZE, CELL_SIZE);
                    }
                } else {  // Draw floor (path)
                    batch.draw(floorTexture, x1, y1, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}