package com.mysterymaze.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bomb {
    private int x;
    private int y;
    private float timer;
    private boolean exploded;

    private static final float TIME_TO_EXPLODE = 3f; // 3 seconds until explosion

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        this.timer = 0f;
        this.exploded = false;
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
    }

    private void explode() {
        exploded = true;
        // Handle explosion logic
    }

    public void draw(SpriteBatch batch, Texture bombTexture, float offsetX, float offsetY, int CELL_SIZE, Maze maze) {
        if (!exploded) {
            batch.draw(bombTexture,  offsetX + this.getX() * CELL_SIZE, offsetY + (maze.getHeight() - this.getY() - 1) * CELL_SIZE , CELL_SIZE, CELL_SIZE);
        }
    }
}