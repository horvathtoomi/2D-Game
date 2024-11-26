package entity.enemy;

import main.Engine;

/**
 * A kis méretű ellenség.
 * Alacsony életerővel és gyenge támadással rendelkező gyors ellenség.
 */
public class SmallEnemy extends Enemy {

    /**
     * Létrehoz egy új kis ellenséget.
     * Kis méretű, 100 életerővel rendelkező gyors egység.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     */
    public SmallEnemy(Engine eng, int startX, int startY) {
        super(eng, "SmallEnemy", startX, startY, (int)(eng.getTileSize() * 1.5), (int)(eng.getTileSize() * 1.5), 25);
        setHealth(100);
        maxHealth = 100;
    }

    @Override
    protected void initializeBehavior() {
        behavior = new PatrolBehavior(getWorldX(), getWorldY(), 5 * eng.getTileSize());
    }
}
