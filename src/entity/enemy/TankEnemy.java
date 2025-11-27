package entity.enemy;

import main.Engine;

public class TankEnemy extends Enemy{

    public TankEnemy(Engine eng, int startX, int startY) {
        super(eng, "TankEnemy", startX, startY, (2 * eng.getTileSize()), (2 * eng.getTileSize()), 5);
        setHealth(5000);
        maxHealth = 5000;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new DefensiveBehavior();
    }
}