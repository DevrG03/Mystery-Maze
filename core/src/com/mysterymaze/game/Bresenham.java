package com.mysterymaze.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a static method to generate a line of points using Bresenham's line algorithm.
 */
public class Bresenham {

    /**
     * Generates a list of points representing a line from (x0, y0) to (x1, y1) using Bresenham's line algorithm.
     *
     * @param x0 Starting x-coordinate
     * @param y0 Starting y-coordinate
     * @param x1 Ending x-coordinate
     * @param y1 Ending y-coordinate
     * @return List of integer arrays where each array contains x and y coordinates of a point on the line
     */
    public static List<int[]> line(int x0, int y0, int x1, int y1) {
        List<int[]> points = new ArrayList<>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            points.add(new int[]{x0, y0});
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }

        return points;
    }
}