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
    private final double speed = 6;
    private int initialX, initialY;

    public EnemyTestAttack(GamePanel gp, int startX, int startY, int targetX, int targetY) {
        super(gp);
        worldX = startX;
        worldY = startY;
        initialX = startX;
        initialY = startY;
        name = "EnemyTestAttack";

        // Calculate direction
        double angle = Math.atan2(targetY - startY, targetX - startX);
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;

        try {
            image1 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack1.png")));
            image2 = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("objects/EnemyTestAttack2.png")));
        } catch(IOException e) {
            e.getCause();
        }
        image = image1;
    }

    @Override
    public void update() {
        worldX += (int) dx;
        worldY += (int) dy;
        initialX = worldX - gp.player.worldX + gp.player.screenX;
        initialY = worldY - gp.player.worldY + gp.player.screenY;
        solidArea = new Rectangle(gp.player.screenX,gp.player.screenY,10,10);
        System.out.println("IDX OF BULLET: " + initialX + " " + initialY);
        System.out.println("IDX OF PLAYER: " + gp.player.worldX + " " + gp.player.worldY);
        if (solidArea.intersects(gp.player.solidArea)) {
            gp.player.health -= 50;
            System.out.println(gp.player.health);
            gp.entities.set(gp.entities.indexOf(this),null);
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
        initialX = worldX - gp.player.worldX + gp.player.screenX;
        initialY = worldY - gp.player.worldY + gp.player.screenY;
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX && worldX - gp.tileSize < gp.player.worldX + gp.player.screenX && worldY + gp.tileSize > gp.player.worldY - gp.player.screenY && worldY - gp.tileSize < gp.player.worldY + gp.player.screenY)
            g2.drawImage(image, initialX, initialY, gp.tileSize, gp.tileSize, null);
    }

}
