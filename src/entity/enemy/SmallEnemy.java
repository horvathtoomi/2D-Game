package entity.enemy;

import main.Engine;

public class SmallEnemy extends Enemy {
    public SmallEnemy(Engine gp, int startX, int startY) {
        super(gp, "SmallEnemy", startX, startY, (int)(gp.getTileSize() * 1.5), (int)(gp.getTileSize() * 1.5), 25);
        setHealth(100);
        maxHealth = 100;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new PatrolBehavior(getWorldX(), getWorldY(), 5 * gp.getTileSize());
    }
}
