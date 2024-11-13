package entity.enemy;

import main.Engine;

public class FriendlyEnemy extends Enemy {

    public FriendlyEnemy(Engine gp, int startX, int startY) {
        super(gp, "FriendlyEnemy", startX, startY, gp.getTileSize(), gp.getTileSize(), -10);
        setHealth(80);
        maxHealth = 80;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new FriendlyBehavior(getWorldX(), getWorldY());
    }
}