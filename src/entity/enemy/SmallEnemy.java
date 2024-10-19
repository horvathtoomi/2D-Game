package entity.enemy;

import main.GamePanel;

public class SmallEnemy extends Enemy {
    public SmallEnemy(GamePanel gp, int startX, int startY) {
        super(gp, "SmallEnemy", startX, startY, (int)(gp.getTileSize() * 1.5), (int)(gp.getTileSize() * 1.5), 75);
        setHealth(100);
        maxHealth = 100;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new PatrolBehavior(getWorldX(), getWorldY(), 5 * gp.getTileSize());
    }
}
