package entity.enemy;

import main.Engine;

/**
 * A barátságos ellenség.
 * A játékost segítő egység, amely a többi ellenséget támadja.
 */
public class FriendlyEnemy extends Enemy {

    /**
     * Létrehoz egy új barátságos ellenséget.
     * Közepes méretű, 80 életerővel rendelkező segítő egység.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     */
    public FriendlyEnemy(Engine eng, int startX, int startY) {
        super(eng, "FriendlyEnemy", startX, startY, eng.getTileSize(), eng.getTileSize(), -10);
        setHealth(80);
        maxHealth = 80;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new FriendlyBehavior(getWorldX(), getWorldY());
    }
}