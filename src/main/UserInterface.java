package main;

import object.OBJ_Key;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class UserInterface {
    GamePanel gp;
    Graphics2D g2;
    Font font;
    public boolean died = false;
    public boolean gameFinished =false;
    double playTime;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public UserInterface(GamePanel gp) {
        this.gp = gp;
        font = new Font("Times New Roman", Font.PLAIN, 40);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(font);
        g2.setColor(Color.BLACK);
        if(gp.gameState==gp.playState){
            //Do playstate stuff later
        }
        if(gp.gameState == gp.pauseState){
            drawPauseScreen(g2);
        }
    }

    public void drawPauseScreen(Graphics2D g2) {
        String text = "PAUSED";
        int y = gp.screenHeight/2;
        g2.drawString(text,getXforCenteredText(text),y);
    }

    public int getXforCenteredText(String text){
        int length =(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int x = gp.screenWidth/2-length/2;
        return x;
    }

}
