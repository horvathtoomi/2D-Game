package serializable;

import entity.enemy.DragonEnemy;
import entity.enemy.GiantEnemy;
import entity.enemy.SmallEnemy;
import entity.npc.NPC_Wayfarer;
import main.*;
import object.*;
import entity.*;
import object.SuperObject;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class FileManager {

    private static final Map<String, BiFunction<GamePanel, SerializableEntityState, Entity>> entityCreators = Map.of(
            "NPC_Wayfarer", (gp, state) -> new NPC_Wayfarer(gp),
            "DragonEnemy", (gp, state) -> new DragonEnemy(gp, state.worldX, state.worldY),
            "SmallEnemy", (gp, state) -> new SmallEnemy(gp, state.worldX, state.worldY),
            "GiantEnemy", (gp, state) -> new GiantEnemy(gp, state.worldX, state.worldY)
    );

    private static final Map<String, BiFunction<GamePanel, SerializableObjectState, SuperObject>> objectCreators = Map.of(
            "key", (gp, state) -> new OBJ_Key(gp, state.worldX, state.worldY),
            "door", (gp, state) -> new OBJ_Door(gp, state.worldX, state.worldY),
            "chest", (gp, state) -> new OBJ_Chest(gp, state.worldX, state.worldY),
            "boots", (gp, state) -> new OBJ_Boots(gp, state.worldX, state.worldY)
    );

    public static void saveGameState(GamePanel gp, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new SerializablePlayerState(gp.player));
            oos.writeObject(new ArrayList<>(gp.entities.stream().filter(Objects::nonNull).map(SerializableEntityState::new).collect(Collectors.toList())));
            oos.writeObject(new ArrayList<>(gp.aSetter.list.stream().filter(Objects::nonNull).map(SerializableObjectState::new).collect(Collectors.toList())));
        }
    }

    public static void loadGameState(GamePanel gp, String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            SerializablePlayerState playerState = (SerializablePlayerState) ois.readObject();
            updatePlayerState(gp.player, playerState);
            List<SerializableEntityState> entityStates = (List<SerializableEntityState>) ois.readObject();
            gp.entities = entityStates.stream().map(state -> createEntityFromState(gp, state)).filter(Objects::nonNull).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
            List<SerializableObjectState> objectStates = (List<SerializableObjectState>) ois.readObject();
            gp.aSetter.list = objectStates.stream().map(state -> createObjectFromState(gp, state)).filter(Objects::nonNull).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        }
    }

    private static void updatePlayerState(Player player, SerializablePlayerState state) {
        player.setWorldX(state.worldX);
        player.setWorldY(state.worldY);
        player.setSpeed(state.speed);
        player.direction = state.direction;
        player.setHealth(state.health);
    }

    private static Entity createEntityFromState(GamePanel gp, SerializableEntityState state) {
        BiFunction<GamePanel, SerializableEntityState, Entity> creator = entityCreators.get(state.type);
        if (creator == null) {
            return null;
        }
        Entity entity = creator.apply(gp, state);
        if (entity != null) {
            entity.setWorldX(state.worldX);
            entity.setWorldY(state.worldY);
            entity.setSpeed(state.speed);
            entity.direction = state.direction;
            entity.setHealth(state.health);
        }
        return entity;
    }

    private static SuperObject createObjectFromState(GamePanel gp, SerializableObjectState state) {
        BiFunction<GamePanel, SerializableObjectState, SuperObject> creator = objectCreators.get(state.name);
        if (creator == null) {
            return null;
        }
        SuperObject obj = creator.apply(gp, state);
        if (obj != null) {
            obj.worldX = state.worldX;
            obj.worldY = state.worldY;
            obj.collision = state.collision;
        }
        return obj;
    }

}

class SerializablePlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    int worldX, worldY, speed, health;
    String direction;

    SerializablePlayerState(Player player) {
        this.worldX = player.getWorldX();
        this.worldY = player.getWorldY();
        this.speed = player.getSpeed();
        this.direction = player.direction;
        this.health = player.getHealth();
    }
}

class SerializableEntityState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String type;
    int worldX, worldY, speed, health;
    String direction;

    SerializableEntityState(Entity entity) {
        this.type = entity.getClass().getSimpleName();
        this.worldX = entity.getWorldX();
        this.worldY = entity.getWorldY();
        this.speed = entity.getSpeed();
        this.direction = entity.direction;
        this.health = entity.getHealth();
    }
}

class SerializableObjectState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String name;
    int worldX, worldY;
    boolean collision;

    SerializableObjectState(SuperObject obj) {
        this.name = obj.name;
        this.worldX = obj.worldX;
        this.worldY = obj.worldY;
        this.collision = obj.collision;
    }

}