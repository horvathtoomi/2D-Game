package leaderboard;

import main.Engine;
import main.GameDifficulty;

import java.time.Duration;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private final String playerName;
    private final long timeInSeconds;
    private final GameDifficulty difficulty;
    private final int enemiesDefeated;
    private final int finalHealth;
    private final int score;

    public LeaderboardEntry(String playerName, long timeInSeconds, GameDifficulty difficulty, int enemiesDefeated, int finalHealth) {
        this.playerName = playerName;
        this.timeInSeconds = timeInSeconds;
        this.difficulty = difficulty;
        this.enemiesDefeated = enemiesDefeated;
        this.finalHealth = finalHealth;
        this.score = calculateScore();
    }

    // Létrehozás string formátumból (fájlból olvasáskor)
    public static LeaderboardEntry fromString(String line) {
        String[] parts = line.split("\\|");
        return new LeaderboardEntry(
                parts[0],
                Long.parseLong(parts[2]),
                GameDifficulty.valueOf(parts[1]),
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4])
        );
    }

    // Átalakítás string formátumba (fájlba íráskor)
    public String toFileString() {
        return String.format("%s|%s|%d|%d|%d|%d",
                playerName, difficulty, timeInSeconds, enemiesDefeated, finalHealth, score);
    }

    private int calculateScore() {
        int timeScore = (int) (100000 / (timeInSeconds + 1));
        int enemyScore = enemiesDefeated * getDifficultyMultiplier() * 100;
        int healthBonus = finalHealth * 10;
        return timeScore + enemyScore + healthBonus;
    }

    private int getDifficultyMultiplier() {
        return switch (difficulty) {
            case EASY -> 1;
            case MEDIUM -> 2;
            case HARD -> 3;
            case IMPOSSIBLE -> 4;
        };
    }

    // Getterek
    public String getPlayerName() { return playerName; }
    public long getTimeInSeconds() { return timeInSeconds; }
    public GameDifficulty getDifficulty() { return difficulty; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public int getFinalHealth() { return finalHealth; }
    public int getScore() { return score; }

    public String getFormattedTime() {
        Duration duration = Duration.ofSeconds(timeInSeconds);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        return Integer.compare(other.score, this.score);
    }
}