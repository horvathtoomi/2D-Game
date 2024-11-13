package entity.enemy;

import main.Engine;

public class DragonEnemy extends Enemy {
    public DragonEnemy(Engine gp, int startX, int startY) {
        super(gp, "DragonEnemy", startX, startY, (int)(2.25 * gp.getTileSize()), (int)(1.5 * gp.getTileSize()), 50);
        setHealth(250);
        maxHealth = 250;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new AggressiveBehavior();
    }
}
