package serializable;

import object.GameObject;

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

    public SerializableObjectState(GameObject obj) {
        this.name = obj.name;
        this.worldX = obj.getWorldX();
        this.worldY = obj.getWorldY();
        this.collision = obj.collision;
        // Other fields are not tracked by GameObject
        // These would need to be handled by specific object types if needed
        this.damage = 0;
    }
}