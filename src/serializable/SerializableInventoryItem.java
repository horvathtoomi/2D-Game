package serializable;

import java.io.Serial;
import java.io.Serializable;

public class SerializableInventoryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String name;
    int durability;
    int maxDurability;
    int leftoverAmmo;
    int inMagAmmo;
    int damage;

    SerializableInventoryItem(String name, int durability, int maxDurability, int damage, int leftoverAmmo, int inMagAmmo) {
        this.name = name;
        this.durability = durability;
        this.maxDurability = maxDurability;
        this.damage = damage;
        this.leftoverAmmo = leftoverAmmo;
        this.inMagAmmo = inMagAmmo;
    }
}