package com.mysterymaze.game;

import java.util.List;

public class Enemy {
    private int x;
    private int y;
    private List<int[]> patrolPath;
    private int pathIndex;
    private boolean movingForward;

    private float movementCooldown = 0.5f; // Adjust this value to control the speed
    private float timeSinceLastMove = 0f;

    public Enemy(int x, int y, List<int[]> patrolPath) {
        this.x = x;
        this.y = y;
        this.patrolPath = patrolPath;
        this.pathIndex = 0;
        this.movingForward = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void update(float deltaTime) {
        timeSinceLastMove += deltaTime;
        if (timeSinceLastMove >= movementCooldown) {
            move();
            timeSinceLastMove = 0f;
        }
    }

    private void move() {
        if (movingForward) {
            pathIndex++;
            if (pathIndex >= patrolPath.size()) {
                pathIndex = patrolPath.size() - 1; // Stay at the end of the path
                movingForward = false;
            }
        } else {
            pathIndex--;
            if (pathIndex < 0) {
                pathIndex = 0; // Stay at the start of the path
                movingForward = true;
            }
        }
        x = patrolPath.get(pathIndex)[0];
        y = patrolPath.get(pathIndex)[1];
    }
}