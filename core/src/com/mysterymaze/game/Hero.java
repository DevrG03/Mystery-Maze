package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main character (hero) in the game.
 */
public class Hero {
    private int x;
    private int y;
    private float timeSinceLastMove = 0f;
    private List<Bomb> bombs;

    /**
     * Constructs a hero at the specified starting coordinates.
     *
     * @param startX The initial x coordinate of the hero
     * @param startY The initial y coordinate of the hero
     */
    public Hero(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.bombs = new ArrayList<>();;
    }

    /**
     * Gets the current x coordinate of the hero.
     *
     * @return The x coordinate of the hero
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the current y coordinate of the hero.
     *
     * @return The y coordinate of the hero
     */
    public int getY() {
        return y;
    }

    /**
     * Moves the hero up (decreasing y coordinate).
     */
    public void moveUp() {
        y--;
    }

    /**
     * Moves the hero down (increasing y coordinate).
     */
    public void moveDown() {
        y++;
    }

    /**
     * Moves the hero left (decreasing x coordinate).
     */
    public void moveLeft() {
        x--;
    }

    /**
     * Moves the hero right (increasing x coordinate).
     */
    public void moveRight() {
        x++;
    }

    /**
     * Handles input to move the hero based on user key presses.
     *
     * @param maze  The maze in which the hero is moving
     * @param moved Indicates whether the hero has moved
     * @return True if the hero has moved, false otherwise
     */
    public boolean handleInput(Maze maze, boolean moved) {
        this.timeSinceLastMove += Gdx.graphics.getDeltaTime();
        float movementCooldown = 0.18f;
        if (this.timeSinceLastMove >= movementCooldown) {
            // Handling movement based on key presses
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                // Check if moving up is valid (1 represents walkable path in maze)
                if (maze.getMaze()[this.getX()][this.getY() - 1] == 1) {
                    this.moveUp(); // Move up in maze coordinates
                    this.timeSinceLastMove = 0f; // Reset move cooldown
                    moved = true; // Indicate movement occurred
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
                // Check if moving down is valid
                if (maze.getMaze()[this.getX()][this.getY() + 1] == 1) {
                    this.moveDown();
                    this.timeSinceLastMove = 0f;
                    moved = true;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                // Check if moving left is valid
                if (maze.getMaze()[this.getX() - 1][this.getY()] == 1) {
                    this.moveLeft();
                    this.timeSinceLastMove = 0f;
                    moved = true;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                // Check if moving right is valid
                if (maze.getMaze()[this.getX() + 1][this.getY()] == 1) {
                    this.moveRight();
                    timeSinceLastMove = 0f;
                    moved = true;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.B) && canDropBomb()) {
                dropBomb();
                this.timeSinceLastMove = 0f; // Optional: reset cooldown to prevent immediate re-dropping
                moved = true; // Indicate movement occurred (dropping bomb is considered an action)
            }
        }
        return moved; // Return whether movement occurred
    }

    /**
     * Checks if the hero can drop a bomb (i.e., no unexploded bombs).
     *
     * @return True if a bomb can be dropped, false otherwise.
     */
    private boolean canDropBomb() {
        for (Bomb bomb : bombs) {
            if (!bomb.hasExploded()) {
                return false;
            }
        }
        return true;
    }

    private void dropBomb() {
        bombs.add(new Bomb(this.x, this.y));
        Gdx.app.log("MysteryMaze","Bomb Dropped!");
    }

    /**
     * Sets the position of the hero to the specified coordinates.
     *
     * @param x The new x coordinate of the hero
     * @param y The new y coordinate of the hero
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Iterable<Bomb> getBombs() {
        return bombs;
    }
}