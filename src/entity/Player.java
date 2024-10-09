package entity;

import main.InputHandler;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity{
    GamePanel gp;
    InputHandler kezelo;
    public final int screenX;
    public final int screenY;
    public int hasKeys = 0;

    public Player(GamePanel panel, InputHandler kezelo) {
        this.gp = panel;
        this.kezelo = kezelo;
        health = 100;
        screenX=gp.screenWidth/2 - (gp.tileSize/2);
        screenY=gp.screenHeight/2 - (gp.tileSize/2);

        solidArea = new Rectangle();
        solidArea.x=8;
        solidArea.y=16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width=32;
        solidArea.height=32;
        setDefaultValues();
        try {
            getPlayerImage();
        }catch (Exception e){
            System.out.println("getPlayerImage() is not working");
        }
    }

    public void setDefaultValues(){
        worldX=gp.tileSize* 23;
        worldY=gp.tileSize*21;
        speed=3;
        direction = "down";
    }

    public void getPlayerImage(){
        right = scale(gp,"player","jobbra");
        left = scale(gp,"player","balra");
        down = scale(gp,"player","le");
        up = scale(gp,"player","fel");
    }

    public void update(){
        if(kezelo.upPressed||kezelo.downPressed||kezelo.leftPressed||kezelo.rightPressed) {
            if (kezelo.upPressed)
                direction = "up";
            if (kezelo.downPressed)
                direction = "down";
            if (kezelo.leftPressed)
                direction = "left";
            if (kezelo.rightPressed)
                direction = "right";

            //Check Tile Collision
            collisionOn = false;
            gp.cChecker.checkTile(this);

            //Check Object Colllision
            int objIndex = gp.cChecker.checkObject(this,true);
            pickUpObject(objIndex);

            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }
        }
    }

    public void pickUpObject(int index){
        if(index!=999){
            String objectName = gp.aSetter.lista.get(index).name;
            switch (objectName){
                case "key":
                    hasKeys++;
                    gp.aSetter.lista.set(index,null);
                    break;
                case "door":
                    if(hasKeys > 0) {
                        gp.aSetter.lista.set(index,null);
                        hasKeys--;
                    }
                    break;
                case "chest":
                    gp.ui.gameFinished=true;
                    break;
                case "boots":
                    speed += speed/3;
                    gp.aSetter.lista.set(index,null);
                    break;
                case "EnemyTestAttack" :
                    System.out.println("Before: " + health);
                    health-=50;
                    gp.aSetter.lista.set(index,null);
                    System.out.println("After: " + health);
                    System.out.println("----------");
                    if(health<=0) {
                        gp.ui.died = true;
                        speed=0;
                        up = down = left = right = null;
                        gp.killEntitys();
                    }
                    break;
            }
        }
    }

    public void draw(Graphics2D g2){
        BufferedImage image = switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> null;
        };
        g2.drawImage(image,screenX,screenY,null);
    }

}
