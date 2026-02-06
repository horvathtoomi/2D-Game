package entity.enemy;

import entity.Direction;
import entity.Entity;
import entity.Player;
import entity.algorithm.AStar;
import entity.attack.*;
import entity.npc.NPC_Wayfarer;
import main.Engine;
import main.logger.GameLogger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Az enemyk absztrakt ősosztálya.
 * Tartalmazza az összes enemy közös tulajdonságait és viselkedését.
 */
public abstract class Enemy extends Entity {
    Direction previousDirection;

    private final int startX;
    private int shootingRate;

    private int shootCooldown;
    private final int SHOOT_COOLDOWN_TIME = 60; // 1 seconds at 60 FPS

    private int shootAnimationTimer;
    private final int SHOOT_ANIMATION_DURATION = 40;

    public Random random;
    protected EnemyBehavior behavior;
    public ArrayList<int[]> path;
    public int pathIndex;
    protected int updateCounter;
    protected final int UPDATE_INTERVAL = 90;
    private final int[] diffSpeed = { 1, 2, 3, 4 };
    private final int[] diffShootingRate = { 200, 150, 100, 50 };
    private static final String LOG_CONTEXT = "[ENEMY]";
    private static final Direction[] newDirection = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };

    /**
     * Létrehoz egy új ellenséget.
     *
     * @param eng          a játékmotor példánya
     * @param name         az ellenség neve
     * @param startX       kezdő X koordináta
     * @param startY       kezdő Y koordináta
     * @param width        szélesség
     * @param height       magasság
     * @param shootingRate lövési gyakoriság alapértéke
     */
    protected Enemy(Engine eng, String name, int startX, int startY, int width, int height, int shootingRate) {
        super(eng);
        solidArea = new Rectangle(10, 10, width / 2, height / 2);
        random = new Random();
        setWidth(width);
        setHeight(height);
        this.name = name;
        this.startX = startX;
        this.shootingRate = shootingRate;
        initializeEnemy(startY, shootingRate);
        initializeBehavior();
    }

    private void initializeEnemy(int startY, int shootingRate) {
        shootCooldown = SHOOT_COOLDOWN_TIME;
        shootAnimationTimer = SHOOT_ANIMATION_DURATION;
        setMaxHealth(getHealth());
        setWorldX(startX);
        setWorldY(startY);
        initializeValues(shootingRate);
        updateCounter = 0;
        direction = previousDirection = Direction.RIGHT;
        try {
            getEnemyImage();
        } catch (Exception e) {
            GameLogger.error(LOG_CONTEXT, "getEnemyImage() is not working: " + e.getCause(), e);
        }
    }

    private void initializeValues(int plusShootingRate) {
        switch (eng.getGameDifficulty()) {
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

    /**
     * Inicializálja az ellenség viselkedését.
     * Az alosztályoknak kötelező implementálni.
     */
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
        if (getHealth() <= 0) {
            GameLogger.info(LOG_CONTEXT, name + " DIES");
            eng.removeEnemy(this);
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
            collisionOn = false;
            eng.cChecker.checkTile(this);
            if (!collisionOn) {
                switch (direction) {
                    case LEFT -> setWorldX(getWorldX() - getSpeed());
                    case RIGHT -> setWorldX(getWorldX() + getSpeed());
                    case DOWN -> setWorldY(getWorldY() + getSpeed());
                    case UP -> setWorldY(getWorldY() - getSpeed());
                    case SHOOT -> {
                        if (shootCooldown > 0)
                            break;
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
        if (random.nextInt(shootingRate) < 1 && shootCooldown == 0) {
            previousDirection = direction;
            direction = Direction.SHOOT;
        }
    }

    private void followPath() {
        if (pathIndex < path.size()) {
            int[] nextPoint = path.get(pathIndex);
            int nextX = nextPoint[0] * eng.getTileSize();
            int nextY = nextPoint[1] * eng.getTileSize();
            if (getWorldX() < nextX) {
                setWorldX(getWorldX() + getSpeed());
                direction = Direction.RIGHT;
            } else if (getWorldX() > nextX) {
                setWorldX(getWorldX() - getSpeed());
                direction = Direction.LEFT;
            }
            if (getWorldY() < nextY) {
                setWorldY(getWorldY() + getSpeed());
                direction = Direction.DOWN;
            } else if (getWorldY() > nextY) {
                setWorldY(getWorldY() - getSpeed());
                direction = Direction.UP;
            }

            if (getWorldX() == nextX && getWorldY() == nextY) {
                pathIndex++;
            }
        } else {
            path = null;
        }
    }

    private Attack getClosestEnemy() {
        Entity closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Entity e : eng.getEntity()) {
            if (e instanceof Player || e instanceof FriendlyEnemy || e instanceof NPC_Wayfarer) {
                continue;
            }
            double distance = Math.pow(e.getWorldX() - getWorldX(), 2) + Math.pow(e.getWorldY() - getWorldY(), 2);
            if (distance < minDistance) {
                minDistance = distance;
                closest = e;
            }
        }
        if (closest == null) {
            return null;
        }
        int dx = closest.getWorldX() - getWorldX();
        int dy = closest.getWorldY() - getWorldY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double normalizedDx = dx / length;
        double normalizedDy = dy / length;
        int startX = (int) (getWorldX() + normalizedDx * eng.getTileSize());
        int startY = (int) (getWorldY() + normalizedDy * eng.getTileSize());
        return new FriendlyEnemyAttack(eng, startX, startY, closest.getWorldX(), closest.getWorldY());
    }

    public void shoot() {
        int playerWorldX = eng.player.getWorldX();
        int playerWorldY = eng.player.getWorldY();
        int dx = playerWorldX - getWorldX();
        int dy = playerWorldY - getWorldY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double normalizedDx = dx / length;
        double normalizedDy = dy / length;

        int startX = (int) (getWorldX() + normalizedDx * eng.getTileSize());
        int startY = (int) (getWorldY() + normalizedDy * eng.getTileSize());
        Attack attack = switch (name) {
            case "SmallEnemy" -> new SmallEnemyAttack(eng, startX, startY, playerWorldX, playerWorldY);
            case "GiantEnemy" -> new GiantEnemyAttack(eng, startX, startY, playerWorldX, playerWorldY);
            case "TankEnemy" -> new TankEnemyAttack(eng, startX, startY, playerWorldX, playerWorldY);
            case "FriendlyEnemy" -> getClosestEnemy();
            default -> new DragonEnemyAttack(eng, startX, startY, playerWorldX, playerWorldY);
        };
        if (attack != null)
            eng.addEntity(attack);
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        drawHealthBar(g2);
    }

    private void drawHealthBar(Graphics2D g2) {
        int screenX = getWorldX() - eng.camera.getX();
        int screenY = getWorldY() - eng.camera.getY();

        if (!isOnScreen(screenX, screenY))
            return;

        int barWidth = eng.getTileSize();
        int barHeight = 5;

        g2.setColor(Color.BLACK);
        g2.fillRect(screenX, screenY - 10, barWidth, barHeight);

        g2.setColor(Color.GREEN);
        int greenWidth = (int) ((double) getHealth() / getMaxHealth() * barWidth);
        g2.fillRect(screenX, screenY - 10, greenWidth, barHeight);
    }

}

interface EnemyBehavior {
    void act(Enemy enemy);
}

class AggressiveBehavior implements EnemyBehavior {
    private int motionCounter = 0;

    @Override
    public void act(Enemy enemy) {
        if (motionCounter > 10) {
            enemy.path = AStar.findPath(enemy.eng, enemy.getWorldX(), enemy.getWorldY(), enemy.eng.player.getWorldX(),
                    enemy.eng.player.getWorldY());
            enemy.pathIndex = 0;
            motionCounter = 0;
        } else {
            motionCounter++;
        }
    }
}

class DefensiveBehavior implements EnemyBehavior {
    private static final int SAFE_DISTANCE = 200;

    @Override
    public void act(Enemy enemy) {
        int dx = enemy.eng.player.getWorldX() - enemy.getWorldX();
        int dy = enemy.eng.player.getWorldY() - enemy.getWorldY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < SAFE_DISTANCE) {
            int targetX = enemy.getWorldX() - dx;
            int targetY = enemy.getWorldY() - dy;
            enemy.path = AStar.findPath(enemy.eng, enemy.getWorldX(), enemy.getWorldY(), targetX, targetY);
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
            enemy.path = AStar.findPath(enemy.eng, enemy.getWorldX(), enemy.getWorldY(), targetX, targetY);
            enemy.pathIndex = 0;
        }
    }
}

class FriendlyBehavior implements EnemyBehavior {
    protected int startX, startY;
    private int motionCounter = 0;

    public FriendlyBehavior(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public void act(Enemy enemy) {
        if (motionCounter > 10) {
            enemy.path = AStar.findPath(enemy.eng, enemy.getWorldX(), enemy.getWorldY(), enemy.eng.player.getWorldX(),
                    enemy.eng.player.getWorldY());
            enemy.pathIndex = 0;
            motionCounter = 0;
        } else
            motionCounter++;
    }
}