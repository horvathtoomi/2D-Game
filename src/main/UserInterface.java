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

    public UserInterface(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.BLACK);
        drawPlayerHealthBar();
        if(gp.gameState == GamePanel.GameState.START){
            drawMenu();
        }
        if(gp.gameState == GamePanel.GameState.RUNNING){
            JProgressBar health = new JProgressBar(0, 100);
            health.setStringPainted(true);
            health.setForeground(Color.GREEN);
        }
        if(gp.gameState == GamePanel.GameState.PAUSED){
            drawPauseScreen(g2);
        }
    }

    public void drawMenu() {
        String text1 = "Stay Alive!";
        String text2 = "Press ENTER to begin";
        String text3 = "Press L to load game";
        g2.drawString(text1, getXforCenteredText(text1), gp.screenHeight/2);
        g2.drawString(text2, getXforCenteredText(text2), gp.screenHeight/2 + gp.screenHeight/4);
        g2.drawString(text3, getXforCenteredText(text3), gp.screenHeight/2 + gp.screenHeight/3);
    }

    public void drawPauseScreen(Graphics2D g2) {
        String text = "PAUSED";
        int y = gp.screenHeight/2;
        g2.drawString(text,getXforCenteredText(text),y);
    }

    public int getXforCenteredText(String text){
        int length =(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        return gp.screenWidth/2-length/2;
    }

    public void drawPlayerHealthBar() {
        int x = gp.tileSize;
        int y = gp.tileSize;
        int width = 200;
        int height = 20;

        // Draw black background
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, width, height);

        // Calculate the width of the red health bar
        int healthBarWidth = (int) ((gp.player.health / 100.0) * width);

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
        String hpText = gp.player.health + "/100 HP";
        int textX = x + width / 2 - g2.getFontMetrics().stringWidth(hpText) / 2;
        int textY = y - 5;
        g2.drawString(hpText, textX, textY);
    }

}
