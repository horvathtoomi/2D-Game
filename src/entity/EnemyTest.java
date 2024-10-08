package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EnemyTest extends Entity{

    GamePanel gp;
    BufferedImage shot;
    public int screenX;
    public int screenY;

    public EnemyTest(GamePanel panel, Player player) {
        this.gp = panel;
        screenX=gp.screenWidth/2 - (gp.tileSize/2);
        screenY=gp.screenHeight/2 - (gp.tileSize/2);

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
        worldX=gp.tileSize* 23;
        worldY=gp.tileSize*21;
        this.speed=player.speed;
        direction = "left";
    }

    public void getEnemyTestImage(){
            right = scale("right");
            left = scale("left");
            shot = scale("shot");
    }

    public BufferedImage scale(String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage bufim = null;
        try{
            bufim = ImageIO.read(getClass().getClassLoader().getResourceAsStream("EnemyTest/"+ imageName +".png"));
            bufim = uTool.scaleImage(bufim, gp.tileSize, gp.tileSize);
        }catch(IOException e){e.printStackTrace();}
        return bufim;
    }

    public void update(){
            //Check Tile Collision
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
                    case "shot":
                        shoot();
                        break;
                }
            }
    }

    public void shoot(){
        gp.aSetter.setObject("EnemyTestAttack",worldX,worldY);
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
        g2.drawImage(image,screenX,screenY, gp.tileSize, gp.tileSize, null);
    }
}
