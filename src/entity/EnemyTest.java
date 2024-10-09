/*
package entity;

import main.GamePanel;
import object.EnemyTestAttack;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemyTest extends Entity {

    GamePanel gp;
    public BufferedImage shoot;
    public int screenX;
    public int screenY;
    int directionChanger = 0;
    String previousDirection = "";
    Random rand = new Random();

    public EnemyTest(GamePanel panel, Player player) {
        this.gp = panel;
        solidArea = new Rectangle();
        solidArea.x = 18;
        solidArea.y = 12; //36x24
        solidArea.width = 8;
        solidArea.height = 8;
        try {
            getEnemyTestImage();
        } catch (Exception e) {
            System.out.println("getEntityTestImage() is not working");
        }
        //screenX=player.screenX;
        //screenY=player.screenY;
        worldX = player.screenX;
        worldY = player.screenY;
        this.speed = player.speed;
        previousDirection = "right";
        direction = "shoot";
    }

    public void getEnemyTestImage() {
        right = scale(gp, "EnemyTest", "right");
        left = scale(gp, "EnemyTest", "left");
        shoot = scale(gp, "EnemyTest", "shoot");
    }

    public void update() {
        if (directionChanger >= 31) {
            switch (direction) {
                case "left" -> {
                    previousDirection = "left";
                    direction = "right";
                }
                case "right" -> {
                    previousDirection = "right";
                    direction = "left";
                }
                case "shoot" -> {
                    if (previousDirection.equals("right")) {
                        direction = "left";
                    } else if (previousDirection.equals("left")) {
                        direction = "right";
                    }
                }
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
                case "shoot":
                    shoot();
                    break;
            }
        }
        directionChanger++;
    }

    public void shoot() {
        manuallySetObject(gp, new EnemyTestAttack(gp, worldX, worldY));
    }

        public void draw(Graphics2D g2) {
            BufferedImage image = switch (direction) {
                case "shoot" -> shoot;
                case "left" -> left;
                case "right" -> right;
                default -> null;
            };
            int width = (int)(2.25 * gp.tileSize);
            int height = (int)(1.5*gp.tileSize);
            g2.drawImage(image,worldX,worldY, width , height , null);
        }

    }
    // gp.tileSize = 3 * 16 = 48
    // 36x24   x:    3 * 36 = 108
    //         y:    3 * 24 = 72

     */

package entity;

import main.GamePanel;
import object.EnemyTestAttack;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemyTest extends Entity {

    GamePanel gp;
    public BufferedImage shoot;
    public int screenX;
    public int screenY;
    int directionChanger = 0;
    String previousDirection = "";
    Random rand = new Random();

    // New variables for fixed movement
    private int startX;
    private int endX;
    private int movementRange = 200; // pixels

    public EnemyTest(GamePanel panel, int startX, int startY) {
        this.gp = panel;
        solidArea = new Rectangle();
        solidArea.x = 18;
        solidArea.y = 12;
        solidArea.width = 8;
        solidArea.height = 8;

        try {
            getEnemyTestImage();
        } catch (Exception e) {
            System.out.println("getEntityTestImage() is not working");
        }

        // Set fixed position and movement range
        this.worldX = startX;
        this.worldY = startY;
        this.startX = startX;
        this.endX = startX + movementRange;

        this.speed = 2;
        previousDirection = "right";
        direction = "right";
    }

    public void getEnemyTestImage() {
        right = scale(gp, "EnemyTest", "right");
        left = scale(gp, "EnemyTest", "left");
        shoot = scale(gp, "EnemyTest", "shoot");
    }

    public void update() {
        directionChanger++;

        if (directionChanger >= 120) { // Change direction every 2 seconds (assuming 60 FPS)
            if (direction.equals("right")) {
                direction = "left";
            } else if (direction.equals("left")) {
                direction = "right";
            } else if (direction.equals("shoot")) {
                direction = previousDirection;
            }
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
                case "shoot":
                    shoot();
                    break;
            }
        }

        // Randomly decide to shoot
        if (rand.nextInt(200) < 0.5) { // 1% chance to shoot each frame
            previousDirection = direction;
            direction = "shoot";
            directionChanger = 0;
        }
    }

    public void shoot() {
        int playerWorldX = gp.player.worldX;
        int playerWorldY = gp.player.worldY;
        gp.addObject(new EnemyTestAttack(gp, worldX, worldY, playerWorldX, playerWorldY));
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case "shoot" -> shoot;
            case "left" -> left;
            case "right" -> right;
            default -> null;
        };

        // Calculate screen position
        screenX = worldX - gp.player.worldX + gp.player.screenX;
        screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Only draw if on screen
        if (screenX > -gp.tileSize && screenX < gp.screenWidth &&
                screenY > -gp.tileSize && screenY < gp.screenHeight) {
            int width = (int)(2.25 * gp.tileSize);
            int height = (int)(1.5 * gp.tileSize);
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }
}
