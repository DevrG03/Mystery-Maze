package com.mysterymaze.game;

import java.util.List;

/**
 * Represents an enemy in the game that patrols and may chase the hero.
 */
public class Enemy {
    private int x;  // Current x-coordinate of the enemy
    private int y;  // Current y-coordinate of the enemy
    private final List<int[]> patrolPath;  // List of coordinates for the enemy's patrol path
    private int pathIndex;  // Index of the current position in the patrol path
    private boolean movingForward;  // Flag indicating direction of patrol

    private float timeSinceLastMove = 0f;  // Time elapsed since the last movement
    private boolean isChasing = false;  // Flag indicating if the enemy is currently chasing the hero

    private final Hero hero;  // Reference to the hero character
    private final Pathfinding pathfinding;  // Object for pathfinding operations
    private List<int[]> currentPath;  // Current path for the enemy's movement
    private int pathStep;  // Index of the current step in the path
    private List<int[]> returnPath;  // Path to return to patrol after chasing
    private boolean dead;

    /**
     * Constructor to initialize an enemy with a patrol path, hero reference, and maze for pathfinding.
     *
     * @param x          Initial x-coordinate
     * @param y          Initial y-coordinate
     * @param patrolPath List of coordinates defining the patrol path
     * @param hero       Reference to the hero character
     * @param maze       Maze object for pathfinding and obstruction checks
     */
    public Enemy(int x, int y, List<int[]> patrolPath, Hero hero, Maze maze) {
        this.x = x;
        this.y = y;
        this.patrolPath = patrolPath;
        this.pathIndex = 0;
        this.movingForward = true;
        this.hero = hero;
        this.pathfinding = new Pathfinding(maze);
        this.currentPath = null;
        this.pathStep = 0;
        this.returnPath = null;
        this.dead = false;
    }

    /**
     * Get the current x-coordinate of the enemy.
     *
     * @return Current x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Get the current y-coordinate of the enemy.
     *
     * @return Current y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Update method called to process enemy behavior based on time and maze conditions.
     *
     * @param deltaTime Time elapsed since the last update
     * @param maze      Maze object for pathfinding and obstruction checks
     */
    public void update(float deltaTime, Maze maze) {
        timeSinceLastMove += deltaTime;
        float movementCooldown = 0.9f;  // Adjust this value to control the speed of enemy movement

        // Check if enough time has passed to allow movement
        if (timeSinceLastMove >= movementCooldown) {
            // Determine whether to chase the hero, revert to patrol, or continue patrolling
            if (isHeroVisible(maze)) {
                chaseHero();
            } else {
                if (isChasing) {
                    // Hero went out of range or is obstructed, reset path and revert to patrol
                    resetPath();
                    revertToPatrol(maze);
                } else {
                    patrol(maze);
                }
            }
            timeSinceLastMove = 0f;  // Reset movement cooldown
        }
    }

    /**
     * Check if the hero is within the chasing range of the enemy.
     *
     * @return True if hero is within chasing range, false otherwise
     */
    private boolean isHeroInRange() {
        float distance = (float) Math.sqrt(Math.pow(hero.getX() - x, 2) + Math.pow(hero.getY() - y, 2));
        // Range within which the enemy will chase the hero
        float chaseRange = 5f;
        return distance <= chaseRange;
    }

    /**
     * Check if the hero is visible to the enemy (line of sight and no obstacles).
     *
     * @param maze Maze object for obstruction checks
     * @return True if hero is visible and within range, false otherwise
     */
    private boolean isHeroVisible(Maze maze) {
        if (isHeroInRange()) {
            List<int[]> lineOfSight = Bresenham.line(x, y, hero.getX(), hero.getY());
            for (int[] pos : lineOfSight) {
                if (!maze.isFree(pos[0], pos[1])) {
                    return false; // Obstacle in the way
                }
            }
            return true; // No obstacles in the way
        }
        return false; // Hero out of range
    }

    /**
     * Move towards the hero's position to chase.
     */
    private void chaseHero() {
        isChasing = true;
        if (currentPath == null || pathStep >= currentPath.size()) {
            currentPath = pathfinding.findPath(x, y, hero.getX(), hero.getY());
            pathStep = 0;
        }
        if (currentPath != null && pathStep < currentPath.size()) {
            int[] nextStep = currentPath.get(pathStep);
            x = nextStep[0];
            y = nextStep[1];
            pathStep++;
        }
    }

    /**
     * Reset the current path and return path variables.
     */
    private void resetPath() {
        currentPath = null;
        pathStep = 0;
        returnPath = null;
    }

    /**
     * Return to the patrol path after chasing the hero.
     */
    private void revertToPatrol(Maze maze) {
        isChasing = false;
        if (returnPath == null) {
            int[] patrolStart = patrolPath.get(0);
            returnPath = pathfinding.findPath(x, y, patrolStart[0], patrolStart[1]);
            pathStep = 0;
        }
        if (returnPath != null && pathStep < returnPath.size()) {
            int[] nextStep = returnPath.get(pathStep);
            x = nextStep[0];
            y = nextStep[1];
            pathStep++;
        } else {
            patrol(maze);
            resetPath(); // Reached the start of the patrol path, reset to patrol mode
        }
    }

    /**
     * Patrol along the predefined path.
     */
    private void patrol(Maze maze) {
        if (patrolPath.isEmpty()) {
            return; // No patrol path defined
        }

        int nextIndex = movingForward ? pathIndex + 1 : pathIndex - 1;

        if (nextIndex >= 0 && nextIndex < patrolPath.size()) {
            int[] nextPosition = patrolPath.get(nextIndex);
            int nextX = nextPosition[0];
            int nextY = nextPosition[1];

            // Check if the next position contains thorns
            if (!containsThorns(nextX, nextY, maze)) {
                pathIndex = nextIndex;
                x = nextX;
                y = nextY;
            } else {
                // Reverse direction if the next position contains thorns
                movingForward = !movingForward;
            }
        } else {
            // Reverse direction if we reach the end of the patrol path
            movingForward = !movingForward;
        }
    }

    /**
     * Checks if a given position contains thorns.
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @return true if the position contains thorns, false otherwise
     */
    private boolean containsThorns(int x, int y, Maze maze) {
        for (int[] thorn : maze.getThorns()) {
            if (thorn[0] == x && thorn[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }
}