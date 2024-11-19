package serializable;

import java.io.Serial;
import java.io.Serializable;

public class SerializableInventoryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String name;
    int durability;
    int maxDurability;
    int damage;

    SerializableInventoryItem(String name, int durability, int maxDurability, int damage) {
        this.name = name;
        this.durability = durability;
        this.maxDurability = maxDurability;
        this.damage = damage;
    }
}