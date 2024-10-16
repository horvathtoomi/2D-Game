package object;

import java.awt.*;
import java.awt.image.BufferedImage;
import main.GamePanel;
import main.UtilityTool;

public abstract class SuperObject {
    public GamePanel gp;
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    UtilityTool uTool = new UtilityTool();

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.getWorldX() + gp.player.getScreenX();
        int screenY = worldY - gp.player.getWorldY() + gp.player.getScreenY();
        if(worldX+gp.getTileSize() > gp.player.getWorldX() - gp.player.getScreenX() && worldX-gp.getTileSize() < gp.player.getWorldX() + gp.player.getScreenX() &&
                worldY+gp.getTileSize() > gp.player.getWorldY() - gp.player.getScreenY() && worldY-gp.getTileSize() < gp.player.getWorldY() + gp.player.getScreenY())
        {
            g2.drawImage(image,screenX,screenY,gp.getTileSize(),gp.getTileSize(),null);
        }
    }

    public void update() {}

}
