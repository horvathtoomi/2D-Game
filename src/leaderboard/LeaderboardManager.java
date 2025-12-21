package leaderboard;

import main.Engine;
import main.GameDifficulty;
import main.logger.GameLogger;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "res/leaderboard/leaderboard.txt";
    private static final int MAX_ENTRIES_PER_DIFFICULTY = 25;
    private static final String LOG_CONTEXT = "[LEADERBOARD]";

    private Map<GameDifficulty, List<LeaderboardEntry>> leaderboards;
    private static LeaderboardManager instance;

    private LeaderboardManager() {
        leaderboards = new EnumMap<>(GameDifficulty.class);
        for (GameDifficulty difficulty : GameDifficulty.values()) {
            leaderboards.put(difficulty, new ArrayList<>());
        }
        loadLeaderboard();
    }

    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }

    public void addEntry(LeaderboardEntry entry) {
        List<LeaderboardEntry> difficultyLeaderboard = leaderboards.get(entry.getDifficulty());
        difficultyLeaderboard.add(entry);
        Collections.sort(difficultyLeaderboard);

        if (difficultyLeaderboard.size() > MAX_ENTRIES_PER_DIFFICULTY) {
            difficultyLeaderboard = new ArrayList<>(
                    difficultyLeaderboard.subList(0, MAX_ENTRIES_PER_DIFFICULTY)
            );
            leaderboards.put(entry.getDifficulty(), difficultyLeaderboard);
        }

        saveLeaderboard();
    }

    public List<LeaderboardEntry> getEntriesForDifficulty(GameDifficulty difficulty) {
        return new ArrayList<>(leaderboards.get(difficulty));
    }

    private void loadLeaderboard() {
        try {
            // Könyvtár létrehozása, ha nem létezik
            Path path = Paths.get(LEADERBOARD_FILE);
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
                return;
            }
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                try {
                    LeaderboardEntry entry = LeaderboardEntry.fromString(line);
                    leaderboards.get(entry.getDifficulty()).add(entry);
                } catch (Exception e) {
                    GameLogger.error(LOG_CONTEXT, "Failed to parse leaderboard entry: " + line, e);
                }
            }

            for (GameDifficulty difficulty : GameDifficulty.values()) {
                List<LeaderboardEntry> entries = leaderboards.get(difficulty);
                Collections.sort(entries);
                if (entries.size() > MAX_ENTRIES_PER_DIFFICULTY) {
                    leaderboards.put(difficulty, new ArrayList<>(
                            entries.subList(0, MAX_ENTRIES_PER_DIFFICULTY)
                    ));
                }
            }

            GameLogger.info(LOG_CONTEXT, "Leaderboard loaded successfully");
        } catch (IOException e) {
            GameLogger.error(LOG_CONTEXT, "Failed to load leaderboard", e);
        }
    }

    private void saveLeaderboard() {
        try {
            List<String> lines = new ArrayList<>();
            for (List<LeaderboardEntry> entries : leaderboards.values()) {
                for (LeaderboardEntry entry : entries) {
                    lines.add(entry.toFileString());
                }
            }
            Files.write(Paths.get(LEADERBOARD_FILE), lines);
            GameLogger.info(LOG_CONTEXT, "Leaderboard saved successfully");
        } catch (IOException e) {
            GameLogger.error(LOG_CONTEXT, "Failed to save leaderboard", e);
        }
    }
}