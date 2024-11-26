package leaderboard;

import main.logger.GameLogger;

public class GameTimer {
    private long startTime;
    private long totalTime;
    private boolean isRunning;
    private static final String LOG_CONTEXT = "[GAME TIMER]";

    public GameTimer() {
        this.startTime = 0;
        this.totalTime = 0;
        this.isRunning = false;
    }

    /**
     * Elindítja vagy folytatja az időmérést
     */
    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            GameLogger.info(LOG_CONTEXT, "Timer started");
        }
    }

    /**
     * Megállítja az időmérést és hozzáadja az eltelt időt a teljes időhöz
     */
    public void stop() {
        if (isRunning) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            totalTime += elapsedTime;
            isRunning = false;
            GameLogger.info(LOG_CONTEXT, "Timer paused. Elapsed time: " + elapsedTime + "ms");
        }
    }

    /**
     * Nullázza az időmérőt
     */
    public void reset() {
        startTime = 0;
        totalTime = 0;
        isRunning = false;
        GameLogger.info(LOG_CONTEXT, "Timer reset");
    }

    /**
     * Visszaadja a teljes játékidőt másodpercekben
     */
    public long getElapsedTimeInSeconds() {
        long currentTotal = totalTime;
        if (isRunning) {
            currentTotal += System.currentTimeMillis() - startTime;
        }
        return currentTotal / 1000;
    }

    /**
     * Visszaadja a formázott időt "HH:MM:SS" formátumban
     */
    public String getFormattedTime() {
        long seconds = getElapsedTimeInSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}