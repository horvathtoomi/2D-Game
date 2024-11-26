package entity.enemy;

import main.Engine;

/**
 * A sárkány típusú ellenség.
 * Nagy életerővel és erős támadással rendelkező repülő ellenség.
 */
public class DragonEnemy extends Enemy {

    /**
     * Létrehoz egy új sárkány ellenséget.
     * Nagy méretű, 250 életerővel rendelkező ellenség.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     */
    public DragonEnemy(Engine eng, int startX, int startY) {
        super(eng, "DragonEnemy", startX, startY, (int)(2.25 * eng.getTileSize()), (int)(1.5 * eng.getTileSize()), 50);
        setHealth(250);
        maxHealth = 250;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new AggressiveBehavior();
    }
}
