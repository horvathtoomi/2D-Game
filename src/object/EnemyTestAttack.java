package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EnemyTestAttack extends SuperObject{
    int imageChange = 0;
    public BufferedImage image1, image2;
    private int targetX, targetY;
    private double dx, dy;
    private final double speed = 4;

    public EnemyTestAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        this.gp=gp;
        this.worldX=startX;
        this.worldY=startY;
        this.targetX=targetX;
        this.targetY=targetY;
        name="EnemyTestAttack";

        double angle = Math.atan2(targetY - startY, targetX - startX);
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;

        try {
            image1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack1.png"));
            image2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack2.png"));
        }catch(IOException e){e.printStackTrace();}

        image=image1;

        solidArea = new Rectangle(8, 8, 32, 32);
    }

    @Override
    public void update() {
        worldX += dx;
        worldY += dy;

        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;
        if (col >= 0 && col < gp.maxWorldCol && row >= 0 && row < gp.maxWorldRow) {
            if (gp.tileman.mapTileNum[col][row] != 0) { // Assuming 0 is passable
                gp.aSetter.lista.remove(this); // Remove if hit a wall
                return;
            }
        }

        // Animate the projectile
        imageChange++;
        if (imageChange > 5) {
            if (image == image1) {
                image = image2;
            } else {
                image = image1;
            }
            imageChange = 0;
        }

        // Remove if out of bounds
        if (worldX < 0 || worldX > gp.worldWidth || worldY < 0 || worldY > gp.worldHeight) {
            gp.aSetter.lista.remove(this);
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }

}
