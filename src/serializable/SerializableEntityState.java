package serializable;

import entity.Direction;
import entity.Entity;

import java.io.Serial;
import java.io.Serializable;

class SerializableEntityState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String type;
    int worldX;
    int worldY;
    int speed;
    int health;
    Direction direction;

    SerializableEntityState(Entity entity) {
        this.type = entity.getClass().getSimpleName();
        this.worldX = entity.getWorldX();
        this.worldY = entity.getWorldY();
        this.speed = entity.getSpeed();
        this.direction = entity.direction;
        this.health = entity.getHealth();
    }
}