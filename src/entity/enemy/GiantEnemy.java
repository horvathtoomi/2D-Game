package entity.enemy;

import main.Engine;

public class GiantEnemy extends Enemy {
    public GiantEnemy(Engine gp, int startX, int startY) {
        super(gp, "GiantEnemy", startX, startY, (int)(1.5 * gp.getTileSize()), (int)(2.75 * gp.getTileSize()), 400);
        setHealth(500);
        maxHealth = 500;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new DefensiveBehavior();
    }
}
