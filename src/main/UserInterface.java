package main;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import leaderboard.LeaderboardDialog;
import main.logger.GameLogger;
import serializable.FileManager;

/**
 * A UserInterface osztály felelős a játék felhasználói felületének megjelenítéséért.
 * Kezeli a menüket, gombokat és játékállapot kijelzőket.
 */
public class UserInterface extends JFrame {
    Engine eng;
    transient Graphics2D g2;
    Font arial_40;
    Font arial_80;
    transient ArrayList<Button> startScreenButtons;
    transient ArrayList<Button> endScreenButtons;
    transient ArrayList<Button> pauseScreenButtons;
    transient ArrayList<Button> modeScreenButtons;
    transient ArrayList<Button> difficultyScreenButtons;
    private static final String LOG_CONTEXT = "[USER INTERFACE]";

    // Start screen
    private static final Color START_GRADIENT_TOP = new Color(50, 50, 150);
    private static final Color START_GRADIENT_BOTTOM = new Color(0, 0, 50);
    private static final Color START_BUTTON = new Color(70, 130, 180);
    private static final Color START_BUTTON_HOVER = new Color(100, 149, 237);

    // Mode screen
    private static final Color MODE_GRADIENT_TOP = new Color(70, 70, 170);
    private static final Color MODE_GRADIENT_BOTTOM = new Color(20, 20, 70);
    private static final Color MODE_BUTTON = new Color(90, 150, 200);
    private static final Color MODE_BUTTON_HOVER = new Color(120, 169, 255);

    // Difficulty screen
    private static final Color DIFFICULTY_GRADIENT_TOP = new Color(40, 60, 140);
    private static final Color DIFFICULTY_GRADIENT_BOTTOM = new Color(10, 10, 40);
    private static final Color DIFFICULTY_BUTTON = new Color(60, 120, 170);
    private static final Color DIFFICULTY_BUTTON_HOVER = new Color(80, 139, 227);

    // Pause screen
    private static final Color PAUSE_GRADIENT_TOP = new Color(40, 100, 40);
    private static final Color PAUSE_GRADIENT_BOTTOM = new Color(0, 40, 0);
    private static final Color PAUSE_BUTTON = new Color(60, 140, 60);
    private static final Color PAUSE_BUTTON_HOVER = new Color(90, 170, 100);

    // Game over screen
    private static final Color GAMEOVER_OVERLAY = new Color(120, 0, 0, 180);
    private static final Color GAMEOVER_BUTTON = new Color(140, 40, 40);
    private static final Color GAMEOVER_BUTTON_HOVER = new Color(170, 60, 60);

    /**
     * Létrehoz egy új felhasználói felület példányt.
     * @param eng a játékmotor példánya
     */
    public UserInterface(Engine eng) {
        this.eng = eng;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80 = new Font("Arial", Font.BOLD, 80);
        startScreenButtons = new ArrayList<>();
        endScreenButtons = new ArrayList<>();
        pauseScreenButtons = new ArrayList<>();
        difficultyScreenButtons = new ArrayList<>();
        modeScreenButtons = new ArrayList<>();
        initializeScreenButtons();
    }

    /**
     * Kirajzolja a felhasználói felületet az aktuális játékállapotnak megfelelően.
     * @param g2 a grafikus kontextus
     */
    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (eng.getGameState()) {
            case START -> drawStartScreen();
            case GAME_MODE_SCREEN -> drawModeChoosingScreen();
            case DIFFICULTY_SCREEN -> drawDifficultyScreen();
            case FINISHED_LOST, FINISHED_WON -> drawGameEndScreen();
            case PAUSED, CONSOLE_INPUT -> drawPauseScreen();
            default -> drawPlayerHealthBar();
        }
    }

    private void drawGradientBackground(Color topColor, Color bottomColor) {
        GradientPaint gradient = new GradientPaint(
                0, 0, topColor,
                0, eng.getScreenHeight(), bottomColor
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, eng.getScreenWidth(), eng.getScreenHeight());
    }

    private void drawStartScreen() {
        drawGradientBackground(START_GRADIENT_TOP, START_GRADIENT_BOTTOM);
        g2.setFont(arial_80);
        g2.setColor(Color.WHITE);
        String title = "2D Game";
        int x = getXforCenteredText(title);
        int y = eng.getScreenHeight() / 4;
        g2.drawString(title, x, y);

        for (Button button : startScreenButtons) {
            button.draw(g2);
        }
    }

    private void drawModeChoosingScreen() {
        drawGradientBackground(MODE_GRADIENT_TOP, MODE_GRADIENT_BOTTOM);
        g2.setFont(arial_80);
        g2.setColor(Color.WHITE);
        String title = "Choose Game Mode!";
        int x = getXforCenteredText(title);
        int y = eng.getScreenHeight() / 4;
        g2.drawString(title, x, y);

        for (Button button : modeScreenButtons) {
            button.draw(g2);
        }
    }

    private void drawDifficultyScreen() {
        drawGradientBackground(DIFFICULTY_GRADIENT_TOP, DIFFICULTY_GRADIENT_BOTTOM);
        g2.setFont(arial_80);
        g2.setColor(Color.WHITE);
        String title = "SET DIFFICULTY";
        int x = getXforCenteredText(title);
        int y = eng.getScreenHeight() / 4;
        g2.drawString(title, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("Keys: 1->EASY, 2->MEDIUM, 3->HARD, 4->IMPOSSIBLE", 10, 10);

        for (Button button : difficultyScreenButtons) {
            button.draw(g2);
        }
    }

    private void drawPauseScreen() {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        drawGradientBackground(PAUSE_GRADIENT_TOP, PAUSE_GRADIENT_BOTTOM);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        g2.setColor(Color.WHITE);
        g2.setFont(arial_80);
        String pauseText = "PAUSED";
        int x = getXforCenteredText(pauseText);
        int y = eng.getScreenHeight() / 4;
        g2.drawString(pauseText, x, y);

        for (Button button : pauseScreenButtons) {
            button.draw(g2);
        }
    }

    private void drawGameEndScreen() {
        g2.setColor(GAMEOVER_OVERLAY);
        g2.fillRect(0, 0, eng.getScreenWidth(), eng.getScreenHeight());

        g2.setFont(arial_80);
        g2.setColor(Color.WHITE);
        String gameOverText = eng.getGameState() == GameState.FINISHED_LOST ? "YOU DIED" : "YOU WON";
        int x = getXforCenteredText(gameOverText);
        int y = eng.getScreenHeight() / 4;
        g2.drawString(gameOverText, x, y);

        for (Button button : endScreenButtons) {
            button.draw(g2);
        }
    }

    public void handleStartScreenClick(Point p) {
        for (Button button : startScreenButtons) {
            if (button.contains(p)) {
                button.doClick();
                break;
            }
        }
    }

    public void handleGameModeScreenClick(Point p) {
        for (Button button : modeScreenButtons) {
            if (button.contains(p)) {
                button.doClick();
                break;
            }
        }
    }

    public void handleDifficultyScreenClick(Point p) {
        for (Button button : difficultyScreenButtons) {
            if (button.contains(p)) {
                button.doClick();
                eng.setGameState(GameState.RUNNING);
                eng.startGame();
                break;
            }
        }
    }

    public void handleGameOverClick(Point p) {
        for (Button button : endScreenButtons) {
            if (button.contains(p)) {
                button.doClick();
                break;
            }
        }
    }

    public void handlePauseScreenClick(Point p) {
        for (Button button : pauseScreenButtons) {
            if (button.contains(p)) {
                button.doClick();
                break;
            }
        }
    }

    public void handleHover(Point p) {
        ArrayList<Button> currentButtons = switch (eng.getGameState()) {
            case START -> startScreenButtons;
            case GAME_MODE_SCREEN -> modeScreenButtons;
            case DIFFICULTY_SCREEN -> difficultyScreenButtons;
            case PAUSED -> pauseScreenButtons;
            case FINISHED_LOST, FINISHED_WON -> endScreenButtons;
            default -> new ArrayList<>();
        };
        for (Button button : currentButtons) {
            if (button.contains(p)) {
                Color hoverColor = switch (eng.getGameState()) {
                    case START -> START_BUTTON_HOVER;
                    case GAME_MODE_SCREEN -> MODE_BUTTON_HOVER;
                    case DIFFICULTY_SCREEN -> DIFFICULTY_BUTTON_HOVER;
                    case PAUSED -> PAUSE_BUTTON_HOVER;
                    case FINISHED_LOST, FINISHED_WON -> GAMEOVER_BUTTON_HOVER;
                    default -> START_BUTTON_HOVER;
                };
                button.setBackgroundColor(hoverColor);
            }
            else {
                Color normalColor = switch (eng.getGameState()) {
                    case START -> START_BUTTON;
                    case GAME_MODE_SCREEN -> MODE_BUTTON;
                    case DIFFICULTY_SCREEN -> DIFFICULTY_BUTTON;
                    case PAUSED -> PAUSE_BUTTON;
                    case FINISHED_LOST, FINISHED_WON -> GAMEOVER_BUTTON;
                    default -> START_BUTTON;
                };
                button.setBackgroundColor(normalColor);
            }
        }
    }

    private int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return eng.getScreenWidth() / 2 - length / 2;
    }

    private void drawPlayerHealthBar() {
        int x = eng.getTileSize();
        int y = eng.getTileSize();
        int width = 200;
        int height = 20;

        int maxHealthBarWidth = (int) ((eng.player.getMaxHealth() / 100.0) * width);
        int normalHealthBarWidth = (int) ((eng.player.getHealth() / 100.0) * width);
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, maxHealthBarWidth, height);
        g2.setColor(Color.RED);
        g2.fillRect(x, y, normalHealthBarWidth, height);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, maxHealthBarWidth, height);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        String hpText = eng.player.getHealth() + "/" + eng.player.getMaxHealth() + " HP";
        int textX = x + maxHealthBarWidth / 2 - g2.getFontMetrics().stringWidth(hpText) / 2;
        int textY = y - 5;
        g2.drawString(hpText, textX, textY);
    }

    /**
     * Inicializálja a képernyők gombjait.
     * @param type a gomb típusa
     * @param x x koordináta
     * @param y y koordináta
     * @param width szélesség
     * //@param height magasság
     * @param text gomb szövege
     */
    private void initButtons(String type, int x, int y, int width, int heigth, String text) {
        switch (type.toLowerCase()) {
            case "start" -> startScreenButtons.add(new Button(x, y, width, heigth, text));
            case "pause" -> pauseScreenButtons.add(new Button(x, y, width, heigth, text));
            case "difficulty" -> difficultyScreenButtons.add(new Button(x, y, width, heigth, text));
            case "gamemode" -> modeScreenButtons.add(new Button(x, y, width, heigth, text));
            case "end" -> endScreenButtons.add(new Button(x, y, width, heigth, text));
        }
    }

    private void initScreenButtonBehavior(){
        initStartButtons();
        initModeButtons();
        initDiffButtons();
        initPauseButtons();
        initGameOverButtons();
    }

    private void initStartButtons(){
        startScreenButtons.getFirst().addActionListener(e -> eng.setGameState(GameState.GAME_MODE_SCREEN));
        startScreenButtons.get(1).addActionListener(e -> {
            if(FileManager.loadGame(eng))
                eng.setGameState(GameState.RUNNING);
            else
                eng.setGameState(GameState.START);
        });
        startScreenButtons.get(2).addActionListener(e -> System.exit(0));
        startScreenButtons.get(3).addActionListener(e ->{
            LeaderboardDialog dialog = new LeaderboardDialog(eng, null);
            dialog.showDialog();
        });
        for(Button button : startScreenButtons){
            button.setBackgroundColor(START_BUTTON);
        }
    }

    private void initModeButtons() {
        modeScreenButtons.getFirst().addActionListener(e -> {
            eng.setGameMode(GameMode.STORY);
            eng.setGameState(GameState.DIFFICULTY_SCREEN);
        });
        modeScreenButtons.get(1).addActionListener(e -> {
            eng.setGameMode(GameMode.CUSTOM);
            Engine.setupCustomMode();
        });
        modeScreenButtons.get(2).addActionListener(e -> eng.setGameState(GameState.START));
        for(Button button : modeScreenButtons){
            button.setBackgroundColor(MODE_BUTTON);
        }
    }

    private void initDiffButtons(){
        difficultyScreenButtons.getFirst().addActionListener(e -> eng.setGameDifficulty(GameDifficulty.EASY));
        difficultyScreenButtons.get(1).addActionListener(e -> eng.setGameDifficulty(GameDifficulty.MEDIUM));
        difficultyScreenButtons.get(2).addActionListener(e -> eng.setGameDifficulty(GameDifficulty.HARD));
        difficultyScreenButtons.get(3).addActionListener(e -> eng.setGameDifficulty(GameDifficulty.IMPOSSIBLE));
        for(Button button : difficultyScreenButtons){
            button.setBackgroundColor(DIFFICULTY_BUTTON);
        }
    }

    private void initPauseButtons(){
        pauseScreenButtons.getFirst().addActionListener(e -> eng.setGameState(GameState.RUNNING));
        pauseScreenButtons.get(1).addActionListener(e -> {
            eng.setGameState(GameState.CONSOLE_INPUT);
            try {
                eng.console.startConsoleInput();
            } catch (Exception err) {
                GameLogger.error(LOG_CONTEXT, "Console input error: {0}", err.getCause());
            }
        });
        pauseScreenButtons.get(2).addActionListener(e -> System.exit(0));
        pauseScreenButtons.get(3).addActionListener(e -> {
            eng.startGame();
            eng.setGameState(GameState.DIFFICULTY_SCREEN);
        });
        pauseScreenButtons.get(4).addActionListener(e -> FileManager.saveGame(eng));
        pauseScreenButtons.get(5).addActionListener(e -> FileManager.loadGame(eng));
        pauseScreenButtons.get(6).addActionListener(e -> {
            LeaderboardDialog dialog = new LeaderboardDialog(eng, null);
            dialog.showDialog();
        });
        for(Button button : pauseScreenButtons){
            button.setBackgroundColor(PAUSE_BUTTON);
        }
    }

    private void initGameOverButtons(){
        endScreenButtons.getFirst().addActionListener(e -> {
            eng.setGameState(GameState.GAME_MODE_SCREEN);
            eng.player.setPlayerHealth(100);
        });
        endScreenButtons.get(1).addActionListener(e -> {
            FileManager.loadGame(eng);
            eng.setGameState(GameState.RUNNING);
        });
        endScreenButtons.get(2).addActionListener(e -> System.exit(0));
        endScreenButtons.get(3).addActionListener(e -> {
            LeaderboardDialog dialog = new LeaderboardDialog(eng, null);
            dialog.showDialog();
        });
        for(Button button : endScreenButtons){
            button.setBackgroundColor(GAMEOVER_BUTTON);
        }
    }


    private void initializeScreenButtons() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int startY = eng.getScreenHeight() / 2;
        initButtons("start", eng.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "Start Game");
        initButtons("start", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Load Game");
        initButtons("start", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Quit");
        initButtons("start", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + 3 * (buttonHeight + 20), buttonWidth, buttonHeight, "Leaderboard");

        initButtons("gamemode", eng.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "Story Mode");
        initButtons("gamemode", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Custom Map");
        initButtons("gamemode", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Back");

        initButtons("pause", eng.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY, buttonWidth, buttonHeight, "Resume");
        initButtons("pause", eng.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Console Input");
        initButtons("pause", eng.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit");
        initButtons("pause", eng.getScreenWidth() / 2 + buttonWidth / 8, startY, buttonWidth, buttonHeight, "New Game");
        initButtons("pause", eng.getScreenWidth() / 2 + buttonWidth / 8, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Save Game");
        initButtons("pause", eng.getScreenWidth() / 2 + buttonWidth / 8, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Load Game");
        initButtons("pause", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + 3 * (buttonHeight + 20), buttonWidth, buttonHeight, "Leaderboard");


        initButtons("difficulty", eng.getScreenWidth() / 2 - buttonWidth / 2 - buttonWidth, startY, buttonWidth, buttonHeight, "EASY");
        initButtons("difficulty", eng.getScreenWidth() / 2 - buttonWidth / 2 - buttonWidth, startY + buttonHeight + 20, buttonWidth, buttonHeight, "MEDIUM");
        initButtons("difficulty", eng.getScreenWidth() / 2 + buttonWidth / 2, startY, buttonWidth, buttonHeight, "HARD");
        initButtons("difficulty", eng.getScreenWidth() / 2 + buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "IMPOSSIBLE");

        initButtons("end", eng.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "New Game");
        initButtons("end", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Load Game");
        initButtons("end", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit");
        initButtons("end", eng.getScreenWidth() / 2 - buttonWidth / 2, startY + 3 * (buttonHeight + 20), buttonWidth, buttonHeight, "Leaderboard");


        initScreenButtonBehavior();
    }

}