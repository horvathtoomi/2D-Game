package entity.algorithm;

import main.Engine;
import main.logger.GameLogger;

import java.util.*;

public class AStar {
    private static final int DIAGONAL_COST = 14;
    private static final int V_H_COST = 10;
    private static final String LOG_CONTEXT = "[A-STAR]";

    private static class Cell {
        int heuristicCost = 0;
        int finalCost = 0;
        int i, j;
        Cell parent;

        Cell(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private static class CellComparator implements Comparator<Cell> {
        @Override
        public int compare(Cell c1, Cell c2) {
            return Integer.compare(c1.finalCost, c2.finalCost);
        }
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

        PriorityQueue<Cell> openList = new PriorityQueue<>(new CellComparator());
        boolean[][] closedList = new boolean[rows][cols];

        int startI = startY / gp.getTileSize();
        int startJ = startX / gp.getTileSize();
        int endI = endY / gp.getTileSize();
        int endJ = endX / gp.getTileSize();

        Cell start = new Cell(startI, startJ);
        Cell end = new Cell(endI, endJ);
        try {
            startI = Math.min(startI, 0);
            startJ = Math.min(startJ, 0);
            endI = Math.max(endI, 0);
            endJ = Math.max(endJ, 0);
            start = grid[startI][startJ];
            end = grid[endI][endJ];
        } catch(Exception e) {
            GameLogger.error(LOG_CONTEXT,"Cell class error: " + e.getCause(),e);
        }
        start.finalCost = 0;
        openList.add(start);

        while (!openList.isEmpty()) {
            Cell current = openList.poll();
            if (current == end) {
                return new ArrayList<>(reconstructPath(current));
            }

            closedList[current.i][current.j] = true;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;

                    int nextI = current.i + i;
                    int nextJ = current.j + j;

                    if (nextI < 0 || nextI >= rows || nextJ < 0 || nextJ >= cols) continue;
                    if (closedList[nextI][nextJ]) continue;
                    if (gp.tileman.tile[gp.tileman.mapTileNum[nextJ][nextI]].collision) continue;

                    Cell neighbor = grid[nextI][nextJ];
                    int newCost = current.finalCost + ((i == 0 || j == 0) ? V_H_COST : DIAGONAL_COST);

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

        return null; // Path not found
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