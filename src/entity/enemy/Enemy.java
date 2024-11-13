package entity.enemy;

import entity.*;
import entity.algorithm.AStar;
import entity.attack.*;
import entity.npc.NPC_Wayfarer;
import main.Engine;
import main.logger.GameLogger;

import java.awt.*;
import java.util.Comparator;
import java.util.Random;
import java.util.ArrayList;

public abstract class Enemy extends Entity {
    String previousDirection;

    private final int startX;
    private int shootingRate;

    private int shootCooldown;
    private final int SHOOT_COOLDOWN_TIME = 60; // 2 seconds at 60 FPS

    private int shootAnimationTimer;
    private final int SHOOT_ANIMATION_DURATION = 40;

    public Random random;
    protected EnemyBehavior behavior;
    public ArrayList<int[]> path;
    public int pathIndex;
    protected int updateCounter;
    protected final int UPDATE_INTERVAL = 90;// Update path every second (assuming 60 FPS)
    private final int[] diffSpeed = {1,2,3,4};
    private final int[] diffShootingRate = {200, 150, 100, 50};
    private static final String LOG_CONTEXT = "[ENEMY]";
    private static final String[] newDirection = {"up","down","left","right"};

    protected Enemy(Engine gp, String name, int startX, int startY, int width, int height, int shootingRate) {
        super(gp);
        solidArea = new Rectangle(10,10,width/2,height/2);
        random = new Random();
        setWidth(width);
        setHeight(height);
        this.name=name;
        this.startX = startX;
        this.shootingRate=shootingRate;
        initializeEnemy(startY, shootingRate);
        initializeBehavior();
    }

    private void initializeEnemy(int startY, int shootingRate){
        shootCooldown = SHOOT_COOLDOWN_TIME;
        shootAnimationTimer=SHOOT_ANIMATION_DURATION;
        setMaxHealth(getHealth());
        setWorldX(startX);
        setWorldY(startY);
        initializeValues(shootingRate);
        updateCounter = 0;
        direction = previousDirection = "right";
        try {
            getEnemyImage();
        } catch (Exception e) {
            GameLogger.error(LOG_CONTEXT, "getEnemyImage() is not working: " + e.getCause(), e);
        }
    }

    private void initializeValues(int plusShootingRate){
        switch(gp.getGameDifficulty()){
            case EASY -> {
                setSpeed(diffSpeed[0]);
                this.shootingRate = diffShootingRate[0] + plusShootingRate;
            }
            case MEDIUM -> {
                setSpeed(diffSpeed[1]);
                this.shootingRate = diffShootingRate[1] + plusShootingRate;
            }
            case HARD -> {
                setSpeed(diffSpeed[2]);
                this.shootingRate = diffShootingRate[2] + plusShootingRate;

            }
            case IMPOSSIBLE -> {
                setSpeed(diffSpeed[3]);
                this.shootingRate = diffShootingRate[3] + plusShootingRate;
            }
        }
    }

    protected abstract void initializeBehavior();

    protected void getEnemyImage() {
        right = scale(name, "right");
        left = scale(name, "left");
        down = scale(name, "down");
        up = scale(name, "up");
        shoot = scale(name, "shoot");
    }

    @Override
    public void update() {
        if(getHealth() <= 0) {
            GameLogger.info(LOG_CONTEXT, name + "DIES");
            gp.removeEnemy(this);
            return;
        }
        updateCounter++;
        if (updateCounter >= UPDATE_INTERVAL) {
            behavior.act(this);
            updateCounter = 0;
        }
        if (shootCooldown > 0) {
            shootCooldown--;
        }
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
                        if (shootCooldown > 0) break;
                        if (shootAnimationTimer == SHOOT_ANIMATION_DURATION / 2) {
                            shoot();
                        }
                        if (shootAnimationTimer > 0) {
                            shootAnimationTimer--;
                        } else {
                            shootCooldown = SHOOT_COOLDOWN_TIME;
                            direction = newDirection[random.nextInt(4)];
                            shootAnimationTimer = SHOOT_ANIMATION_DURATION;
                        }
                    }
                }
            }
        }
        // Shooting logic
        if (random.nextInt(shootingRate) < 1 && shootCooldown == 0) {
            previousDirection = direction;
            direction = "shoot";
        }
    }

    private void followPath() {
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

    private Attack getClosestEnemy() {
        return gp.getEntity().stream()
                .filter(e -> !(e instanceof Player) && !(e instanceof FriendlyEnemy) && !(e instanceof NPC_Wayfarer))
                .min(Comparator.comparingDouble(e ->
                        Math.pow(e.getWorldX() - getWorldX(), 2) +
                                Math.pow(e.getWorldY() - getWorldY(), 2)))
                .map(nearestEnemy -> {
                    int dx = nearestEnemy.getWorldX() - getWorldX();
                    int dy = nearestEnemy.getWorldY() - getWorldY();
                    double length = Math.sqrt(dx * dx + dy * dy);
                    double normalizedDx = dx / length;
                    double normalizedDy = dy / length;

                    // Calculate starting position
                    int startX = (int) (getWorldX() + normalizedDx * gp.getTileSize());
                    int startY = (int) (getWorldY() + normalizedDy * gp.getTileSize());

                    return new FriendlyEnemyAttack(gp, startX, startY,
                            nearestEnemy.getWorldX(), nearestEnemy.getWorldY());
                })
                .orElse(null);
    }

    public void shoot() {
        int playerWorldX = gp.player.getWorldX();
        int playerWorldY = gp.player.getWorldY();
        int dx = playerWorldX - getWorldX();
        int dy = playerWorldY - getWorldY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double normalizedDx = dx / length;
        double normalizedDy = dy / length;

        // Adjust starting position
        int startX = (int) (getWorldX() + normalizedDx * gp.getTileSize());
        int startY = (int) (getWorldY() + normalizedDy * gp.getTileSize());
        Attack attack = switch(name){
            case "SmallEnemy" -> new SmallEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY);
            case "GiantEnemy" -> new GiantEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY);
            case "FriendlyEnemy" -> getClosestEnemy();
            default -> new DragonEnemyAttack(gp, startX, startY, playerWorldX, playerWorldY);
        };
        if(attack != null)
            gp.addEntity(attack);
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        drawHealthBar(g2);
    }

    private void drawHealthBar(Graphics2D g2) {
        int screenX = getWorldX() - gp.player.getWorldX() + gp.player.getScreenX();
        int screenY = getWorldY() - gp.player.getWorldY() + gp.player.getScreenY();
        screenX = adjustScreenX(screenX);
        screenY = adjustScreenY(screenY);
        if (isValidScreenXY(screenX, screenY)) {
            g2.setColor(Color.BLACK);
            g2.fillRect(screenX - 1, screenY - 11, gp.getTileSize() / 100 * getMaxHealth(), 7);
            g2.setColor(Color.RED);
            g2.fillRect(screenX + getWidth() - gp.getTileSize(), screenY - 10, gp.getTileSize(), 5);
            g2.setColor(Color.GREEN);
            int greenWidth = (int) ((double) getHealth() / getMaxHealth() * gp.getTileSize());
            g2.fillRect(screenX + getWidth() - gp.getTileSize(), screenY - 10, greenWidth, 5);
        }
    }

}


interface EnemyBehavior {
    void act(Enemy enemy);
}

class AggressiveBehavior implements EnemyBehavior {
    private int motionCounter = 0;

    @Override
    public void act(Enemy enemy) {
        if(motionCounter>10) {
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
    private int motionCounter = 0;
    public FriendlyBehavior(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public void act(Enemy enemy) {
        if (motionCounter > 10) {
            enemy.path = AStar.findPath(enemy.gp, enemy.getWorldX(), enemy.getWorldY(), enemy.gp.player.getWorldX(), enemy.gp.player.getWorldY());
            enemy.pathIndex = 0;
            motionCounter = 0;
        }
        else
            motionCounter++;
    }
}