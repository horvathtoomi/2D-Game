package entity;

import main.InputHandler;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity{
    InputHandler kezelo;
    public final int screenX;
    public final int screenY;


    public Player(GamePanel panel, InputHandler kezelo) {

        super(panel);
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
        }catch (Exception e){System.out.println("getPlayerImage() is not working");}

    }

    public void setDefaultValues(){
        worldX=gp.tileSize* 23;
        worldY=gp.tileSize*21;
        speed=3;
        direction = "down";
    }

    public void getPlayerImage(){
        right = scale("player","jobbra");
        left = scale("player","balra");
        down = scale("player","le");
        up = scale("player","fel");
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

            //Check npc collision
            int npcIndex = gp.cChecker.checkEntity(this,gp.entities);
            interractNPC(npcIndex);

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


    public void pickUpObject(int index) {
        //if(index!=999){
        //}
    }

    public void interractNPC(int idx){
        //if(idx!=999){
           //System.out.println("interaction w an NPC!");
        //}
    }

    public void draw(Graphics2D g2){
        BufferedImage image = switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> null;
        };

        int x = screenX;
        int y = screenY;
        if(screenX > worldX){
            x = worldX;
        }
        if(screenY > worldY){
            y = worldY;
        }
        int rightOffset = gp.screenWidth - screenX;
        if(rightOffset > gp.worldWidth - worldX){
            x = gp.screenWidth - gp.worldWidth - worldX;
        }
        int bottomOffset = gp.screenHeight - screenY;
        if(bottomOffset > gp.worldHeight - worldY){
            y = gp.screenHeight - gp.worldHeight - worldY;
        }
        g2.drawImage(image,x,y,null);
    }

}
