package serializable;

import main.Engine;

import java.io.Serial;
import java.io.Serializable;

public class GameMetadata implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public Engine.GameMode gameMode;
    public Engine.GameDifficulty difficulty;
    public int currentStoryLevel;

    GameMetadata(Engine.GameMode gameMode, Engine.GameDifficulty difficulty, int currentStoryLevel) {
        this.gameMode = gameMode;
        this.difficulty = difficulty;
        this.currentStoryLevel = currentStoryLevel;
    }
}