package serializable;

import object.SuperObject;
import object.Weapon;

import java.io.Serial;
import java.io.Serializable;

public class SerializableObjectState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String name;
    public int worldX;
    public int worldY;
    public boolean collision;
    public boolean opened;
    public int durability;
    public int maxDurability;
    public int damage;

    public SerializableObjectState(SuperObject obj) {
        this.name = obj.name;
        this.worldX = obj.worldX;
        this.worldY = obj.worldY;
        this.collision = obj.collision;
        this.opened = obj.opened;
        this.durability = obj.getDurability();
        this.maxDurability = obj.getMaxDurability();
        this.damage = (obj instanceof Weapon) ? ((Weapon) obj).getDamage() : 0;
    }
}