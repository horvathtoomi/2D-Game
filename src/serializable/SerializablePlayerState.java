package serializable;

import entity.Player;
import main.Engine;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SerializablePlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public int worldX;
    public int worldY;
    public int speed;
    public int health;
    public int maxHealth;
    public String direction;
    public List<SerializableInventoryItem> inventoryState;

    SerializablePlayerState(Player player, Engine.GameDifficulty difficulty,
                            List<SerializableInventoryItem> inventoryState) {
        this.worldX = player.getWorldX();
        this.worldY = player.getWorldY();
        this.speed = player.getSpeed();
        this.direction = player.direction;
        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
        this.inventoryState = inventoryState;
    }
}