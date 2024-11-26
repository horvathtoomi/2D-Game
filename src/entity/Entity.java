package entity;

import main.Engine;
import main.UtilityTool;
import main.logger.GameLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * A játék alapvető entitás osztálya, minden mozgó játékelem ősosztálya.
 * Tartalmazza az általános tulajdonságokat és metódusokat, amiket minden entitás használ.
 */
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
    public Engine eng;
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
    public void setWorldX(int a) {worldX = Math.max(a,0);}
    public void setWorldY(int a) {worldY = Math.max(a,0);}
    public void setScreenX(int a) {screenX = a;}
    public void setScreenY(int a) {screenY = a;}
    public void setSpeed(int a) {speed = a;}
    public void setHealth(int a) {health = a;}
    public void setMaxHealth(int a) {maxHealth = a;}

    public void dealDamage(int damage){
        setHealth(Math.max(getHealth() - damage, 0));
    }

    /**
     * Létrehoz egy új entitást.
     * @param eng a játékmotor példánya
     */
    public Entity(Engine eng) {
        this.eng = eng;
        width = eng.getTileSize();
        height = eng.getTileSize();
    }

    /**
     * Az entitás cselekvésének meghatározása.
     * Az alosztályok felülírhatják saját viselkedés implementálásához.
     */
    public void setAction(){}

    public void update(){
        setAction();
        collisionOn = false;
        eng.cChecker.checkTile(this);
        eng.cChecker.checkObject(this,false);
        eng.cChecker.checkPlayer(this);
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
        int screenX = getWorldX() - eng.player.getWorldX() + eng.player.getScreenX();
        int screenY = getWorldY() - eng.player.getWorldY() + eng.player.getScreenY();

        screenX = adjustScreenX(screenX);
        screenY = adjustScreenY(screenY);

        if (isValidScreenXY(screenX, screenY)) {
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }

    protected boolean isValidScreenXY(int screenX, int screenY){
        return screenX > -eng.getTileSize() && screenX < eng.getScreenWidth() + eng.getTileSize() && screenY > -eng.getTileSize() && screenY < eng.getScreenHeight() + eng.getTileSize();
    }

    protected int adjustScreenY(int screenY){
        if (eng.player.getScreenY() > eng.player.getWorldY()) {
            screenY = getWorldY();
        }
        int bottomOffset = eng.getScreenHeight() - eng.player.getScreenY();
        if (bottomOffset > eng.getWorldHeight() - eng.player.getWorldY()) {
            screenY = eng.getScreenHeight() - (eng.getWorldHeight() - getWorldY());
        }
        return screenY;
    }

    protected int adjustScreenX(int screenX){
        if (eng.player.getScreenX() > eng.player.getWorldX()) {
            screenX = getWorldX();
        }
        int rightOffset = eng.getScreenWidth() - eng.player.getScreenX();
        if (rightOffset > eng.getWorldWidth() - eng.player.getWorldX()) {
            screenX = eng.getScreenWidth() - (eng.getWorldWidth() - getWorldX());
        }
        return screenX;
    }

    public BufferedImage scale(String folderName, String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage bufim = null;
        try{
            bufim = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(folderName + "/" + imageName + ".png")));
            bufim = uTool.scaleImage(bufim, eng.getTileSize(), eng.getTileSize());
        }catch(IOException e){
            GameLogger.error("[ENTITY]", "Failed to load image: " + e.getMessage(), e);
        }
        return bufim;
    }

}
