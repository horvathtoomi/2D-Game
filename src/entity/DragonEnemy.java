package entity;

import main.GamePanel;

public class DragonEnemy extends Enemy {
    public DragonEnemy(GamePanel gp, int startX, int startY) {
        super(gp, "DragonEnemy", startX, startY, (int)(2.25 * gp.getTileSize()), (int)(1.5 * gp.getTileSize()), 200);
        setHealth(100);
    }

    @Override
    protected void initializeBehavior() {
        behavior = new AggressiveBehavior();
    }
}
