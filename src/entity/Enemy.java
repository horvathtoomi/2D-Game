package entity;

import entity.algorithm.AStar;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.ArrayList;

public abstract class Enemy extends Entity{
    public BufferedImage shoot;
    String previousDirection = "right";
    private int width, height;

    private final int startX;
    private final int shootingRate;

    //Duration between shots
    private int shootCooldown;
    private final int SHOOT_COOLDOWN_TIME = 60; // 2 seconds at 60 FPS

    //Duration of the shoot image
    private int shootAnimationTimer;
    private final int SHOOT_ANIMATION_DURATION = 40;

    public Random random;
    protected EnemyBehavior behavior;
    public ArrayList<int[]> path;
    public int pathIndex;
    protected int updateCounter;
    protected final int UPDATE_INTERVAL = 60; // Update path every second (assuming 60 FPS)


    public Enemy(GamePanel gp, String name, int startX, int startY, int width, int height, int shootingRate) {
        super(gp);
        solidArea = new Rectangle(18,12,8,8);
        shootCooldown = SHOOT_COOLDOWN_TIME;
        shootAnimationTimer=SHOOT_ANIMATION_DURATION;
        this.width = width;
        this.height = height;
        this.name=name;
        setWorldX(startX);
        setWorldY(startY);
        updateCounter = 0;
        this.shootingRate=shootingRate;
        random = new Random();
        try {
            getEnemyImage();
        } catch (Exception e) {
            System.out.println("getEnemyImage() is not working");
        }
        int movementRange = 300;
        this.startX = startX;
        setSpeed(1);
        previousDirection = "right";
        direction = "right";
        initializeBehavior();
    }

    protected abstract void initializeBehavior();

    public void getEnemyImage() {
        right = scale(name, "right");
        left = scale(name, "left");
        down = scale(name, "down");
        up = scale(name, "up");
        shoot = scale(name, "shoot");
    }

    @Override
    public void update() {
        updateCounter++;
        if (updateCounter >= UPDATE_INTERVAL) {
            behavior.act(this);
            updateCounter = 0;
        }
        if (shootCooldown > 0)
            shootCooldown--;

        if (path != null && !path.isEmpty()) {
            followPath();
        } else {
            // Existing movement logic
            collisionOn = false;
            gp.cChecker.checkTile(this);
            if (!collisionOn) {
                switch (direction) {
                    case "left" -> setWorldX(getWorldX() - getSpeed());
                    case "right" -> setWorldX(getWorldX() + getSpeed());
                    case "down" -> setWorldY(getWorldY() + getSpeed());
                    case "up" -> setWorldY(getWorldY() - getSpeed());
                    case "shoot" -> {
                        if (shootCooldown == 0) {
                            if (shootAnimationTimer == SHOOT_ANIMATION_DURATION / 2) {
                                shoot();
                            } else if (shootAnimationTimer == 0) {
                                shootAnimationTimer = SHOOT_ANIMATION_DURATION;
                                shootCooldown = SHOOT_COOLDOWN_TIME;
                            }
                            shootAnimationTimer--;
                        } else
                            direction = previousDirection;
                    }
                }
            }
        }
        // Shooting logic
        if (random.nextInt(shootingRate) < 1 && shootCooldown == 0) {
            if(direction.equals("shoot"))
                previousDirection = "up";
            else
                previousDirection = direction;
            direction = "shoot";
        }
    }

    public int[] getClosestEnemyCoord(int startX, int startY) {
        int[] coord = new int[2];
        int minDist = Integer.MAX_VALUE;
        int entX = 0, entY= 0;
        for(Entity ent : gp.entities) {
            if(!ent.name.equals("NPC_Wayfarer")) {
                if (((ent.getWorldX()-startX)*(ent.getWorldX()-startX) + (ent.getWorldY()-startY)*(ent.getWorldY()-startY)) <minDist) {
                    minDist = ((ent.getWorldX()-startX) * (ent.getWorldX()-startX)) + ((ent.getWorldY()-startY) * (ent.getWorldY()-startY));
                    entX = ent.getWorldX();
                    entY = ent.getWorldY();
                }
            }
        }
        coord[0] = entX;
        coord[1] = entY;
        return coord;
    }

    protected void followPath() {
        if (pathIndex < path.size()) {
            int[] nextPoint = path.get(pathIndex);
            int nextX = nextPoint[0] * gp.getTileSize();
            int nextY = nextPoint[1] * gp.getTileSize();
            if (getWorldX() < nextX) {
                setWorldX(getWorldX() + getSpeed());
                direction = "right";
            } else if (getWorldX() > nextX) {
                setWorldX(getWorldX() - getSpeed());
                direction = "left";
            }

            if (getWorldY() < nextY) {
                setWorldY(getWorldY() + getSpeed());
                direction = "down";
            } else if (getWorldY() > nextY) {
                setWorldY(getWorldY() - getSpeed());
                direction = "up";
            }

            if (getWorldX() == nextX && getWorldY() == nextY) {
                pathIndex++;
            }
        } else {
            path = null;
        }
    }


    public void shoot() {
        int playerWorldX = gp.player.getWorldX();
        int playerWorldY = gp.player.getWorldY();

        // Calculate direction
        int dx = playerWorldX - getWorldX();
        int dy = playerWorldY - getWorldY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double normalizedDx = dx / length;
        double normalizedDy = dy / length;

        // Adjust starting position
        int startX = (int) (getWorldX() + normalizedDx * gp.getTileSize());
        int startY = (int) (getWorldY() + normalizedDy * gp.getTileSize());
        switch(name){
            case "EnemyTest":
                gp.entities.add(new DragonEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY));
                break;
            case "SmallEnemy":
                gp.entities.add(new SmallEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY));
                break;
            case "GiantEnemy":
                gp.entities.add(new GiantEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY));
                break;
            case "FriendlyEnemy":
                //VALTOZTATAS SZUKSEGES!
                gp.entities.add(new FriendlyEnemyAttack(gp, startX, startY, getClosestEnemyCoord(startX,startY)[0], getClosestEnemyCoord(startX,startY)[1]));
                break;
            default:
                gp.entities.add(new DragonEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY));
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
        setScreenX(getWorldX() - gp.player.getWorldX() + gp.player.getScreenX());
        setScreenY(getWorldY() - gp.player.getWorldY() + gp.player.getScreenY());
        if (getScreenX() > -gp.getTileSize() && getScreenX() < gp.getScreenWidth() && getScreenY() > -gp.getTileSize() && getScreenY() < gp.getScreenHeight())
            g2.drawImage(image, getScreenX(), getScreenY(), width, height, null);
    }
}


interface EnemyBehavior {
    void act(Enemy enemy);
}

class AggressiveBehavior implements EnemyBehavior {
    private int motionCounter = 0;
    @Override
    public void act(Enemy enemy) {
        if(motionCounter>5) {
            enemy.path = AStar.findPath(enemy.gp, enemy.getWorldX(), enemy.getWorldY(), enemy.gp.player.getWorldX(), enemy.gp.player.getWorldY());
            enemy.pathIndex = 0;
            motionCounter = 0;
        }
        else {
            motionCounter++;
        }
    }
}

class DefensiveBehavior implements EnemyBehavior {
    private static final int SAFE_DISTANCE = 200;

    @Override
    public void act(Enemy enemy) {
        int dx = enemy.gp.player.getWorldX() - enemy.getWorldX();
        int dy = enemy.gp.player.getWorldY() - enemy.getWorldY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < SAFE_DISTANCE) {
            int targetX = enemy.getWorldX() - dx;
            int targetY = enemy.getWorldY() - dy;
            enemy.path = AStar.findPath(enemy.gp, enemy.getWorldX(), enemy.getWorldY(), targetX, targetY);
            enemy.pathIndex = 0;
        } else {
            enemy.path = null;
        }
    }
}

class PatrolBehavior implements EnemyBehavior {
    private final int patrolRadius;
    private final int startX, startY;

    public PatrolBehavior(int startX, int startY, int patrolRadius) {
        this.startX = startX;
        this.startY = startY;
        this.patrolRadius = patrolRadius;
    }

    @Override
    public void act(Enemy enemy) {
        if (enemy.path == null || enemy.path.isEmpty()) {
            int targetX = startX + enemy.random.nextInt(patrolRadius * 2) - patrolRadius;
            int targetY = startY + enemy.random.nextInt(patrolRadius * 2) - patrolRadius;
            enemy.path = AStar.findPath(enemy.gp, enemy.getWorldX(), enemy.getWorldY(), targetX, targetY);
            enemy.pathIndex = 0;
        }
    }
}

class FriendlyBehavior implements EnemyBehavior{
    protected int startX, startY;

    public FriendlyBehavior(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public void act(Enemy enemy){
        enemy.path = AStar.findPath(enemy.gp, enemy.getWorldX(), enemy.getWorldY(), enemy.gp.player.getWorldX(), enemy.gp.player.getWorldY());
        enemy.pathIndex = 0;
    }
}