package entity;

import main.GamePanel;
import main.UtilityTool;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Entity{
    String name;
    private int worldX, worldY;
    private int screenX, screenY;
    private int speed, health;
    public int actionLockCounter;
    public BufferedImage right,left,up,down;
    public String direction;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    GamePanel gp;

    public int getWorldX() {return worldX;}
    public int getWorldY() {return worldY;}
    public int getScreenX() {return screenX;}
    public int getScreenY() {return screenY;}
    public int getSpeed() {return speed;}
    public int getHealth() {return health;}

    public void setWorldX(int a) {worldX = a;}
    public void setWorldY(int a) {worldY = a;}
    public void setScreenX(int a) {screenX = a;}
    public void setScreenY(int a) {screenY = a;}
    public void setSpeed(int a) {speed = a;}
    public void setHealth(int a) {health = a;}


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
            int screenX = worldX - gp.player.getWorldX() + gp.player.getScreenX();
            int screenY = worldY - gp.player.getWorldY() + gp.player.getScreenY();
            if(worldX+gp.getTileSize() > gp.player.getWorldX() - gp.player.getScreenX() && worldX-gp.getTileSize() < gp.player.getWorldX() + gp.player.getScreenX() &&
                    worldY+gp.getTileSize() > gp.player.getWorldY() - gp.player.getScreenY() && worldY-gp.getTileSize() < gp.player.getWorldY() + gp.player.getScreenY())
            {
                image = switch (direction) {
                    case "up" -> up;
                    case "down" -> down;
                    case "left" -> left;
                    case "right" -> right;
                    default -> null;
                };
                g2.drawImage(image,screenX,screenY,gp.getTileSize(),gp.getTileSize(),null);
            }

    }

    public BufferedImage scale(String folderName, String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage bufim = null;
        try{
            bufim = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(folderName + "/" + imageName + ".png")));
            bufim = uTool.scaleImage(bufim, gp.getTileSize(), gp.getTileSize());
        }catch(IOException e){
            e.getCause();}
        return bufim;
    }

}
