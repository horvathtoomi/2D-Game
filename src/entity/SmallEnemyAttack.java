package entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SmallEnemyAttack extends Entity{
    String name;
    int imageChange = 0;
    public BufferedImage image, image1, image2;
    public double dx, dy;

    public SmallEnemyAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp);
        worldX = startX;
        worldY = startY;
        name = "SmallEnemyAttack";
        // Calculate direction
        double angle = Math.atan2(targetY - startY, targetX - startX);
        double speed = 6;
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
        image1 = scale("objects","SmallEnemyAttack1");
        image2 = scale("objects","SmallEnemyAttack2");
        image = image1;
        solidArea = new Rectangle(worldX + 6, worldY + 8, 32, 32);

    }

    @Override
    public void update() {
        worldX += (int) dx;
        worldY += (int) dy;
        solidArea.setLocation(worldX + 3, worldY + 4);
        Rectangle playerHitbox = new Rectangle(gp.player.worldX + gp.player.solidArea.x, gp.player.worldY + gp.player.solidArea.y, gp.player.solidArea.width, gp.player.solidArea.height); //Width=32, Height=32
        if (solidArea.intersects(playerHitbox)) {
            gp.player.health -= 33;
            // Set this attack to null in the entities list
            gp.entities.set(gp.entities.indexOf(this), null);
            return;
        }
        imageChange++;
        if (imageChange > 5) {
            if (image == image1)
                image = image2;
            else
                image = image1;
            imageChange = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            // x-> screenX, y -> screenY
            g2.drawImage(image, worldX - gp.player.worldX + gp.player.screenX, worldY - gp.player.worldY + gp.player.screenY, gp.tileSize, gp.tileSize, null);
        }
    }

}
