package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a bomb in the game.
 */
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
    private final Sound deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/herodeath.mp3"));
    private static final float TIME_TO_EXPLODE = 3f; // 3 seconds until explosion
    private static final float FLASH_DURATION = 0.4f; // 0.4 seconds flash duration
    private static final int DEFAULT_FLASH_RADIUS = 2; // Default flash radius in cells

    /**
     * Creates a new bomb at the specified position.
     * 
     * @param x the x-coordinate of the bomb
     * @param y the y-coordinate of the bomb
     */
    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.timer = 0f;
        this.exploded = false;
        this.flashActive = false;
        this.flashTimer = 0f;
        this.flashRadius = DEFAULT_FLASH_RADIUS; // Set default flash radius
    }

    /**
     * Gets the x-coordinate of the bomb.
     * 
     * @return the x-coordinate of the bomb
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the bomb.
     * 
     * @return the y-coordinate of the bomb
     */
    public int getY() {
        return y;
    }

    /**
     * Checks if the bomb has exploded.
     * 
     * @return true if the bomb has exploded, false otherwise
     */
    public boolean hasExploded() {
        return exploded;
    }

    /**
     * Updates the bomb's state and checks for collisions with the hero and enemies.
     * 
     * @param deltaTime the time since the last update
     * @param hero      the hero object
     * @param enemies   the list of enemy objects
     * @param maze      the maze object
     * @return the score earned from the explosion
     */
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

    /**
     * Explodes the bomb and checks for collisions with the hero and enemies.
     * 
     * @param hero    the hero object
     * @param enemies the list of enemy objects
     * @param maze    the maze object
     * @return the score earned from the explosion
     */
    private int explode(Hero hero, List<Enemy> enemies, Maze maze) {
        exploded = true;
        explosionSound.play();
        flashActive = true;
        flashTimer = 0f;
        if (isWithinFlashRadius(hero.getX(), hero.getY()) && isClearPath(x, y, hero.getX(), hero.getY(), maze)) {
            hero.setDead(true);
            deathSound.play();
        }

        // Check if enemies are within the flash radius and no wall in between, then
        // remove dead enemies
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (isWithinFlashRadius(enemy.getX(), enemy.getY())
                    && isClearPath(x, y, enemy.getX(), enemy.getY(), maze)) {
                enemy.setDead(true);
                bombScore += 5;
                iterator.remove(); // Remove dead enemy from the list
            }
        }

        // Check if thorns are within the flash radius and destroy them
        for (int[] thorn : maze.getThorns()) {
            if (isWithinFlashRadius(thorn[0], thorn[1]) && isClearPath(x, y, thorn[0], thorn[1], maze)) {
                maze.removeThorn(thorn[0], thorn[1]);
            }
        }

        return bombScore;
    }

    /**
     * Checks if a target is within the flash radius of the bomb.
     * 
     * @param targetX the x-coordinate of the target
     * @param targetY the y-coordinate of the target
     * @return true if the target is within the flash radius, false otherwise
     */
    private boolean isWithinFlashRadius(int targetX, int targetY) {
        int dx = Math.abs(targetX - x);
        int dy = Math.abs(targetY - y);
        boolean withinRadius = dx <= flashRadius && dy <= flashRadius;
        return withinRadius;
    }

    /**
     * Checks if there is a clear path between two points in the maze.
     * 
     * @param x1   the x-coordinate of the first point
     * @param y1   the y-coordinate of the first point
     * @param x2   the x-coordinate of the second point
     * @param y2   the y-coordinate of the second point
     * @param maze the maze object
     * @return true if there is a clear path, false otherwise
     */
    private boolean isClearPath(int x1, int y1, int x2, int y2, Maze maze) {
        List<int[]> points = Bresenham.line(x1, y1, x2, y2);
        for (int[] point : points) {
            if (maze.isWall(point[0], point[1])) {
                return false; // There is a wall in the way
            }
        }
        return true; // No walls in the way
    }

    /**
     * Draws the bomb or its flash effect on the screen.
     * 
     * @param batch        the sprite batch
     * @param bombTexture  the texture for the bomb
     * @param flashTexture the texture for the flash effect
     * @param offsetX      the x-coordinate offset
     * @param offsetY      the y-coordinate offset
     * @param CELL_SIZE    the size of a cell in the maze
     * @param maze         the maze object
     */
    public void draw(SpriteBatch batch, Texture bombTexture, Texture flashTexture, float offsetX, float offsetY,
            int CELL_SIZE, Maze maze) {
        if (!exploded) {
            batch.draw(bombTexture, offsetX + this.getX() * CELL_SIZE,
                    offsetY + (maze.getHeight() - this.getY() - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        } else if (flashActive) {
            // Draw the flash at the bomb's location
            batch.draw(flashTexture, offsetX + x * CELL_SIZE, offsetY + (maze.getHeight() - y - 1) * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE);

            // Left direction
            for (int dx = 1; dx <= flashRadius; dx++) {
                int nx = x - dx;
                if (maze.isWithinBounds(nx, y) && !maze.isWall(nx, y)) {
                    batch.draw(flashTexture, offsetX + nx * CELL_SIZE, offsetY + (maze.getHeight() - y - 1) * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
            // Right direction
            for (int dx = 1; dx <= flashRadius; dx++) {
                int nx = x + dx;
                if (maze.isWithinBounds(nx, y) && !maze.isWall(nx, y)) {
                    batch.draw(flashTexture, offsetX + nx * CELL_SIZE, offsetY + (maze.getHeight() - y - 1) * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
            // Up direction
            for (int dy = 1; dy <= flashRadius; dy++) {
                int ny = y + dy;
                if (maze.isWithinBounds(x, ny) && !maze.isWall(x, ny)) {
                    batch.draw(flashTexture, offsetX + x * CELL_SIZE, offsetY + (maze.getHeight() - ny - 1) * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
            // Down direction
            for (int dy = 1; dy <= flashRadius; dy++) {
                int ny = y - dy;
                if (maze.isWithinBounds(x, ny) && !maze.isWall(x, ny)) {
                    batch.draw(flashTexture, offsetX + x * CELL_SIZE, offsetY + (maze.getHeight() - ny - 1) * CELL_SIZE,
                            CELL_SIZE, CELL_SIZE);
                } else {
                    break;
                }
            }
        }
    }
}