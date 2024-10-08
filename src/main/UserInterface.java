package main;

import object.OBJ_Key;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class UserInterface {
    GamePanel gp;
    Font font;
    BufferedImage img;
    public boolean died = false;
    public boolean gameFinished =false;
    double playTime;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public UserInterface(GamePanel gp) {
        this.gp = gp;
        font = new Font("Times New Roman", Font.PLAIN, 25);
        OBJ_Key key = new OBJ_Key(gp,0,0);
        img = key.image;
    }

    public void draw(Graphics2D g2) {
        if (gameFinished) {
            g2.setFont(font);
            g2.setColor(Color.white);

            String text;
            int textLength;
            int x,y;

            text="You won!";
            textLength=(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
            x= gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 - gp.tileSize*3;
            g2.drawString(text,x,y);

            text="Your time: " + decimalFormat.format(playTime);
            textLength=(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
            x= gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 + gp.tileSize*3;
            g2.drawString(text,x,y);
        }
        else if(died){
                g2.setFont(font);
                g2.setColor(Color.white);

                String text;
                int textLength;
                int x,y;

                text="You Died!";
                textLength=(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
                x= gp.screenWidth/2 - textLength/2;
                y = gp.screenHeight/2 - gp.tileSize*3;
                g2.drawString(text,x,y);

                text="Playtime: " + decimalFormat.format(playTime);
                textLength=(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
                x= gp.screenWidth/2 - textLength/2;
                y = gp.screenHeight/2 + gp.tileSize*3;
                g2.drawString(text,x,y);
        }
        else {
            g2.setFont(font);
            g2.setColor(Color.black);
            g2.drawImage(img, gp.tileSize / 2, gp.tileSize / 2, gp.tileSize, gp.tileSize, null);
            g2.drawString("x " + gp.player.hasKeys, 74, 53);

            //TIME
            playTime +=(double)1/60;
            g2.drawString("Your time: " + decimalFormat.format(playTime), gp.tileSize*12+gp.tileSize/2,53);
        }
    }
}
