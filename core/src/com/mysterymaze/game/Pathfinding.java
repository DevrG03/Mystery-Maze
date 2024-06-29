package com.mysterymaze.game;

import java.util.*;

/**
 * A* pathfinding algorithm implementation for finding paths in a maze.
 */
public class Pathfinding {
    private final Maze maze;

    /**
     * Constructs a pathfinding instance for the specified maze.
     *
     * @param maze The maze in which to find paths
     */
    public Pathfinding(Maze maze) {
        this.maze = maze;
    }

    /**
     * Finds a path from the starting coordinates to the goal coordinates using A* algorithm.
     *
     * @param startX The x coordinate of the starting position
     * @param startY The y coordinate of the starting position
     * @param goalX  The x coordinate of the goal position
     * @param goalY  The y coordinate of the goal position
     * @return A list of coordinates representing the path from start to goal, or an empty list if no path is found
     */
    public List<int[]> findPath(int startX, int startY, int goalX, int goalY) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<String, Node> allNodes = new HashMap<>();

        Node startNode = new Node(startX, startY, null, 0, getHeuristic(startX, startY, goalX, goalY));
        openList.add(startNode);
        allNodes.put(startNode.key, startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.x == goalX && current.y == goalY) {
                return buildPath(current);
            }

            for (int[] neighbor : getNeighbors(current.x, current.y)) {
                int neighborX = neighbor[0];
                int neighborY = neighbor[1];
                double tentativeG = current.g + 1;

                Node neighborNode = allNodes.get(Node.getKey(neighborX, neighborY));
                if (neighborNode == null) {
                    neighborNode = new Node(neighborX, neighborY, current, tentativeG, getHeuristic(neighborX, neighborY, goalX, goalY));
                    openList.add(neighborNode);
                    allNodes.put(neighborNode.key, neighborNode);
                } else if (tentativeG < neighborNode.g) {
                    neighborNode.parent = current;
                    neighborNode.g = tentativeG;
                    neighborNode.f = tentativeG + neighborNode.h;
                    openList.add(neighborNode);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    /**
     * Constructs the path by traversing backwards from the goal node to the start node.
     *
     * @param node The goal node from which to build the path
     * @return A list of coordinates representing the path from start to goal
     */
    private List<int[]> buildPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new int[]{node.x, node.y});
            node = node.parent;
        }
        return path;
    }

    /**
     * Retrieves the list of neighboring coordinates that are accessible in the maze.
     *
     * @param x The x coordinate of the current position
     * @param y The y coordinate of the current position
     * @return A list of neighboring coordinates
     */
    private List<int[]> getNeighbors(int x, int y) {
        List<int[]> neighbors = new ArrayList<>();
        if (maze.isFree(x + 1, y)) neighbors.add(new int[]{x + 1, y});
        if (maze.isFree(x - 1, y)) neighbors.add(new int[]{x - 1, y});
        if (maze.isFree(x, y + 1)) neighbors.add(new int[]{x, y + 1});
        if (maze.isFree(x, y - 1)) neighbors.add(new int[]{x, y - 1});
        return neighbors;
    }

    /**
     * Calculates the Manhattan distance heuristic between two points.
     *
     * @param x1 The x coordinate of the first point
     * @param y1 The y coordinate of the first point
     * @param x2 The x coordinate of the second point
     * @param y2 The y coordinate of the second point
     * @return The Manhattan distance between the two points
     */
    private double getHeuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Represents a node in the A* pathfinding algorithm.
     */
    private static class Node {
        int x, y;
        Node parent;
        double g, h, f;
        String key;

        /**
         * Constructs a node with the specified properties.
         *
         * @param x      The x coordinate of the node
         * @param y      The y coordinate of the node
         * @param parent The parent node
         * @param g      The cost from the start node to this node
         * @param h      The heuristic value from this node to the goal node
         */
        Node(int x, int y, Node parent, double g, double h) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.key = getKey(x, y);
        }

        /**
         * Generates a unique key for the node based on its coordinates.
         *
         * @param x The x coordinate of the node
         * @param y The y coordinate of the node
         * @return A unique key string for the node
         */
        static String getKey(int x, int y) {
            return x + "," + y;
        }
    }
}