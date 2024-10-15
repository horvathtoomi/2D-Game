package main;

import javax.swing.*;
import java.awt.*;

public class UserInterface {
    GamePanel gp;
    Graphics2D g2;
    Font font;

    public UserInterface(GamePanel gp) {
        this.gp = gp;
        font = new Font("Times New Roman", Font.PLAIN, 40);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(font);
        g2.setColor(Color.BLACK);
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
        Font menuFont = new Font("Arial", Font.PLAIN, 60);
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

}
