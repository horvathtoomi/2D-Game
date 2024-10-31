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
    public boolean collision = false;
    public boolean opened = false;
    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    protected SuperObject(GamePanel gp, int x, int y, String name, String imageName) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.name = name;
        try {
            image1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/" + imageName +".png")));
        } catch(IOException e) {
            GameLogger.error("[SUPER OBJECT]", "Failed to get image: " + e.getMessage(), e);
        }
        image = image1;
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.getWorldX() + gp.player.getScreenX();
        int screenY = worldY - gp.player.getWorldY() + gp.player.getScreenY();
        if(worldX+gp.getTileSize() > gp.player.getWorldX() - gp.player.getScreenX() && worldX-gp.getTileSize() < gp.player.getWorldX() + gp.player.getScreenX() &&
                worldY+gp.getTileSize() > gp.player.getWorldY() - gp.player.getScreenY() && worldY-gp.getTileSize() < gp.player.getWorldY() + gp.player.getScreenY())
        {
            g2.drawImage(image,screenX,screenY,gp.getTileSize(),gp.getTileSize(),null);
        }
    }

    protected BufferedImage scale(String filename){
        BufferedImage ima = null;
        try {
            ima = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/" + filename +".png")));
        } catch(IOException e) {
            GameLogger.error("[SUPER OBJECT]", "Failed to scale image: " + e.getMessage(), e);
        }
        return ima;
    }

    public void update() {}

}
