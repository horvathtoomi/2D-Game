package entity;

import main.GamePanel;

public class GiantEnemy extends Enemy {
    public GiantEnemy(GamePanel gp, int startX, int startY) {
        super(gp, "GiantEnemy", startX, startY, (int)(1.5 * gp.getTileSize()), (int)(2.75 * gp.getTileSize()), 500);
        setHealth(100);
    }

    @Override
    protected void initializeBehavior() {
        behavior = new DefensiveBehavior();
    }
}
