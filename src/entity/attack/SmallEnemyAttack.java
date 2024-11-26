package entity.attack;

import main.Engine;

/**
 * A kis ellenség támadását reprezentáló osztály.
 * Alacsony sebzésű, gyors támadás.
 */
public class SmallEnemyAttack extends Attack {

    /**
     * Létrehoz egy új kis ellenség támadás objektumot.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     * @param targetX célpont X koordinátája
     * @param targetY célpont Y koordinátája
     */
    public SmallEnemyAttack(Engine eng, int startX, int startY, int targetX, int targetY) {
        super(eng,"SmallEnemyAttack",0, startX, startY, targetX, targetY);
    }

}
