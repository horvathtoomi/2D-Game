package entity.enemy;

import main.Engine;

/**
 * Az óriás típusú ellenség.
 * Nagy életerővel és erős támadással rendelkező lassú ellenség.
 */
public class GiantEnemy extends Enemy {

    /**
     * Létrehoz egy új óriás ellenséget.
     * Nagy méretű, 500 életerővel rendelkező tank egység.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     */
    public GiantEnemy(Engine eng, int startX, int startY) {
        super(eng, "GiantEnemy", startX, startY, (int)(1.5 * eng.getTileSize()), (int)(2.75 * eng.getTileSize()), 400);
        setHealth(500);
        maxHealth = 500;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new DefensiveBehavior();
    }
}
