package entity.enemy;

import main.GamePanel;

public class FriendlyEnemy extends Enemy {

    public FriendlyEnemy(GamePanel gp, int startX, int startY) {
        super(gp, "FriendlyEnemy", startX, startY, gp.getTileSize(), gp.getTileSize(), 50);
        setHealth(80);
        maxHealth = 80;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new FriendlyBehavior(getWorldX(), getWorldY());
    }
}