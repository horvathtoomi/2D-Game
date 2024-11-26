package entity.algorithm;

import entity.Entity;
import main.Engine;
import main.logger.GameLogger;
import tile.TileManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Az A* útvonalkereső algoritmus implementációja.
 * Ez az osztály biztosítja az entitások számára az optimális útvonal megtalálását
 * a játékban figyelembe véve a terep akadályait és más entitásokat.
 */
public class AStar {
    private static final int DIAGONAL_COST = 14;
    private static final int V_H_COST = 10;
    private static final String LOG_CONTEXT = "[A-STAR]";
    private static final double ENTITY_AVOIDANCE_WEIGHT = 3.0;
    private static final int ENTITY_INFLUENCE_RADIUS = 3;

    private static final Map<PathKey, PathCacheEntry> pathCache = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 5000; // 5 seconds cache duration


    /**
     * Egy cella reprezentációja az A* algoritmus számára.
     * Tartalmazza a cella pozícióját és az útvonalkereséshez szükséges költségértékeket.
     */
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

    private static class PathKey {
        final int startX, startY, endX, endY;
        final int hash;

        PathKey(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.hash = Objects.hash(startX, startY, endX, endY);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathKey key = (PathKey) o;
            return startX == key.startX &&
                    startY == key.startY &&
                    endX == key.endX &&
                    endY == key.endY;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    private static class PathCacheEntry {
        final ArrayList<int[]> path;
        final long timestamp;

        PathCacheEntry(ArrayList<int[]> path) {
            this.path = new ArrayList<>(path); // Create defensive copy
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_DURATION;
        }
    }

    static {
        // Initialize cache cleanup
        ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "PathCache-Cleanup");
            thread.setDaemon(true);
            return thread;
        });

        cleanupExecutor.scheduleAtFixedRate(() -> pathCache.entrySet().removeIf(entry -> entry.getValue().isExpired()), CACHE_DURATION, CACHE_DURATION, TimeUnit.MILLISECONDS);
    }


    /**
     * Megkeresi az optimális útvonalat két pont között.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdőpont X koordinátája
     * @param startY kezdőpont Y koordinátája
     * @param endX célpont X koordinátája
     * @param endY célpont Y koordinátája
     * @return ArrayList<int[]> az útvonal koordinátáinak listája, vagy null ha nem található útvonal
     */
    public static ArrayList<int[]> findPath(Engine eng, int startX, int startY, int endX, int endY) {
        int startI = startY / eng.getTileSize();
        int startJ = startX / eng.getTileSize();
        int endI = endY / eng.getTileSize();
        int endJ = endX / eng.getTileSize();

        try {
            startI = Math.max(Math.min(startI, eng.getMaxWorldRow() - 1), 0);
            startJ = Math.max(Math.min(startJ, eng.getMaxWorldCol() - 1), 0);
            endI = Math.max(Math.min(endI, eng.getMaxWorldRow() - 1), 0);
            endJ = Math.max(Math.min(endJ, eng.getMaxWorldCol() - 1), 0);
        } catch(Exception e) {
            GameLogger.error(LOG_CONTEXT, "Invalid coordinates: " + e.getMessage(), e);
            return null;
        }

        PathKey key = new PathKey(startI, startJ, endI, endJ);
        PathCacheEntry cached = pathCache.get(key);
        if (cached != null && !cached.isExpired()) {
            return new ArrayList<>(cached.path);
        }

        ArrayList<int[]> path = calculatePath(eng, startI, startJ, endI, endJ);
        if (path != null) {
            pathCache.put(key, new PathCacheEntry(path));
        }

        return path;
    }

    /**
     * Kiszámítja az útvonalat két pont között az A* algoritmus segítségével.
     *
     * @param eng a játékmotor példánya
     * @param startI kezdőpont sor indexe
     * @param startJ kezdőpont oszlop indexe
     * @param endI célpont sor indexe
     * @param endJ célpont oszlop indexe
     * @return ArrayList<int[]> az útvonal koordinátáinak listája, vagy null ha nem található útvonal
     */
    private static ArrayList<int[]> calculatePath(Engine eng, int startI, int startJ, int endI, int endJ) {
        int rows = eng.getMaxWorldRow();
        int cols = eng.getMaxWorldCol();

        Cell[][] grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

        Map<Cell, Double> dynamicCosts = calculateDynamicCosts(eng, rows, cols);

        PriorityQueue<Cell> openList = new PriorityQueue<>(
                Comparator.comparingDouble(c -> c.finalCost)
        );
        boolean[][] closedList = new boolean[rows][cols];

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
                    if (eng.tileman.tile[TileManager.mapTileNum[nextJ][nextI]].collision) continue;

                    Cell neighbor = grid[nextI][nextJ];

                    double movementCost = (i == 0 || j == 0) ? V_H_COST : DIAGONAL_COST;

                    movementCost += dynamicCosts.getOrDefault(neighbor, 0.0);

                    double newCost = current.finalCost + movementCost;

                    if (!openList.contains(neighbor) || newCost < neighbor.finalCost) {
                        neighbor.heuristicCost = calculateHeuristic(neighbor, endI, endJ);
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

    /**
     * Kiszámítja a heurisztikus költséget két pont között (Manhattan távolság).
     *
     * @param cell a vizsgált cella
     * @param endI célpont sor indexe
     * @param endJ célpont oszlop indexe
     * @return a heurisztikus költség értéke
     */
    private static int calculateHeuristic(Cell cell, int endI, int endJ) {
        return Math.abs(cell.i - endI) + Math.abs(cell.j - endJ);
    }

    /**
     * Kiszámítja a dinamikus költségeket a pályán lévő entitások alapján.
     * Ez befolyásolja az útvonalválasztást, hogy az entitások elkerülhetők legyenek.
     *
     * @param eng a játékmotor példánya
     * @param rows a pálya sorainak száma
     * @param cols a pálya oszlopainak száma
     * @return Map<Cell, Double> a cellákhoz tartozó dinamikus költségek
     */
    private static Map<Cell, Double> calculateDynamicCosts(Engine eng, int rows, int cols) {
        Map<Cell, Double> dynamicCosts = new HashMap<>();

        for (Entity entity : eng.getEntity()) {
            int entityI = entity.getWorldY() / eng.getTileSize();
            int entityJ = entity.getWorldX() / eng.getTileSize();

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

    /**
     * Rekonstruálja a megtalált útvonalat a célponttól visszafelé haladva.
     *
     * @param end a célpontot tartalmazó cella
     * @return ArrayList<int[]> az útvonal koordinátáinak listája
     */
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