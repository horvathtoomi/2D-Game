package object;

import java.awt.*;

/**
 * A fegyverek ritkaságát definiáló enum.
 */
public enum WeaponRarity {
    COMMON(1.0f, Color.GRAY),
    UNCOMMON(1.2f, Color.GREEN),
    RARE(1.5f, Color.BLUE),
    LEGENDARY(2.0f, Color.ORANGE);

    public final float damageMultiplier;
    public final Color color;

    /**
     * Létrehoz egy új ritkaság típust.
     * @param damageMultiplier sebzés szorzó
     * @param color fényeffekt színe
     */
    WeaponRarity(float damageMultiplier, Color color) {
        this.damageMultiplier = damageMultiplier;
        this.color = color;
    }
}
