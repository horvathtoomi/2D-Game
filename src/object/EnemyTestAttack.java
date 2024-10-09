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
    private int initialX, initialY;
    private boolean hasMovedOneTile = false;
    private final int MAX_DISTANCE = 10 * 48;

    public EnemyTestAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        this.gp = gp;
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
            image1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack1.png"));
            image2 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack2.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        image = image1;

        // Set up collision area
        solidArea = new Rectangle(8, 8, 32, 32); // Adjust as needed
    }

    @Override
    public void update() {
        // Move the projectile
        worldX += dx;
        worldY += dy;

        System.out.println("EnemyTestAttack updated. Position: (" + worldX + ", " + worldY + ")");

        // Check if the projectile has moved at least one tile
        if (!hasMovedOneTile) {
            double distanceMoved = Math.sqrt(Math.pow(worldX - initialX, 2) + Math.pow(worldY - initialY, 2));
            if (distanceMoved >= gp.tileSize) {
                hasMovedOneTile = true;
            }
        }

        // Check for maximum distance
        double totalDistanceTraveled = Math.sqrt(Math.pow(worldX - initialX, 2) + Math.pow(worldY - initialY, 2));
        if (totalDistanceTraveled > MAX_DISTANCE) {
            System.out.println("EnemyTestAttack reached maximum distance. Removing.");
            gp.aSetter.lista.remove(this);
            return;
        }

        // Only check for collision after moving one tile
        if (hasMovedOneTile) {
            // Check for collision with tiles
            int col = worldX / gp.tileSize;
            int row = worldY / gp.tileSize;
            System.out.println("Checking tile at col: " + col + ", row: " + row);

            if (col >= 0 && col < gp.maxWorldCol && row >= 0 && row < gp.maxWorldRow) {
                if (gp.tileman.mapTileNum[col][row] != 0) { // Assuming 0 is passable
                    System.out.println("EnemyTestAttack hit a wall. Removing.");
                    gp.aSetter.lista.remove(this); // Remove if hit a wall
                    return;
                }
            }

            // Check for collision with player
            Rectangle attackHitbox = new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
            Rectangle playerHitbox = new Rectangle(gp.player.worldX + gp.player.solidArea.x,
                    gp.player.worldY + gp.player.solidArea.y,
                    gp.player.solidArea.width,
                    gp.player.solidArea.height);
            if (attackHitbox.intersects(playerHitbox)) {
                System.out.println("EnemyTestAttack hit player. Removing.");
                gp.player.health -= 50; // Decrease player health
                gp.aSetter.lista.remove(this);
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
            System.out.println("EnemyTestAttack out of bounds. Removing.");
            gp.aSetter.lista.remove(this);
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        System.out.println("EnemyTestAttack drawing. Screen position: (" + screenX + ", " + screenY + ")");
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            System.out.println("EnemyTestAttack drawn on screen.");
        }
        else{
            System.out.println("EnemyTestAttack not in view.");
        }
    }

}
