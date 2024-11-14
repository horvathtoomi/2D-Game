package entity.algorithm;

import entity.Entity;
import main.Engine;
import main.logger.GameLogger;
import tile.TileManager;

import java.util.*;

public class AStar {
    private static final int DIAGONAL_COST = 14;
    private static final int V_H_COST = 10;
    private static final String LOG_CONTEXT = "[A-STAR]";
    private static final double ENTITY_AVOIDANCE_WEIGHT = 3.0;
    private static final int ENTITY_INFLUENCE_RADIUS = 3;

    private static class Cell {
        int heuristicCost = 0;
        double finalCost = 0;
        int i, j;
        Cell parent;

        Cell(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return i == cell.i && j == cell.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }
    }

    private static class CellComparator implements Comparator<Cell> {
        @Override
        public int compare(Cell c1, Cell c2) {
            return Double.compare(c1.finalCost, c2.finalCost);
        }
    }

    private static Map<Cell, Double> calculateDynamicCosts(Engine gp, int rows, int cols) {
        Map<Cell, Double> dynamicCosts = new HashMap<>();

        for (Entity entity : gp.getEntity()) {
            int entityI = entity.getWorldY() / gp.getTileSize();
            int entityJ = entity.getWorldX() / gp.getTileSize();

            for (int i = Math.max(0, entityI - ENTITY_INFLUENCE_RADIUS);
                 i < Math.min(rows, entityI + ENTITY_INFLUENCE_RADIUS); i++) {
                for (int j = Math.max(0, entityJ - ENTITY_INFLUENCE_RADIUS);
                     j < Math.min(cols, entityJ + ENTITY_INFLUENCE_RADIUS); j++) {

                    Cell cell = new Cell(i, j);
                    double distance = Math.sqrt(Math.pow(i - entityI, 2) + Math.pow(j - entityJ, 2));

                    if (distance <= ENTITY_INFLUENCE_RADIUS) {
                        double cost = ENTITY_AVOIDANCE_WEIGHT * (ENTITY_INFLUENCE_RADIUS - distance + 1);
                        dynamicCosts.merge(cell, cost, Double::sum);
                    }
                }
            }
        }

        return dynamicCosts;
    }

    public static ArrayList<int[]> findPath(Engine gp, int startX, int startY, int endX, int endY) {
        int rows = gp.getMaxWorldRow();
        int cols = gp.getMaxWorldCol();

        Cell[][] grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

        Map<Cell, Double> dynamicCosts = calculateDynamicCosts(gp, rows, cols);
        PriorityQueue<Cell> openList = new PriorityQueue<>(new CellComparator());
        boolean[][] closedList = new boolean[rows][cols];

        int startI = startY / gp.getTileSize();
        int startJ = startX / gp.getTileSize();
        int endI = endY / gp.getTileSize();
        int endJ = endX / gp.getTileSize();

        try {
            startI = Math.max(Math.min(startI, rows - 1), 0);
            startJ = Math.max(Math.min(startJ, cols - 1), 0);
            endI = Math.max(Math.min(endI, rows - 1), 0);
            endJ = Math.max(Math.min(endJ, cols - 1), 0);
        } catch(Exception e) {
            GameLogger.error(LOG_CONTEXT, "Invalid coordinates: " + e.getMessage(), e);
            return null;
        }

        Cell start = grid[startI][startJ];
        Cell end = grid[endI][endJ];

        start.finalCost = 0;
        openList.add(start);

        while (!openList.isEmpty()) {
            Cell current = openList.poll();

            if (current.equals(end)) {
                return reconstructPath(current);
            }

            closedList[current.i][current.j] = true;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;

                    int nextI = current.i + i;
                    int nextJ = current.j + j;

                    if (nextI < 0 || nextI >= rows || nextJ < 0 || nextJ >= cols) continue;
                    if (closedList[nextI][nextJ]) continue;
                    if (gp.tileman.tile[TileManager.mapTileNum[nextJ][nextI]].collision) continue;

                    Cell neighbor = grid[nextI][nextJ];
                    double movementCost = (i == 0 || j == 0) ? V_H_COST : DIAGONAL_COST;

                    movementCost += dynamicCosts.getOrDefault(neighbor, 0.0);

                    double newCost = current.finalCost + movementCost;

                    if (!openList.contains(neighbor) || newCost < neighbor.finalCost) {
                        neighbor.heuristicCost = Math.abs(neighbor.i - endI) + Math.abs(neighbor.j - endJ);
                        neighbor.finalCost = newCost + neighbor.heuristicCost;
                        neighbor.parent = current;

                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                        }
                    }
                }
            }
        }

        return null;
    }

    private static ArrayList<int[]> reconstructPath(Cell end) {
        ArrayList<int[]> path = new ArrayList<>();
        Cell current = end;

        while (current != null) {
            path.add(new int[]{current.j, current.i});
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }
}