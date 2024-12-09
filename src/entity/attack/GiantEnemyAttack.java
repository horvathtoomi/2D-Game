package entity.attack;

import main.Engine;

/**
 * Az óriás ellenség támadását reprezentáló osztály.
 * Nagy sebzésű, lassú támadás.
 */
public class GiantEnemyAttack extends Attack {

    /**
     * Létrehoz egy új óriás támadás objektumot.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     * @param targetX célpont X koordinátája
     * @param targetY célpont Y koordinátája
     */
    public GiantEnemyAttack(Engine eng, int startX, int startY, int targetX, int targetY) {
        super(eng,"GiantEnemyAttack",100,startX,startY,targetX,targetY);
        image1 = scale("objects",name+"1");
        image2 = scale("objects",name+"2");
        image = image1;
    }

}
