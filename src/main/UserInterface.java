package main;

import main.logger.GameLogger;
import serializable.FileManager;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class UserInterface extends JFrame {
    Engine gp;
    transient Graphics2D g2;
    Font arial_40;
    Font arial_80;
    transient ArrayList<Button> startScreenButtons;
    transient ArrayList<Button> endScreenButtons;
    transient ArrayList<Button> pauseScreenButtons;
    transient ArrayList<Button> modeScreenButtons;
    transient ArrayList<Button> difficultyScreenButtons;
    private static final String LOG_CONTEXT = "[USER INTERFACE]";

    // Start screen colors
    private static final Color START_GRADIENT_TOP = new Color(50, 50, 150);
    private static final Color START_GRADIENT_BOTTOM = new Color(0, 0, 50);
    private static final Color START_BUTTON = new Color(70, 130, 180);
    private static final Color START_BUTTON_HOVER = new Color(100, 149, 237);

    // Mode screen colors
    private static final Color MODE_GRADIENT_TOP = new Color(70, 70, 170);
    private static final Color MODE_GRADIENT_BOTTOM = new Color(20, 20, 70);
    private static final Color MODE_BUTTON = new Color(90, 150, 200);
    private static final Color MODE_BUTTON_HOVER = new Color(120, 169, 255);  // Javítva: 257 -> 255

    // Difficulty screen colors
    private static final Color DIFFICULTY_GRADIENT_TOP = new Color(40, 60, 140);
    private static final Color DIFFICULTY_GRADIENT_BOTTOM = new Color(10, 10, 40);
    private static final Color DIFFICULTY_BUTTON = new Color(60, 120, 170);
    private static final Color DIFFICULTY_BUTTON_HOVER = new Color(80, 139, 227);

    // Pause screen colors
    private static final Color PAUSE_GRADIENT_TOP = new Color(40, 100, 40);
    private static final Color PAUSE_GRADIENT_BOTTOM = new Color(0, 40, 0);
    private static final Color PAUSE_BUTTON = new Color(60, 140, 60);
    private static final Color PAUSE_BUTTON_HOVER = new Color(80, 160, 80);

    // Game over screen colors
    private static final Color GAMEOVER_OVERLAY = new Color(120, 0, 0, 180);
    private static final Color GAMEOVER_BUTTON = new Color(140, 40, 40);
    private static final Color GAMEOVER_BUTTON_HOVER = new Color(170, 60, 60);

    public UserInterface(Engine gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80 = new Font("Arial", Font.BOLD, 80);
        startScreenButtons = new ArrayList<>();
        endScreenButtons = new ArrayList<>();
        pauseScreenButtons = new ArrayList<>();
        difficultyScreenButtons = new ArrayList<>();
        modeScreenButtons = new ArrayList<>();
        initializeScreenButtons();
    }

    // A draw metódus módosítása a UserInterface osztályban:
    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);

        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (gp.getGameState()) {
            case START -> drawStartScreen();
            case GAME_MODE_SCREEN -> drawModeChoosingScreen();
            case DIFFICULTY_SCREEN -> drawDifficultyScreen();
            case FINISHED_LOST, FINISHED_WON -> drawGameEndScreen();
            case PAUSED -> drawPauseScreen();
            default -> drawPlayerHealthBar();
        }
    }

    private void drawGradientBackground(Color topColor, Color bottomColor) {
        GradientPaint gradient = new GradientPaint(
                0, 0, topColor,
                0, gp.getScreenHeight(), bottomColor
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
    }

    private void drawStartScreen() {
        drawGradientBackground(START_GRADIENT_TOP, START_GRADIENT_BOTTOM);
        g2.setFont(arial_80);
        g2.setColor(Color.WHITE);
        String title = "2D Game";
        int x = getXforCenteredText(title);
        int y = gp.getScreenHeight() / 4;
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
        int y = gp.getScreenHeight() / 4;
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
        int y = gp.getScreenHeight() / 4;
        g2.drawString(title, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("Keys: 1->EASY, 2->MEDIUM, 3->HARD, 4->IMPOSSIBLE", 10, 10);

        for (Button button : difficultyScreenButtons) {
            button.draw(g2);
        }
    }

    private void drawPauseScreen() {
        // First draw the game screen
        drawPlayerHealthBar();

        // Then add green gradient overlay
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        drawGradientBackground(PAUSE_GRADIENT_TOP, PAUSE_GRADIENT_BOTTOM);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        g2.setColor(Color.WHITE);
        g2.setFont(arial_80);
        String pauseText = "PAUSED";
        int x = getXforCenteredText(pauseText);
        int y = gp.getScreenHeight() / 4;
        g2.drawString(pauseText, x, y);

        for (Button button : pauseScreenButtons) {
            button.draw(g2);
        }
    }

    private void drawGameEndScreen() {
        // First draw the game screen
        drawPlayerHealthBar();

        // Add dark red overlay
        g2.setColor(GAMEOVER_OVERLAY);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        g2.setFont(arial_80);
        g2.setColor(Color.WHITE);
        String gameOverText = gp.getGameState() == Engine.GameState.FINISHED_LOST ? "GAME OVER" : "YOU WON";
        int x = getXforCenteredText(gameOverText);
        int y = gp.getScreenHeight() / 4;
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
                gp.setGameState(Engine.GameState.RUNNING);
                gp.startGame();
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
        ArrayList<Button> currentButtons = switch (gp.getGameState()) {
            case START -> {
                setButtonColors(startScreenButtons, START_BUTTON);
                yield startScreenButtons;
            }
            case GAME_MODE_SCREEN -> {
                setButtonColors(modeScreenButtons, MODE_BUTTON);
                yield modeScreenButtons;
            }
            case DIFFICULTY_SCREEN -> {
                setButtonColors(difficultyScreenButtons, DIFFICULTY_BUTTON);
                yield difficultyScreenButtons;
            }
            case PAUSED -> {
                setButtonColors(pauseScreenButtons, PAUSE_BUTTON);
                yield pauseScreenButtons;
            }
            case FINISHED_LOST, FINISHED_WON -> {
                setButtonColors(endScreenButtons, GAMEOVER_BUTTON);
                yield endScreenButtons;
            }
            default -> new ArrayList<>();
        };

        // Handle hover effects
        for (Button button : currentButtons) {
            if (button.contains(p)) {
                Color hoverColor = switch (gp.getGameState()) {
                    case START -> START_BUTTON_HOVER;
                    case GAME_MODE_SCREEN -> MODE_BUTTON_HOVER;
                    case DIFFICULTY_SCREEN -> DIFFICULTY_BUTTON_HOVER;
                    case PAUSED -> PAUSE_BUTTON_HOVER;
                    case FINISHED_LOST, FINISHED_WON -> GAMEOVER_BUTTON_HOVER;
                    default -> START_BUTTON_HOVER;
                };
                button.setBackgroundColor(hoverColor);
            }
        }
    }

    private void setButtonColors(ArrayList<Button> buttons, Color defaultColor) {
        for (Button button : buttons) {
            button.setBackgroundColor(defaultColor);
        }
    }

    private int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.getScreenWidth() / 2 - length / 2;
    }

    private void drawPlayerHealthBar() {
        int x = gp.getTileSize();
        int y = gp.getTileSize();
        int width = 200;
        int height = 20;

        // Calculate the width of the red health bar
        int maxHealthBarWidth = (int) ((gp.player.getMaxHealth() / 100.0) * width);
        int normalHealthBarWidth = (int) ((gp.player.getHealth() / 100.0) * width);
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, maxHealthBarWidth, height);
        // Draw red health bar
        g2.setColor(Color.RED);
        g2.fillRect(x, y, normalHealthBarWidth, height);

        // Draw white border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, maxHealthBarWidth, height);

        // Draw HP text
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        String hpText = gp.player.getHealth() + "/" + gp.player.getMaxHealth() + " HP";
        int textX = x + maxHealthBarWidth / 2 - g2.getFontMetrics().stringWidth(hpText) / 2;
        int textY = y - 5;
        g2.drawString(hpText, textX, textY);
    }

    private void initButtons(String type, int x, int y, int width, int heigth, String text) {
        switch (type.toLowerCase()) {
            case "start" -> startScreenButtons.add(new Button(x, y, width, heigth, text));
            case "pause" -> pauseScreenButtons.add(new Button(x, y, width, heigth, text));
            case "difficulty" -> difficultyScreenButtons.add(new Button(x, y, width, heigth, text));
            case "gamemode" -> modeScreenButtons.add(new Button(x, y, width, heigth, text));
            case "end" -> endScreenButtons.add(new Button(x, y, width, heigth, text));
        }
    }


    private void initializeScreenButtons() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int startY = gp.getScreenHeight() / 2;
        initButtons("start", gp.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "Start Game");
        initButtons("start", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Load Game");
        initButtons("start", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Quit");
        startScreenButtons.getFirst().addActionListener(e -> gp.setGameState(Engine.GameState.GAME_MODE_SCREEN));
        startScreenButtons.get(1).addActionListener(e -> {
            if(FileManager.loadGame(gp))
                gp.setGameState(Engine.GameState.RUNNING);
            else
                gp.setGameState(Engine.GameState.START);
        });
        startScreenButtons.get(2).addActionListener(e -> System.exit(0));
        initButtons("gamemode", gp.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "Story Mode");
        initButtons("gamemode", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Custom Map");
        initButtons("gamemode", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Back");
        modeScreenButtons.getFirst().addActionListener(e -> {
            gp.setGameMode(Engine.GameMode.STORY);
            gp.setGameState(Engine.GameState.DIFFICULTY_SCREEN);
        });
        modeScreenButtons.get(1).addActionListener(e -> {
            gp.setGameMode(Engine.GameMode.CUSTOM);
            Engine.setupCustomMode();
        });
        modeScreenButtons.get(2).addActionListener(e -> gp.setGameState(Engine.GameState.START));
        initButtons("pause", gp.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY, buttonWidth, buttonHeight, "Resume");
        initButtons("pause", gp.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Console Input");
        initButtons("pause", gp.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit");
        initButtons("pause", gp.getScreenWidth() / 2 + buttonWidth / 8, startY, buttonWidth, buttonHeight, "New Game");
        initButtons("pause", gp.getScreenWidth() / 2 + buttonWidth / 8, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Save Game");
        initButtons("pause", gp.getScreenWidth() / 2 + buttonWidth / 8, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Load Game");
        pauseScreenButtons.getFirst().addActionListener(e -> gp.setGameState(Engine.GameState.RUNNING));
        pauseScreenButtons.get(1).addActionListener(e -> {
            gp.setGameState(Engine.GameState.CONSOLE_INPUT);
            try {
                gp.console.startConsoleInput();
            } catch (Exception err) {
                GameLogger.error(LOG_CONTEXT, "Console input error: {0}", err.getCause());
            }
        });
        pauseScreenButtons.get(2).addActionListener(e -> System.exit(0));
        pauseScreenButtons.get(3).addActionListener(e -> {
            gp.startGame();
            gp.setGameState(Engine.GameState.DIFFICULTY_SCREEN);
        });
        pauseScreenButtons.get(4).addActionListener(e -> FileManager.saveGame(gp));
        pauseScreenButtons.get(5).addActionListener(e -> FileManager.loadGame(gp));
        initButtons("difficulty", gp.getScreenWidth() / 2 - buttonWidth / 2 - buttonWidth, startY, buttonWidth, buttonHeight, "EASY");
        initButtons("difficulty", gp.getScreenWidth() / 2 - buttonWidth / 2 - buttonWidth, startY + buttonHeight + 20, buttonWidth, buttonHeight, "MEDIUM");
        initButtons("difficulty", gp.getScreenWidth() / 2 + buttonWidth / 2, startY, buttonWidth, buttonHeight, "HARD");
        initButtons("difficulty", gp.getScreenWidth() / 2 + buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "IMPOSSIBLE");
        difficultyScreenButtons.getFirst().addActionListener(e -> gp.setGameDifficulty(Engine.GameDifficulty.EASY));
        difficultyScreenButtons.get(1).addActionListener(e -> gp.setGameDifficulty(Engine.GameDifficulty.MEDIUM));
        difficultyScreenButtons.get(2).addActionListener(e -> gp.setGameDifficulty(Engine.GameDifficulty.HARD));
        difficultyScreenButtons.get(3).addActionListener(e -> gp.setGameDifficulty(Engine.GameDifficulty.IMPOSSIBLE));
        initButtons("end", gp.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "New Game");
        initButtons("end", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Load Game");
        initButtons("end", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit");
        endScreenButtons.getFirst().addActionListener(e -> gp.setGameState(Engine.GameState.DIFFICULTY_SCREEN));
        endScreenButtons.get(1).addActionListener(e -> {
            FileManager.loadGame(gp);
            gp.setGameState(Engine.GameState.RUNNING);
        });
        endScreenButtons.get(2).addActionListener(e -> System.exit(0));
    }

}