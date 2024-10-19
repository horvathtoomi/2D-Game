package entity;

import main.GamePanel;

import java.awt.*;
import java.util.Comparator;

public class FriendlyEnemy extends Enemy {
    private static final int SHOOT_INTERVAL = 180; // 3 seconds at 60 FPS
    private static final int ATTACK_RANGE = 20; // 20 tiles
    private int shootCooldown = 0;

    public FriendlyEnemy(GamePanel gp, int startX, int startY) {
        super(gp, "FriendlyEnemy", startX, startY, gp.getTileSize(), gp.getTileSize(), 50);
        setHealth(80);
    }

    @Override
    protected void initializeBehavior() {
        behavior = new FriendlyBehavior(getWorldX(), getWorldY());
    }

    @Override
    public void update() {
        super.update();

        // Follow the player
        followPlayer();

        // Shoot at enemies
        shootCooldown++;
        if (shootCooldown >= SHOOT_INTERVAL) {
            shootAtNearestEnemy();
            shootCooldown = 0;
        }
    }

    private void followPlayer() {
        int dx = gp.player.getWorldX() - getWorldX();
        int dy = gp.player.getWorldY() - getWorldY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > gp.getTileSize()) {
            setWorldX(getWorldX() + (int) (dx / distance * getSpeed()));
            setWorldY(getWorldY() + (int) (dy / distance * getSpeed()));
        }
    }

    private void shootAtNearestEnemy() {
        Entity nearestEnemy = gp.entities.stream()
                .filter(e -> e instanceof Enemy && !(e instanceof FriendlyEnemy) && !(e instanceof NPC_Wayfarer))
                .min(Comparator.comparingDouble(e ->
                        Math.pow(e.getWorldX() - getWorldX(), 2) + Math.pow(e.getWorldY() - getWorldY(), 2)))
                .orElse(null);

        if (nearestEnemy != null) {
            double distance = Math.sqrt(Math.pow(nearestEnemy.getWorldX() - getWorldX(), 2) +
                    Math.pow(nearestEnemy.getWorldY() - getWorldY(), 2));

            if (distance <= ATTACK_RANGE * gp.getTileSize()) {
                FriendlyEnemyAttack attack = new FriendlyEnemyAttack(gp, getWorldX(), getWorldY(),
                        nearestEnemy.getWorldX(), nearestEnemy.getWorldY());
                gp.entities.add(attack);
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        // Draw health bar
        int screenX = getWorldX() - gp.player.getWorldX() + gp.player.getScreenX();
        int screenY = getWorldY() - gp.player.getWorldY() + gp.player.getScreenY();

        g2.setColor(Color.BLACK);
        g2.fillRect(screenX - 1, screenY - 11, gp.getTileSize() + 2, 7);
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY - 10, gp.getTileSize(), 5);
        g2.setColor(Color.GREEN);
        int greenWidth = (int) ((double) getHealth() / 80 * gp.getTileSize());
        g2.fillRect(screenX, screenY - 10, greenWidth, 5);
    }
}