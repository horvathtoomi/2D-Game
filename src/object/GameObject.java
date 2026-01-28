package object;

import main.Engine;
import main.UtilityTool;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    protected final Engine eng;
    public int worldX, worldY;
    protected BufferedImage image;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    public boolean collision = false;
    public String name;

    private static final String LOG_CONTEXT = "[GAME OBJECT]";

    protected GameObject(Engine eng, int x, int y, String imageName) {
        this.eng = eng;
        this.worldX = x;
        this.worldY = y;
        this.name = imageName; // Default name to image name
        image = UtilityTool.getImage("object", imageName);
    }

    public void update() {
    }

    public void draw(Graphics2D g2) {
        int screenX = worldX - eng.camera.getX();
        int screenY = worldY - eng.camera.getY();

        if (screenX + eng.getTileSize() > 0 && screenX < eng.getScreenWidth() &&
                screenY + eng.getTileSize() > 0 && screenY < eng.getScreenHeight()) {
            g2.drawImage(image, screenX, screenY, eng.getTileSize(), eng.getTileSize(), null);
        }
    }

    public int getWorldX() {
        return worldX;
    }
    public int getWorldY() {
        return worldY;
    }
}