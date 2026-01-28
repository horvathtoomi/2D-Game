package entity;

import main.Engine;
import main.UtilityTool;
import main.logger.GameLogger;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A játék alapvető entitás osztálya, minden mozgó játékelem ősosztálya.
 * Tartalmazza az általános tulajdonságokat és metódusokat, amiket minden entitás használ.
 */
public class Entity{

    private final String LOG_CONTEXT = "[ENTITY]";

    public String name;
    private int worldX, worldY;
    private int screenX, screenY;
    private int width;
    private int height;
    private int speed, health;
    public int actionLockCounter;
    public BufferedImage right,left,up,down,shoot;
    public Direction direction = Direction.DOWN;
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
                case UP:
                    worldY -= speed;
                    break;
                case DOWN:
                    worldY += speed;
                    break;
                case LEFT:
                    worldX -= speed;
                    break;
                case RIGHT:
                    worldX += speed;
                    break;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            case SHOOT -> shoot;
        };
        screenX = worldX - eng.camera.getX();
        screenY = worldY - eng.camera.getY();
        if (isOnScreen(screenX, screenY)) {
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }

    protected boolean isOnScreen(int x, int y) {
        return x + width > 0 &&
                x < eng.getScreenWidth() &&
                y + height > 0 &&
                y < eng.getScreenHeight();
    }



    public BufferedImage scale(String folderName, String imageName){
        UtilityTool uTool = new UtilityTool();
        BufferedImage bufim = null;
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream(folderName + "/" + imageName + ".png");
            if (inputStream == null) {
                GameLogger.error(LOG_CONTEXT, "resource inputStream is null", new IOException());
                return null;
            }
            bufim = ImageIO.read(inputStream);
            bufim = uTool.scaleImage(bufim, eng.getTileSize(), eng.getTileSize());
        } catch(IOException e) {
            GameLogger.error(LOG_CONTEXT, "Failed to load image: " + e.getMessage(), e);
        }
        return bufim;
    }
}