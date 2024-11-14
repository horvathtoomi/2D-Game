package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import main.Engine;
import main.UtilityTool;
import main.logger.GameLogger;

public class Entity{
    public String name;
    private int worldX, worldY;
    private int screenX, screenY;
    private int width;
    private int height;
    private int speed, health;
    public int actionLockCounter;
    public BufferedImage right,left,up,down,shoot;
    public String direction;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public Engine gp;
    protected int maxHealth;

    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public int getWorldX() {return worldX;}
    public int getWorldY() {return worldY;}
    public int getScreenX() {return screenX;}
    public int getScreenY() {return screenY;}
    public int getSpeed() {return speed;}
    public int getHealth() {return health;}
    public String getName(){return name;}
    public int getMaxHealth(){return maxHealth;}

    public void setWidth(int a) {width=a;}
    public void setHeight(int a) {height=a;}
    public void setWorldX(int a) {worldX = a;}
    public void setWorldY(int a) {worldY = a;}
    public void setScreenX(int a) {screenX = a;}
    public void setScreenY(int a) {screenY = a;}
    public void setSpeed(int a) {speed = a;}
    public void setHealth(int a) {health = a;}
    public void setMaxHealth(int a) {maxHealth = a;}

    public Entity(Engine gp) {
        this.gp = gp;
        width = gp.getTileSize();
        height = gp.getTileSize();
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

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            case "shoot" -> shoot;
            default -> null;
        };
        int screenX = getWorldX() - gp.player.getWorldX() + gp.player.getScreenX();
        int screenY = getWorldY() - gp.player.getWorldY() + gp.player.getScreenY();

        screenX = adjustScreenX(screenX);
        screenY = adjustScreenY(screenY);

        if (isValidScreenXY(screenX, screenY)) {
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }

    protected boolean isValidScreenXY(int screenX, int screenY){
        return screenX > -gp.getTileSize() && screenX < gp.getScreenWidth() + gp.getTileSize() && screenY > -gp.getTileSize() && screenY < gp.getScreenHeight() + gp.getTileSize();
    }

    protected int adjustScreenY(int screenY){
        if (gp.player.getScreenY() > gp.player.getWorldY()) {
            screenY = getWorldY();
        }
        int bottomOffset = gp.getScreenHeight() - gp.player.getScreenY();
        if (bottomOffset > gp.getWorldHeight() - gp.player.getWorldY()) {
            screenY = gp.getScreenHeight() - (gp.getWorldHeight() - getWorldY());
        }
        return screenY;
    }

    protected int adjustScreenX(int screenX){
        if (gp.player.getScreenX() > gp.player.getWorldX()) {
            screenX = getWorldX();
        }
        int rightOffset = gp.getScreenWidth() - gp.player.getScreenX();
        if (rightOffset > gp.getWorldWidth() - gp.player.getWorldX()) {
            screenX = gp.getScreenWidth() - (gp.getWorldWidth() - getWorldX());
        }
        return screenX;
    }

    public BufferedImage scale(String folderName, String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage bufim = null;
        try{
            bufim = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(folderName + "/" + imageName + ".png")));
            bufim = uTool.scaleImage(bufim, gp.getTileSize(), gp.getTileSize());
        }catch(IOException e){
            GameLogger.error("[ENTITY]", "Failed to load image: " + e.getMessage(), e);
        }
        return bufim;
    }

}
