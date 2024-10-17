package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UserInterface {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    ArrayList<Button> startScreenButtons;
    ArrayList<Button> endScreenButtons;
    ArrayList<Button> pauseScreenButtons;
    JProgressBar health;

    public UserInterface(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
        startScreenButtons = new ArrayList<>();
        endScreenButtons = new ArrayList<>();
        pauseScreenButtons = new ArrayList<>();
        initializeStartScreenButtons();
        initializeEndScreenButtons();
        initializePauseScreenButtons();
        health = new JProgressBar(0, 100);
        health.setStringPainted(true);
        health.setForeground(Color.GREEN);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.BLACK);
        switch (gp.gameState) {
            case START -> drawStartScreen();
            case FINISHED -> drawGameEndScreen();
            case PAUSED -> drawPauseScreen();
            case RUNNING -> drawPlayerHealthBar();
        }
    }

    private void drawStartScreen() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(arial_80B);
        String title = "2D Game";
        int x = getXforCenteredText(title);
        int y = gp.getScreenHeight() / 4;
        g2.drawString(title, x, y);

        for (Button button : startScreenButtons) {
            button.draw(g2);
        }
    }

    private void drawGameEndScreen() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        g2.setColor(Color.RED);
        g2.setFont(arial_80B);
        String gameOverText = "GAME OVER";
        int x = getXforCenteredText(gameOverText);
        int y = gp.getScreenHeight() / 4;
        g2.drawString(gameOverText, x, y);

        for (Button button : endScreenButtons) {
            button.draw(g2);
        }
    }

    public void handleStartScreenClick(Point p) {
        for (int i = 0; i < startScreenButtons.size(); i++) {
            if (startScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> {
                        gp.gameState = GamePanel.GameState.RUNNING;
                        gp.setupGame();
                    }
                    case 1 -> {
                        if (gp.loadGame()) {
                            gp.gameState = GamePanel.GameState.RUNNING;
                        } else {
                            // If load fails, start a new game
                            gp.setupGame();
                            gp.gameState = GamePanel.GameState.RUNNING;
                        }
                    }
                    case 2 -> System.exit(0);
                }
                break;
            }
        }
    }

    public void handleGameOverClick(Point p) {
        for (int i = 0; i < endScreenButtons.size(); i++) {
            if (endScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> {
                        gp.resetGame();
                        gp.gameState = GamePanel.GameState.RUNNING;
                    }
                    case 1 -> {
                        if (gp.loadGame()) {
                            gp.gameState = GamePanel.GameState.RUNNING;
                        } else {
                            // If load fails, start a new game
                            gp.resetGame();
                            gp.gameState = GamePanel.GameState.RUNNING;
                        }
                    }
                    case 2 -> System.exit(0);
                }
                break;
            }
        }
    }

    public void handlePauseScreenClick(Point p) {
        for (int i = 0; i < pauseScreenButtons.size(); i++) {
            if (pauseScreenButtons.get(i).contains(p)) {
                switch (i) {
                    case 0 -> gp.gameState = GamePanel.GameState.RUNNING;
                    case 1 -> gp.saveGame();
                    case 2 -> {
                        if (gp.loadGame())
                            gp.gameState = GamePanel.GameState.RUNNING;
                    }
                    case 3 -> System.exit(0);
                }
                break;
            }
        }
    }

    public void handleHover(Point p) {
        ArrayList<Button> hoverButtons = new ArrayList<>();
        switch (gp.gameState) {
            case START -> hoverButtons.addAll(startScreenButtons);
            case PAUSED -> hoverButtons.addAll(pauseScreenButtons);
            case FINISHED -> hoverButtons.addAll(endScreenButtons);
        }
        for (Button button : hoverButtons) {
            if (button.contains(p))
                button.setBackgroundColor(new Color(100, 100, 100));
            else
                button.setBackgroundColor(new Color(70, 70, 70));
        }
    }

    private void initializeStartScreenButtons() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int startY = gp.getScreenHeight() / 2;

        startScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY, buttonWidth, buttonHeight, "Start Game"));
        startScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Load Game"));
        startScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Quit"));
    }

    private void initializeEndScreenButtons() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int startY = gp.getScreenHeight() / 2;

        endScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY, buttonWidth, buttonHeight, "New Game"));
        endScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Load Game"));
        endScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit"));
    }

    private void initializePauseScreenButtons() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int startY = gp.getScreenHeight() / 2 - (2 * buttonHeight + 30);

        pauseScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY, buttonWidth, buttonHeight, "Resume"));
        pauseScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY + buttonHeight + 20, buttonWidth, buttonHeight, "Save Game"));
        pauseScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY + 2 * (buttonHeight + 20), buttonWidth, buttonHeight, "Load Game"));
        pauseScreenButtons.add(new Button(gp.getScreenWidth()/2 - buttonWidth/2, startY + 3 * (buttonHeight + 20), buttonWidth, buttonHeight, "Exit"));
    }

    private void drawPauseScreen() {
        // Draw a semi-transparent black overlay
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(arial_80B);
        String pauseText = "PAUSED";
        int x = getXforCenteredText(pauseText);
        int y = gp.getScreenHeight() / 4;
        g2.drawString(pauseText, x, y);

        for (Button button : pauseScreenButtons) {
            button.draw(g2);
        }
    }

    private int getXforCenteredText(String text){
        int length =(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        return gp.getScreenWidth()/2-length/2;
    }

    private void drawPlayerHealthBar() {
        int x = gp.getTileSize();
        int y = gp.getTileSize();
        int width = 200;
        int height = 20;

        // Draw black background
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, width, height);

        // Calculate the width of the red health bar
        int healthBarWidth = (int) ((gp.player.getHealth() / 100.0) * width);

        // Draw red health bar
        g2.setColor(Color.RED);
        g2.fillRect(x, y, healthBarWidth, height);

        // Draw white border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, width, height);

        // Draw HP text
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        String hpText = gp.player.getHealth() + "/100 HP";
        int textX = x + width / 2 - g2.getFontMetrics().stringWidth(hpText) / 2;
        int textY = y - 5;
        g2.drawString(hpText, textX, textY);
    }

}
