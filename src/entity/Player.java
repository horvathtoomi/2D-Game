package entity;

import main.InputHandler;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    InputHandler kezelo;

    public Player(GamePanel panel, InputHandler kezelo) {
        super(panel);
        this.kezelo = kezelo;
        setHealth(100);
        setScreenX(gp.getScreenWidth()/2 - (gp.getTileSize()/2));
        setScreenY(gp.getScreenHeight()/2 - (gp.getTileSize()/2));
        solidArea = new Rectangle(8,16,32,32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        setWorldX(gp.getTileSize() * 23);
        setWorldY(gp.getTileSize() * 21);
        setSpeed(3);
        direction = "down";
    }

    public void getPlayerImage(){
        right = scale("player","right");
        left = scale("player","left");
        down = scale("player","down");
        up = scale("player","up");
    }

   // @Override
    public void update(){
        if(kezelo.upPressed||kezelo.downPressed||kezelo.leftPressed||kezelo.rightPressed) {
            if (kezelo.upPressed) direction = "up";
            if (kezelo.downPressed) direction = "down";
            if (kezelo.leftPressed) direction = "left";
            if (kezelo.rightPressed) direction = "right";

            //Check Tile Collision
            collisionOn = false;
            gp.cChecker.checkTile(this);

            //Check Object Colllision
            int objIndex = gp.cChecker.checkObject(this,true);
            pickUpObject(objIndex);

            //Check npc collision
            int npcIndex = gp.cChecker.checkEntity(this,gp.entities);
            interractNPC(npcIndex);

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> setWorldY(getWorldY()-getSpeed());
                    case "down"-> setWorldY(getWorldY()+getSpeed());
                    case "left" -> setWorldX(getWorldX()-getSpeed());
                    case "right" -> setWorldX(getWorldX()+getSpeed());
                }
            }
        }
    }


    public void pickUpObject(int index) {
        //if(index!=999){
        //}
    }

    public void interractNPC(int idx){
        //if(idx!=999){
           //System.out.println("interaction w an NPC!");
        //}
    }

    //@Override
    public void draw(Graphics2D g2){
        BufferedImage image = switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> null;
        };
        int x = getScreenX();
        int y = getScreenY();
        if(getScreenX() > getWorldX()){
            x = getWorldX();
        }
        if(getScreenY() > getWorldY()){
            y = getWorldY();
        }
        /*
        int rightOffset = gp.getScreenWidth() - getScreenX();
        if(rightOffset > gp.getWorldWidth() - getWorldX()){
            x = gp.getScreenWidth() - gp.getWorldWidth() - getWorldX();
        }
        int bottomOffset = gp.getScreenHeight() - getScreenY();
        if(bottomOffset > gp.getWorldHeight() - getWorldY()){
            y = gp.getScreenHeight() - gp.getWorldHeight() - getWorldY();
        }
        */
        int rightOffset = gp.getScreenWidth() - getScreenX();
        if (rightOffset > gp.getWorldWidth() - getWorldX()) {
            x = gp.getScreenWidth() - (gp.getWorldWidth() - getWorldX());
        }
        int bottomOffset = gp.getScreenHeight() - getScreenY();
        if (bottomOffset > gp.getWorldHeight() - getWorldY()) {
            y = gp.getScreenHeight() - (gp.getWorldHeight() - getWorldY());
        }
        g2.drawImage(image,x,y,null);
    }

}
