package main;

import javax.swing.*;
import java.awt.*;

public class UserInterface {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    JProgressBar health;

    public UserInterface(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
        health = new JProgressBar(0, 100);
        health.setStringPainted(true);
        health.setForeground(Color.GREEN);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.BLACK);
        switch (gp.gameState) {
            case GamePanel.GameState.START -> drawMenu();
            case GamePanel.GameState.FINISHED -> drawEndScreen();
            case GamePanel.GameState.PAUSED -> drawPauseScreen();
            case GamePanel.GameState.RUNNING -> drawPlayerHealthBar();
        }
    }

    private void drawMenu() {
        String text1 = "Stay Alive!";
        String text2 = "Press ENTER to begin";
        String text3 = "Press L to load game";
        g2.drawString(text1, getXforCenteredText(text1), gp.getScreenHeight()/2);
        g2.drawString(text2, getXforCenteredText(text2), gp.getScreenHeight()/2 + gp.getScreenHeight()/4);
        g2.drawString(text3, getXforCenteredText(text3), gp.getScreenHeight()/2 + gp.getScreenHeight()/3);
    }

    private void drawPauseScreen() {
        String text = "PAUSED";
        int y = gp.getScreenHeight()/2;
        g2.drawString(text,getXforCenteredText(text),y);
    }

    private void drawEndScreen() {
        String text = "You Died!";
        int y = gp.getScreenHeight()/2;
        g2.drawString(text,getXforCenteredText(text),y);
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
