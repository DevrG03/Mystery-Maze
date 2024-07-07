package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;
import java.util.List;

public class Bomb {
    private final int x;
    private final int y;
    private float timer;
    private boolean exploded;
    private boolean flashActive;
    private float flashTimer;
    private final int flashRadius;
    private int bombScore = 0;
    private final Sound explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bomb.mp3"));

    private static final float TIME_TO_EXPLODE = 3f; // 3 seconds until explosion
    private static final float FLASH_DURATION = 0.4f; // 0.4 seconds flash duration
    private static final int DEFAULT_FLASH_RADIUS = 2; // Default flash radius in cells

    public Bomb(int x, int y) {
            this.x = x;
            this.y = y;
            this.timer = 0f;
            this.exploded = false;
            this.flashActive = false;
            this.flashTimer = 0f;
            this.flashRadius = DEFAULT_FLASH_RADIUS;// Set default flash radius
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

    public int update(float deltaTime, Hero hero, List<Enemy> enemies, Maze maze) {
        int score = 0;
        timer += deltaTime;
        if (timer >= TIME_TO_EXPLODE) {
            score = explode(hero, enemies, maze);
        }
        if (flashActive) {
            flashTimer += deltaTime;
            if (flashTimer >= FLASH_DURATION) {
                flashActive = false;
            }
        }
        return score;
    }

    private int explode(Hero hero, List<Enemy> enemies, Maze maze) {
        exploded = true;
        explosionSound.play();
        flashActive = true;
        flashTimer = 0f;
        Gdx.app.log("Bomb", "Exploding at (" + x + ", " + y + ")");

        // Log bomb position and flash radius
        Gdx.app.log("Bomb", "Bomb position: (" + x + ", " + y + "), Flash radius: " + DEFAULT_FLASH_RADIUS);

        // Check if hero is within the flash radius and no wall in between
        Gdx.app.log("Bomb", "Checking hero at position: (" + hero.getX() + ", " + hero.getY() + ")");
        if (isWithinFlashRadius(hero.getX(), hero.getY()) && isClearPath(x, y, hero.getX(), hero.getY(), maze)) {
            hero.setDead(true);
            Gdx.app.log("Bomb", "Hero is within range and has no wall in between, setting hero as dead.");
        } else {
            Gdx.app.log("Bomb", "Hero is not affected by the bomb.");
        }

        // Check if enemies are within the flash radius and no wall in between, then remove dead enemies
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            Gdx.app.log("Bomb", "Checking enemy at position: (" + enemy.getX() + ", " + enemy.getY() + ")");
            if (isWithinFlashRadius(enemy.getX(), enemy.getY()) && isClearPath(x, y, enemy.getX(), enemy.getY(), maze)) {
                enemy.setDead(true);
                bombScore += 5;
                iterator.remove(); // Remove dead enemy from the list
                Gdx.app.log("Bomb", "Enemy at (" + enemy.getX() + ", " + enemy.getY() + ") is within range and has no wall in between, removing enemy.");
            } else {
                Gdx.app.log("Bomb", "Enemy at (" + enemy.getX() + ", " + enemy.getY() + ") is not affected by the bomb.");
            }
        }

        // Check if thorns are within the flash radius and destroy them
        for (int[] thorn : maze.getThorns()) {
            Gdx.app.log("Bomb", "Checking thorn at position: (" + thorn[0] + ", " + thorn[1] + ")");
            if (isWithinFlashRadius(thorn[0], thorn[1]) && isClearPath(x, y, thorn[0], thorn[1], maze)) {
                maze.removeThorn(thorn[0], thorn[1]);
                Gdx.app.log("Bomb", "Thorn at (" + thorn[0] + ", " + thorn[1] + ") is within range and has no wall in between, destroying thorn.");
            } else {
                Gdx.app.log("Bomb", "Thorn at (" + thorn[0] + ", " + thorn[1] + ") is not affected by the bomb.");
            }
        }

        return bombScore;
    }

    private boolean isWithinFlashRadius(int targetX, int targetY) {
        int dx = Math.abs(targetX - x);
        int dy = Math.abs(targetY - y);
        boolean withinRadius = dx <= flashRadius && dy <= flashRadius;
        Gdx.app.log("Bomb", "Checking flash radius for target at (" + targetX + ", " + targetY + "): " + withinRadius);
        return withinRadius;
    }

    private boolean isClearPath(int x1, int y1, int x2, int y2, Maze maze) {
        List<int[]> points = Bresenham.line(x1, y1, x2, y2);
        for (int[] point : points) {
            if (maze.isWall(point[0], point[1])) {
                Gdx.app.log("Bomb", "Path blocked at (" + point[0] + ", " + point[1] + ")");
                return false; // There is a wall in the way
            }
        }
        Gdx.app.log("Bomb", "Path is clear from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ")");
        return true; // No walls in the way
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