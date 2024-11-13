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

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.BLACK);
        switch (gp.getGameState()) {
            case START -> drawStartScreen();
            case GAME_MODE_SCREEN -> drawModeChoosingScreen();
            case DIFFICULTY_SCREEN -> drawDifficultyScreen();
            case FINISHED_LOST, FINISHED_WON -> drawGameEndScreen();
            case PAUSED -> drawPauseScreen();
            default -> drawPlayerHealthBar(); //case RUNNING
        }
    }

    private void drawModeChoosingScreen(){
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
        g2.setColor(Color.WHITE);
        g2.setFont(arial_80);
        String title = "Choose Game Mode!";
        int x = getXforCenteredText(title);
        int y = gp.getScreenHeight() / 4;
        g2.drawString(title, x, y);
        for (Button button : modeScreenButtons)
            button.draw(g2);
    }

    private void drawStartScreen() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
        g2.setColor(Color.WHITE);
        g2.setFont(arial_80);
        String title = "2D Game";
        int x = getXforCenteredText(title);
        int y = gp.getScreenHeight() / 4;
        g2.drawString(title, x, y);
        for (Button button : startScreenButtons)
            button.draw(g2);
    }

    private void drawDifficultyScreen() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
        g2.setColor(Color.WHITE);
        g2.setFont(arial_80);
        String title = "SET DIFFICULTY";
        int x = getXforCenteredText(title);
        int y = gp.getScreenHeight() / 4;
        g2.drawString(title, x, y);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("Keys: 1->EASY, 2->MEDIUM, 3->HARD, 4->IMPOSSIBLE", 10, 10);
        for (Button button : difficultyScreenButtons)
            button.draw(g2);
    }

    private void drawGameEndScreen() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
        g2.setColor(Color.RED);
        g2.setFont(arial_80);
        String gameOverText = "GAME OVER";
        String wonGameText = "YOU WON";
        int x = getXforCenteredText(gameOverText);
        int y = gp.getScreenHeight() / 4;
        if(gp.getGameState().equals(Engine.GameState.FINISHED_LOST))
            g2.drawString(gameOverText, x, y);
        else
            g2.drawString(wonGameText, x, y);
        for (Button button : endScreenButtons)
            button.draw(g2);
    }

    private void drawPauseScreen() {
        // Draw a semi-transparent black overlay
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
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

    public void handleStartScreenClick(Point p) {
        for (int i = 0; i < startScreenButtons.size(); i++) {
            if (startScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> gp.setGameState(Engine.GameState.GAME_MODE_SCREEN);
                    case 1 -> {
                        FileManager.loadGame(gp);
                        gp.setGameState(Engine.GameState.RUNNING);
                    }
                    default -> System.exit(0); // case 2
                }
                break;
            }
        }
    }


    public void handleGameModeScreenClick(Point p) {
        for (int i = 0; i < startScreenButtons.size(); i++) {
            if (startScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> {
                        gp.setGameMode(Engine.GameMode.STORY);
                        gp.setGameState(Engine.GameState.DIFFICULTY_SCREEN);
                    }
                    case 1 -> {
                        gp.setGameMode(Engine.GameMode.CUSTOM);
                        Engine.setupCustomMode();
                    }
                    case 2 -> gp.setGameState(Engine.GameState.START);
                }
                break;
            }
        }
    }


    public void handleDifficultyScreenClick(Point p) {
        for (int i = 0; i < difficultyScreenButtons.size(); i++) {
            if (difficultyScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> gp.setGameDifficulty(Engine.GameDifficulty.EASY);
                    case 1 -> gp.setGameDifficulty(Engine.GameDifficulty.MEDIUM);
                    case 2 -> gp.setGameDifficulty(Engine.GameDifficulty.HARD);
                    default -> gp.setGameDifficulty(Engine.GameDifficulty.IMPOSSIBLE); // case 3
                }
                gp.setGameState(Engine.GameState.RUNNING);
                gp.startGame();
                break;
            }
        }
    }

    public void handleGameOverClick(Point p) {
        for (int i = 0; i < endScreenButtons.size(); i++) {
            if (endScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> gp.setGameState(Engine.GameState.DIFFICULTY_SCREEN);
                    case 1 -> {
                        FileManager.loadGame(gp);
                        gp.setGameState(Engine.GameState.RUNNING);
                    }
                    default -> System.exit(0); // case 2
                }
                break;
            }
        }
    }

    public void handlePauseScreenClick(Point p) {
        for (int i = 0; i < pauseScreenButtons.size(); i++) {
            if (pauseScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> gp.setGameState(Engine.GameState.RUNNING);
                    case 1 -> {
                        gp.setGameState(Engine.GameState.CONSOLE_INPUT);
                        try {
                            gp.console.startConsoleInput();
                        } catch (Exception e) {
                            GameLogger.error(LOG_CONTEXT, "Console input error: {0}", e.getCause());
                        }
                    }
                    case 2 -> System.exit(0);
                    case 3 -> {
                        gp.startGame();
                        gp.setGameState(Engine.GameState.DIFFICULTY_SCREEN);
                    }
                    case 4 -> FileManager.saveGame(gp);
                    case 5 -> {
                        FileManager.loadGame(gp);
                        gp.setGameState(Engine.GameState.RUNNING);
                    }
                }
                break;
            }
        }
    }

    public void handleHover(Point p) {
        ArrayList<Button> hoverButtons = new ArrayList<>();
        switch (gp.getGameState()) {
            case START -> hoverButtons.addAll(startScreenButtons);
            case DIFFICULTY_SCREEN -> hoverButtons.addAll(difficultyScreenButtons);
            case PAUSED -> hoverButtons.addAll(pauseScreenButtons);
            case FINISHED_LOST, FINISHED_WON -> hoverButtons.addAll(endScreenButtons);
        }
        for (Button button : hoverButtons) {
            if (button.contains(p))
                button.setBackgroundColor(new Color(100, 100, 100));
            else
                button.setBackgroundColor(new Color(70, 70, 70));
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

        initButtons("gamemode", gp.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "Story Mode");
        initButtons("gamemode", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Custom Map");
        initButtons("gamemode", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Back");


        initButtons("pause", gp.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY, buttonWidth, buttonHeight, "Resume");
        initButtons("pause", gp.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Console Input");
        initButtons("pause", gp.getScreenWidth() / 2 - buttonWidth - buttonWidth / 8, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit");
        initButtons("pause", gp.getScreenWidth() / 2 + buttonWidth / 8, startY, buttonWidth, buttonHeight, "New Game");
        initButtons("pause", gp.getScreenWidth() / 2 + buttonWidth / 8, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Save Game");
        initButtons("pause", gp.getScreenWidth() / 2 + buttonWidth / 8, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Load Game");
        initButtons("difficulty", gp.getScreenWidth() / 2 - buttonWidth / 2 - buttonWidth, startY, buttonWidth, buttonHeight, "EASY");
        initButtons("difficulty", gp.getScreenWidth() / 2 - buttonWidth / 2 - buttonWidth, startY + buttonHeight + 20, buttonWidth, buttonHeight, "MEDIUM");
        initButtons("difficulty", gp.getScreenWidth() / 2 + buttonWidth / 2, startY, buttonWidth, buttonHeight, "HARD");
        initButtons("difficulty", gp.getScreenWidth() / 2 + buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "IMPOSSIBLE");
        initButtons("end", gp.getScreenWidth() / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "New Game");
        initButtons("end", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Load Game");
        initButtons("end", gp.getScreenWidth() / 2 - buttonWidth / 2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit");
    }
}