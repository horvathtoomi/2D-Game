package entity;

import main.GamePanel;
import main.UtilityTool;
import object.EnemyTestAttack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EnemyTest extends Entity{

    GamePanel gp;
    BufferedImage shot;
    public int screenX;
    public int screenY;
    int directionChanger = 0;

    public EnemyTest(GamePanel panel, Player player) {
        this.gp = panel;
        solidArea = new Rectangle();
        solidArea.x=8;
        solidArea.y=16;
        solidArea.width=32;
        solidArea.height=32;
        try {
            getEnemyTestImage();
        }catch (Exception e){
            System.out.println("getEntityTestImage() is not working");
        }
        screenX=gp.tileSize*21;
        screenY=gp.tileSize* 23;
        worldX=screenX;
        worldY=screenY;
        this.speed=player.speed;
        direction = "left";
    }

    public void getEnemyTestImage(){
            right = scale(gp,"EnemyTest","right");
            left = scale(gp,"EnemyTest","left");
            shot = scale(gp,"EnemyTest","shot");
    }

    public void update(){
        if(directionChanger>60) {
            if (direction.equals("right")) {
                direction = "left";
            } else if (direction.equals("left")) {
                direction = "right";
            }
            shoot();
            directionChanger = 0;
        }
        collisionOn = false;
        gp.cChecker.checkTile(this);
        if (!collisionOn) {
            switch (direction) {
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            }
        }
        directionChanger++;
    }

    public void shoot(){
        manuallySetObject(gp,new EnemyTestAttack(gp,worldX,worldY));
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch(direction){
            case "shot":
                image = shot;
                break;
            case "left":
                image = left;
                break;
            case "right":
                image = right;
                break;
        }
        g2.drawImage(image,worldX,worldY, gp.tileSize, gp.tileSize, null);
    }
}
