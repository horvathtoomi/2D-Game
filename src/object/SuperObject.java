package object;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import main.GamePanel;
import main.logger.GameLogger;

import javax.imageio.ImageIO;

public abstract class SuperObject{
    public GamePanel gp;
    public BufferedImage image, image1, image2;
    public String name;

    private final int MAX_DURABILITY;
    private int durability;
    public int usageDamage = 0;

    public boolean collision = false;
    public boolean opened = false;
    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    private static final String LOG_CONTEXT = "[SUPER OBJECT]";


    public int getWorldX(){return worldX;}
    public int getWorldY(){return worldY;}
    public int getMaxDurability(){return MAX_DURABILITY;}
    public int getDurability(){return durability;}

    public void setWorldX(int x){worldX = x;}
    public void setWorldY(int y){worldY = y;}
    public void setDurability(int a){durability = a;}
    public void setUsageDamage(int a){usageDamage = a;}

    protected SuperObject(GamePanel gp, int x, int y, String name, String imageName) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.name = name;
        try {
            image1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/" + imageName +".png")));
        } catch(IOException e) {
            GameLogger.error(LOG_CONTEXT, "Failed to get image: " + e.getMessage(), e);
        }
        image = image1;
        MAX_DURABILITY = 60 * gp.getFPS();
        durability = MAX_DURABILITY;
    }

    public void use(){}

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.getWorldX() + gp.player.getScreenX();
        int screenY = worldY - gp.player.getWorldY() + gp.player.getScreenY();

        if (gp.player.getScreenX() > gp.player.getWorldX()) {
            screenX = worldX;
        }
        if (gp.player.getScreenY() > gp.player.getWorldY()) {
            screenY = worldY;
        }
        int rightOffset = gp.getScreenWidth() - gp.player.getScreenX();
        if (rightOffset > gp.getWorldWidth() - gp.player.getWorldX()) {
            screenX = gp.getScreenWidth() - (gp.getWorldWidth() - worldX);
        }
        int bottomOffset = gp.getScreenHeight() - gp.player.getScreenY();
        if (bottomOffset > gp.getWorldHeight() - gp.player.getWorldY()) {
            screenY = gp.getScreenHeight() - (gp.getWorldHeight() - worldY);
        }

        if (screenX > -gp.getTileSize() && screenX < gp.getScreenWidth() + gp.getTileSize() && screenY > -gp.getTileSize() && screenY < gp.getScreenHeight() + gp.getTileSize()) {
            g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
        }
    }

    protected BufferedImage scale(String filename){
        BufferedImage ima = null;
        try {
            ima = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/" + filename +".png")));
        } catch(IOException e) {
            GameLogger.error(LOG_CONTEXT, "Failed to scale image: " + e.getMessage(), e);
        }
        return ima;
    }

    public void update() {}

}
