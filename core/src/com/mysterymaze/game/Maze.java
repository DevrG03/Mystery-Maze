package com.mysterymaze.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Maze {
    private final int width;
    private final int height;
    private final int[][] maze;
    private final Random random = new Random();

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new int[width][height];
        for (int[] row : maze) {
            Arrays.fill(row, 0);
        }
        generateMaze(1, 1);
    }

    private void generateMaze(int x, int y) {
        Direction[] directions = Direction.values();
        Collections.shuffle(Arrays.asList(directions), random);

        for (Direction direction : directions) {
            int newX = x + direction.dx * 2;
            int newY = y + direction.dy * 2;

            if (isValid(newX, newY)) {
                maze[x + direction.dx][y + direction.dy] = 1;
                maze[newX][newY] = 1;
                generateMaze(newX, newY);
            }
        }
    }

    private boolean isValid(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1 && maze[x][y] == 0;
    }

    public int[][] getMaze() {
        return maze;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasFloorBelow(int x, int y) {
        return ((y + 1 < height - 1) &&(maze[x][y + 1] == 1)) || y == height - 1;
    }

    private enum Direction {
        NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0);

        private final int dx;
        private final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public void drawMaze(SpriteBatch batch, int offsetX, int offsetY, int CELL_SIZE, Texture wallTexture1, Texture wallTexture2, Texture floorTexture) {
        int[][] mazeArray = this.getMaze();
        for (int y = 0; y < mazeArray.length; y++) {
            for (int x = 0; x < mazeArray[y].length; x++) {
                int y1 = offsetY + (mazeArray.length - y - 1) * CELL_SIZE, x1 = offsetX + x * CELL_SIZE;
                if (mazeArray[x][y] == 0) {  // Draw wall
                    if (this.hasFloorBelow(x, y)) {
                        batch.draw(wallTexture2, x1, y1, CELL_SIZE, CELL_SIZE);
                    } else {
                        batch.draw(wallTexture1, x1, y1, CELL_SIZE, CELL_SIZE);
                        // Draw a different wall texture here for no wall below
                        // Example: batch.draw(differentWallTexture, x * CELL_SIZE, (mazeArray.length - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                } else {  // Draw path
                    batch.draw(floorTexture, x1, y1, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}