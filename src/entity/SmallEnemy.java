package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SmallEnemy extends Entity{

        public BufferedImage shoot;
        public int screenX;
        public int screenY;
        int directionChanger = 0;
        String previousDirection = "right";
        Random rand = new Random();

        private final int startX;
        private final int endX;

        //Duration between shots
        private int shootCooldown;
        private final int SHOOT_COOLDOWN_TIME = 60; // 2 seconds at 60 FPS

        //Duration of the shoot image
        private int shootAnimationTimer;
        private final int SHOOT_ANIMATION_DURATION = 40;

        public SmallEnemy(GamePanel gp, int startX, int startY) {
            super(gp);
            solidArea = new Rectangle(18,12,8,8);
            shootCooldown = SHOOT_COOLDOWN_TIME;
            shootAnimationTimer=SHOOT_ANIMATION_DURATION;
            try {
                getSmallEnemyImage();
            } catch (Exception e) {
                System.out.println("getSmallEnemy() is not working");
            }
            this.worldX = startX;
            this.worldY = startY;

            int movementRange = 300;
            this.startX = startX;
            this.endX = startX + movementRange;

            this.speed = 2;
            previousDirection = "right";
            direction = "right";
        }

        public void getSmallEnemyImage() {
            right = scale("SmallEnemy", "right");
            left = scale("SmallEnemy", "left");
            up = scale("SmallEnemy", "up");
            down = scale("SmallEnemy", "down");
            shoot = scale("SmallEnemy", "shoot");
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
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "up":
                        worldY -= speed;
                        break;
                    case "shoot":
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

            gp.entities.add(new SmallEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY));
        }

        public void draw(Graphics2D g2) {
            BufferedImage image = switch (direction) {
                case "shoot" -> shoot;
                case "left" -> left;
                case "up" -> up;
                case "down" -> down;
                case "right" -> right;
                default -> null;
            };
            screenX = worldX - gp.player.worldX + gp.player.screenX;
            screenY = worldY - gp.player.worldY + gp.player.screenY;
            if (screenX > -gp.tileSize && screenX < gp.screenWidth && screenY > -gp.tileSize && screenY < gp.screenHeight) {
                g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }

}
