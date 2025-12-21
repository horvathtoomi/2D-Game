package serializable;

import main.GameDifficulty;
import main.GameMode;

import java.io.Serial;
import java.io.Serializable;

public class GameMetadata implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public GameMode gameMode;
    public GameDifficulty difficulty;
    public int currentStoryLevel;

    GameMetadata(GameMode gameMode, GameDifficulty difficulty, int currentStoryLevel) {
        this.gameMode = gameMode;
        this.difficulty = difficulty;
        this.currentStoryLevel = currentStoryLevel;
    }
}