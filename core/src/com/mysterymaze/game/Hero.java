package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Hero {
    private int x;
    private int y;
    private float movementCooldown = 0.15f;
    private float timeSinceLastMove = 0f;

    public Hero(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveUp() {
        y++;
    }

    public void moveDown() {
        y--;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public boolean handleInput(Maze maze, boolean moved) {
        this.timeSinceLastMove += Gdx.graphics.getDeltaTime();
        if (this.timeSinceLastMove >= this.movementCooldown) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                if (maze.getMaze()[this.getX()][this.getY() - 1] == 1) {
                    this.moveDown();
                    this.timeSinceLastMove = 0f;
                    moved = true;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (maze.getMaze()[this.getX()][this.getY() + 1] == 1) {
                    this.moveUp();
                    this.timeSinceLastMove = 0f;
                    moved = true;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                if (maze.getMaze()[this.getX() - 1][this.getY()] == 1) {
                    this.moveLeft();
                    this.timeSinceLastMove = 0f;
                    moved = true;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (maze.getMaze()[this.getX() + 1][this.getY()] == 1) {
                    this.moveRight();
                    timeSinceLastMove = 0f;
                    moved = true;
                }
            }
        }
        return moved;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}