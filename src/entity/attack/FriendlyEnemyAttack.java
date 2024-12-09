package entity.attack;

import entity.Entity;
import entity.enemy.Enemy;
import entity.enemy.FriendlyEnemy;
import main.Engine;

/**
 * A barátságos ellenség támadását reprezentáló osztály.
 * Csak az ellenséges egységeket sebzi, a játékost nem.
 */
public class FriendlyEnemyAttack extends Attack {

    /**
     * Létrehoz egy új barátságos támadás objektumot.
     * Az alap támadásnál nagyobb sebességgel rendelkezik.
     *
     * @param eng a játékmotor példánya
     * @param startX kezdő X koordináta
     * @param startY kezdő Y koordináta
     * @param targetX célpont X koordinátája
     * @param targetY célpont Y koordinátája
     */
    public FriendlyEnemyAttack(Engine eng, int startX, int startY, int targetX, int targetY) {
        super(eng, "FriendlyEnemyAttack", 10, startX, startY, targetX, targetY);
        image1 = scale("objects",name+"1");
        image2 = scale("objects",name+"2");
        image = image1;
        setSpeed(getSpeed()+2);
    }

    /**
     * Frissíti a támadás állapotát.
     * Felülírja az alap update metódust, hogy csak az ellenséges egységekkel
     * történő ütközést kezelje.
     */
    @Override
    public void update() {
        super.update();
        for (Entity entity : eng.getEntity()) {
            if (entity instanceof Enemy && !(entity instanceof FriendlyEnemy)) {
                if (entity.solidArea.intersects(this.solidArea)) {
                    (entity).setHealth(entity.getHealth() - damage);
                    eng.removeEnemy(this);
                    return;
                }
            }
        }
    }
}