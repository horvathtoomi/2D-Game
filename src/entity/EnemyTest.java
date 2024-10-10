package entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemyTest extends Entity {
    public BufferedImage shoot;
    public int screenX;
    public int screenY;
    int directionChanger = 0;
    String previousDirection = "right";
    Random rand = new Random();

    private final int startX;
    private final int endX;
    private final int movementRange = 300; // pixels

    private int shootCooldown = 0;
    private final int SHOOT_COOLDOWN_TIME = 120; // 2 seconds at 60 FPS

    private int shootAnimationTimer = 0;
    private final int SHOOT_ANIMATION_DURATION = 30;

    public EnemyTest(GamePanel gp,int startX, int startY) {
        super(gp);
        solidArea = new Rectangle(18,12,8,8);
        shootAnimationTimer=SHOOT_ANIMATION_DURATION;
        try {
            getEnemyTestImage();
        } catch (Exception e) {
            System.out.println("getEntityTestImage() is not working");
        }
        this.worldX = startX;
        this.worldY = startY;
        this.startX = startX;
        this.endX = startX + movementRange;
        this.speed = 2;
        previousDirection = "right";
        direction = "right";
    }

    public void getEnemyTestImage() {
        right = scale("EnemyTest", "right");
        left = scale("EnemyTest", "left");
        shoot = scale("EnemyTest", "shoot");
    }

    public void update() {
        directionChanger++;
        if (shootCooldown > 0)
            shootCooldown--;
        if (direction.equals("shoot")) {
            //private int shootAnimationTimer = 0;
            //private final int SHOOT_ANIMATION_DURATION = 30;
            if (shootCooldown == 0) {
                if(shootAnimationTimer == 15) {
                    shoot();
                    shootCooldown = SHOOT_COOLDOWN_TIME;
                }
                else if(shootAnimationTimer == 0){
                    shootAnimationTimer = SHOOT_ANIMATION_DURATION;
                }
                else{
                    shootAnimationTimer--;
                }
            }
            else {
                direction = "left";
            }
        }
        else {
            if (directionChanger >= 120) { // Change direction every seconds (assuming 60 FPS)
                if (direction.equals("right"))
                    direction = "left";
                else if (direction.equals("left"))
                    direction = "right";
                directionChanger = 0;
            }
            collisionOn = false;
            gp.cChecker.checkTile(this);
            if (!collisionOn) {
                switch (direction) {
                    case "left":
                        if (worldX > startX) {
                            worldX -= speed;
                        } else {
                            direction = "right";
                        }
                        break;
                    case "right":
                        if (worldX < endX) {
                            worldX += speed;
                        } else {
                            direction = "left";
                        }
                        break;
                }
            }
            // Randomly decide to shoot
            if (rand.nextInt(100) < 1 && shootCooldown == 0) { // 1% chance to shoot each frame
                previousDirection = direction;
                direction = "shoot";
            }
        }
    }


    public void shoot() {
        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;

        // Calculate direction
        int dx = playerWorldX - worldX;
        int dy = playerWorldY - worldY;
        double length = Math.sqrt(dx * dx + dy * dy);
        double normalizedDx = dx / length;
        double normalizedDy = dy / length;

        // Adjust starting position
        int startX = (int) (worldX + normalizedDx * gp.tileSize);
        int startY = (int) (worldY + normalizedDy * gp.tileSize);

        gp.entities.add(new EnemyTestAttack(gp, startX, startY, playerWorldX, playerWorldY));
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case "shoot" -> shoot;
            case "left" -> left;
            case "right" -> right;
            default -> null;
        };
        screenX = worldX - gp.player.worldX + gp.player.screenX;
        screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (screenX > -gp.tileSize && screenX < gp.screenWidth && screenY > -gp.tileSize && screenY < gp.screenHeight) {
            int width = (int)(2.25 * gp.tileSize);
            int height = (int)(1.5 * gp.tileSize);
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }
}