package entity.attack;

import main.Engine;

/**
 * A sárkány támadását reprezentáló osztály.
 * Nagy sebzésű, közepes sebességű támadás.
 */
public class DragonEnemyAttack extends Attack {

    /**
     * Létrehoz egy új sárkány támadás objektumot.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     * @param targetX célpont X koordinátája
     * @param targetY célpont Y koordinátája
     */
    public DragonEnemyAttack(Engine eng, int startX, int startY, int targetX, int targetY) {
        super(eng,"DragonEnemyAttack",25,startX,startY,targetX,targetY);
    }

}
