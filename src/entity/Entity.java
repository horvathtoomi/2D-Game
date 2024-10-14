package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Entity {
    public int worldX,worldY;
    public int speed, health;
    public int actionLockCounter;
    public BufferedImage right,left,up,down;
    public String direction;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    GamePanel gp;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void setAction(){}

    public void update(){
        setAction();
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this,false);
        gp.cChecker.checkPlayer(this);
        if(!collisionOn){
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

    public void draw(Graphics2D g2){
        BufferedImage image;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;
            if(worldX+gp.tileSize > gp.player.worldX - gp.player.screenX && worldX-gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY+gp.tileSize > gp.player.worldY - gp.player.screenY && worldY-gp.tileSize < gp.player.worldY + gp.player.screenY)
            {
                image = switch (direction) {
                    case "up" -> up;
                    case "down" -> down;
                    case "left" -> left;
                    case "right" -> right;
                    default -> null;
                };
                g2.drawImage(image,screenX,screenY,gp.tileSize,gp.tileSize,null);
            }

    }

    public BufferedImage scale(String folderName, String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage bufim = null;
        try{
            bufim = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(folderName + "/" + imageName + ".png")));
            bufim = uTool.scaleImage(bufim, gp.tileSize, gp.tileSize);

        }catch(IOException e){e.getCause();}
        return bufim;
    }

}
