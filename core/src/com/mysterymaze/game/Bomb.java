package com.mysterymaze.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bomb {
    private final int x;
    private final int y;
    private float timer;
    private boolean exploded;
    private boolean flashActive;
    private float flashTimer;
    private final int flashRadius;

    private static final float TIME_TO_EXPLODE = 3f; // 3 seconds until explosion
    private static final float FLASH_DURATION = 0.4f; // 0.2 seconds flash duration
    private static final int DEFAULT_FLASH_RADIUS = 2; // Default flash radius in cells

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.timer = 0f;
        this.exploded = false;
        this.flashActive = false;
        this.flashTimer = 0f;
        this.flashRadius = DEFAULT_FLASH_RADIUS; // Set default flash radius
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasExploded() {
        return exploded;
    }

    public void update(float deltaTime) {
        timer += deltaTime;
        if (timer >= TIME_TO_EXPLODE) {
            explode();
        }
        if (flashActive) {
            flashTimer += deltaTime;
            if (flashTimer >= FLASH_DURATION) {
                flashActive = false;
            }
        }
    }

    private void explode() {
        exploded = true;
        flashActive = true;
        flashTimer = 0f;
    }

    public void draw(SpriteBatch batch, Texture bombTexture, Texture flashTexture, float offsetX, float offsetY, int CELL_SIZE, Maze maze) {
        if (!exploded) {
            batch.draw(bombTexture, offsetX + this.getX() * CELL_SIZE, offsetY + (maze.getHeight() - this.getY() - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        } else if (flashActive) {
            // Draw the flash at the bomb's location
            batch.draw(flashTexture, offsetX + x * CELL_SIZE, offsetY + (maze.getHeight() - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            // Left direction
            for (int dx = 1; dx <= flashRadius; dx++) {
                int nx = x - dx;
                if (maze.isWithinBounds(nx, y) && !maze.isWall(nx, y)) {
                    batch.draw(flashTexture, offsetX + nx * CELL_SIZE, offsetY + (maze.getHeight() - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
            // Right direction
            for (int dx = 1; dx <= flashRadius; dx++) {
                int nx = x + dx;
                if (maze.isWithinBounds(nx, y) && !maze.isWall(nx, y)) {
                    batch.draw(flashTexture, offsetX + nx * CELL_SIZE, offsetY + (maze.getHeight() - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
            // Up direction
            for (int dy = 1; dy <= flashRadius; dy++) {
                int ny = y + dy;
                if (maze.isWithinBounds(x, ny) && !maze.isWall(x, ny)) {
                    batch.draw(flashTexture, offsetX + x * CELL_SIZE, offsetY + (maze.getHeight() - ny - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
            // Down direction
            for (int dy = 1; dy <= flashRadius; dy++) {
                int ny = y - dy;
                if (maze.isWithinBounds(x, ny) && !maze.isWall(x, ny)) {
                    batch.draw(flashTexture, offsetX + x * CELL_SIZE, offsetY + (maze.getHeight() - ny - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
        }
    }
}