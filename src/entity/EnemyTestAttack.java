package entity;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class EnemyTestAttack extends Entity{
    String name;
    int imageChange = 0;
    public BufferedImage image, image1, image2;
    private double dx, dy;
    private final double speed = 4;
    private int initialX, initialY;
    private boolean hasMovedOneTile = false;
    private final int MAX_DISTANCE = 10 * 48;

    public EnemyTestAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp);
        this.worldX = startX;
        this.worldY = startY;
        this.initialX = startX;
        this.initialY = startY;
        name = "EnemyTestAttack";

        // Calculate direction
        double angle = Math.atan2(targetY - startY, targetX - startX);
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;

        try {
            image1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack1.png")));
            image2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack2.png")));
        } catch(IOException e) {
            e.printStackTrace();
        }
        image = image1;
        // Set up collision area
        solidArea = new Rectangle(worldX, worldY, 32, 32); // Adjust as needed
    }

    @Override
    public void update() {
        //collisionOn = ;
        if(!collisionOn) {
            // Move the projectile
            worldX += (int) dx;
            worldY += (int) dy;

            // Only check for collision after moving one tile
            if (hasMovedOneTile) {
                // Check for collision with player
                Rectangle attackHitbox = new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
                if (attackHitbox.intersects(gp.player.solidArea)) {
                    System.out.println("EnemyTestAttack hit player. Removing.");
                    gp.player.health -= 50; // Decrease player health
                    gp.npc.remove(this);
                    return;
                }
            }

            // Remove if out of bounds
            //if (worldX < 0 || worldX > gp.worldWidth || worldY < 0 || worldY > gp.worldHeight) {
            //    System.out.println("EnemyTestAttack out of bounds. Removing.");
            //    gp.npc.remove(this);
            //}
        }
        imageChange++;
        if (imageChange > 5) {
            if (image == image1) {
                image = image2;
            } else {
                image = image1;
            }
            imageChange = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX && worldX - gp.tileSize < gp.player.worldX + gp.player.screenX && worldY + gp.tileSize > gp.player.worldY - gp.player.screenY && worldY - gp.tileSize < gp.player.worldY + gp.player.screenY)
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        else
            System.out.println("EnemyTestAttack not in view.");
    }

}
