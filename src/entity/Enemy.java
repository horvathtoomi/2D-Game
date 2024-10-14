package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends Entity{
    public BufferedImage shoot;
    public int screenX;
    public int screenY;
    int directionChanger = 0;
    String previousDirection = "right";
    Random rand = new Random();
    int width, height;

    private final int startX;
    private final int shootingRate;
    //Duration between shots
    private int shootCooldown;
    private final int SHOOT_COOLDOWN_TIME = 60; // 2 seconds at 60 FPS

    //Duration of the shoot image
    private int shootAnimationTimer;
    private final int SHOOT_ANIMATION_DURATION = 40;

    public Enemy(GamePanel gp, String name, int startX, int startY, int width, int height, int shootingRate) {
        super(gp);
        solidArea = new Rectangle(18,12,8,8);
        shootCooldown = SHOOT_COOLDOWN_TIME;
        shootAnimationTimer=SHOOT_ANIMATION_DURATION;
        this.width = width;
        this.height = height;
        this.shootingRate=shootingRate;
        this.name=name;
        try {
            getEnemyImage();
        } catch (Exception e) {
            System.out.println("getEnemyImage() is not working");
        }
        this.worldX = startX;
        this.worldY = startY;

        int movementRange = 300;
        this.startX = startX;

        this.speed = 1;
        previousDirection = "right";
        direction = "right";
    }

    public void getEnemyImage() {
        right = scale(name, "right");
        left = scale(name, "left");
        down = scale(name, "down");
        up = scale(name, "up");
        shoot = scale(name, "shoot");
    }

    public void update() {
        directionChanger++;
        if (shootCooldown > 0)
            shootCooldown--;
        if (direction.equals("shoot")) {
            if (shootCooldown == 0) {
                if(shootAnimationTimer == SHOOT_ANIMATION_DURATION/2) {
                    shoot();
                }
                else if(shootAnimationTimer == 0){
                    shootAnimationTimer = SHOOT_ANIMATION_DURATION;
                    shootCooldown = SHOOT_COOLDOWN_TIME;
                }
                shootAnimationTimer--;
            }
            else {
                direction = previousDirection;
            }
        }
        else {
            if (directionChanger >= 120) { // Change direction every second (assuming 60 FPS)
                switch (direction) {
                    case "right":
                        if(rand.nextInt(2)==1)
                            direction = "left";
                        else
                            direction = "down";
                        break;
                    case "left":
                        if(rand.nextInt(2)==1)
                            direction = "right";
                        else
                            direction = "up";
                        break;
                    case "down":
                        if(rand.nextInt(2)==1)
                            direction = "left";
                        else
                            direction = "up";
                        break;
                    case "up":
                        if(rand.nextInt(2)==1)
                            direction = "right";
                        else
                            direction = "down";
                        break;
                }
                directionChanger = 0;
            }
            collisionOn = false;
            gp.cChecker.checkTile(this);
            if (!collisionOn) {
                switch (direction) {
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                    case "down" -> worldY += speed;
                    case "up" -> worldY -= speed;
                    case "shoot" -> {}
                }
            }
            // Randomly decide to shoot
            if (rand.nextInt(shootingRate) < 1 && shootCooldown == 0) { // 1% chance to shoot each frame
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
        switch(name){
            case "EnemyTest":
                gp.entities.add(new EnemyTestAttack(gp, startX, startY, playerWorldX, playerWorldY));
                break;
            case "SmallEnemy":
                gp.entities.add(new SmallEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY));
                break;
            case "GiantEnemy":
                gp.entities.add(new GiantEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY));
                break;
            default:
                gp.entities.add(new EnemyTestAttack(gp, startX, startY, playerWorldX, playerWorldY));
                break;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case "shoot" -> shoot;
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> null;
        };
        screenX = worldX - gp.player.worldX + gp.player.screenX;
        screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (screenX > -gp.tileSize && screenX < gp.screenWidth && screenY > -gp.tileSize && screenY < gp.screenHeight) {
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }
}
