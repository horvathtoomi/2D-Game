package object;

import main.Engine;

import java.awt.*;

/**
 * A kard tárgy osztálya, amely támadásra használható.
 */
public class OBJ_Sword extends Weapon {

    private static final int swordUsageDamage = 2;

    /**
     * Létrehoz egy új kard objektumot.
     * @param eng játékmotor példány
     * @param x kezdő X pozíció
     * @param y kezdő Y pozíció
     * @param baseDamage alap sebzés
     */
    public OBJ_Sword(Engine eng, int x, int y, int baseDamage) {
        super(eng, x, y, "sword", "sword", baseDamage, 1, 30); // 30 frames cooldown (0.5 seconds at 60 FPS)
        setMaxDurability(200);
        setDurability(getMaxDurability());
        setUsageDamage(swordUsageDamage);
        this.damage = (int)(baseDamage * rarity.damageMultiplier);
        image1 = scale("sword1");
        image2 = scale("sword2");
        image = image1;
        hitbox = new Rectangle(0,0,32,eng.getTileSize());
    }

}