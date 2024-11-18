package serializable;

import entity.*;
import entity.enemy.*;
import entity.npc.NPC_Wayfarer;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import main.*;
import main.logger.GameLogger;
import object.*;
import tile.TileManager;

import javax.swing.*;

public class FileManager {
    private static final String LOG_CONTEXT = "[FILE MANAGER]";
    private static final String SAVE_EXTENSION = ".sav";

    private FileManager() {}

    // Updated entity creators map with all entity types
    private static final Map<String, BiFunction<Engine, SerializableEntityState, Entity>> entityCreators = Map.of(
            "NPC_Wayfarer", (gp, state) -> createEntity(new NPC_Wayfarer(gp), state),
            "DragonEnemy", (gp, state) -> createEntity(new DragonEnemy(gp, state.worldX, state.worldY), state),
            "SmallEnemy", (gp, state) -> createEntity(new SmallEnemy(gp, state.worldX, state.worldY), state),
            "GiantEnemy", (gp, state) -> createEntity(new GiantEnemy(gp, state.worldX, state.worldY), state)
    );

    // Updated object creators map with all object types
    private static final Map<String, BiFunction<Engine, SerializableObjectState, SuperObject>> objectCreators = Map.of(
            "key", (gp, state) -> createObject(new OBJ_Key(gp, state.worldX, state.worldY), state),
            "door", (gp, state) -> createObject(new OBJ_Door(gp, state.worldX, state.worldY), state),
            "chest", (gp, state) -> createObject(new OBJ_Chest(gp, state.worldX, state.worldY), state),
            "boots", (gp, state) -> createObject(new OBJ_Boots(gp, state.worldX, state.worldY), state),
            "sword", (gp, state) -> createObject(new OBJ_Sword(gp, state.worldX, state.worldY, state.damage), state)
    );

    public static void saveGameState(Engine gp, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            // Save game metadata
            GameMetadata metadata = new GameMetadata(
                    gp.getGameMode(),
                    gp.getGameDifficulty(),
                    gp.getStoryLevel()
            );
            oos.writeObject(metadata);

            // Save player state with inventory
            SerializablePlayerState playerState = new SerializablePlayerState(
                    gp.player,
                    gp.getGameDifficulty(),
                    serializeInventory(gp.player.getInventory())
            );
            oos.writeObject(playerState);

            // Save tile map state
            SerializableTileState tileState = new SerializableTileState(
                    TileManager.mapTileNum
            );
            oos.writeObject(tileState);

            // Save entities and objects based on game mode
            if (gp.getGameMode() == Engine.GameMode.STORY) {
                // Save all entities
                oos.writeObject(new ArrayList<>(gp.getEntity().stream()
                        .filter(Objects::nonNull)
                        .map(SerializableEntityState::new)
                        .toList()));

                // Save all objects
                oos.writeObject(new ArrayList<>(gp.aSetter.list.stream()
                        .filter(Objects::nonNull)
                        .map(SerializableObjectState::new)
                        .toList()));
            }
        }
    }

    public static void loadGameState(Engine gp, String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            // Load game metadata
            GameMetadata metadata = (GameMetadata) ois.readObject();
            gp.setGameMode(metadata.gameMode);
            gp.setGameDifficulty(metadata.difficulty);
            gp.setStoryLevel(metadata.currentStoryLevel);

            // Load player state and inventory
            SerializablePlayerState playerState = (SerializablePlayerState) ois.readObject();
            updatePlayerState(gp.player, playerState);
            deserializeInventory(gp.player.getInventory(), playerState.inventoryState, gp);

            // Load tile map state
            SerializableTileState tileState = (SerializableTileState) ois.readObject();
            TileManager.mapTileNum = tileState.mapTileNum;

            // Load entities and objects for story mode
            if (metadata.gameMode == Engine.GameMode.STORY) {
                List<SerializableEntityState> entityStates = (List<SerializableEntityState>) ois.readObject();
                gp.setEntities(entityStates.stream()
                        .map(state -> createEntityFromState(gp, state)).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new)));

                List<SerializableObjectState> objectStates = (List<SerializableObjectState>) ois.readObject();
                gp.aSetter.list = objectStates.stream()
                        .map(state -> createObjectFromState(gp, state)).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
            }
        }
    }

    private static List<SerializableInventoryItem> serializeInventory(Inventory inventory) {
        return inventory.getItems().stream()
                .map(item -> new SerializableInventoryItem(
                        item.name,
                        item.getDurability(),
                        item.getMaxDurability(),
                        item instanceof Weapon ? ((Weapon) item).getDamage() : 0
                ))
                .toList();
    }

    private static void deserializeInventory(Inventory inventory, List<SerializableInventoryItem> items, Engine gp) {
        inventory.getItems().clear();
        for (SerializableInventoryItem item : items) {
            SuperObject obj = switch (item.name) {
                case "sword" -> new OBJ_Sword(gp, 0, 0, item.damage);
                case "boots" -> new OBJ_Boots(gp, 0, 0);
                case "key" -> new OBJ_Key(gp, 0, 0);
                default -> null;
            };
            if (obj != null) {
                if (obj instanceof Wearable || obj instanceof Weapon) {
                    obj.setDurability(item.durability);
                    obj.setMaxDurability(item.maxDurability);
                }
                inventory.addItem(obj);
            }
        }
    }

    private static Entity createEntity(Entity entity, SerializableEntityState state) {
        if (entity != null) {
            entity.setWorldX(state.worldX);
            entity.setWorldY(state.worldY);
            entity.setSpeed(state.speed);
            entity.direction = state.direction;
            entity.setHealth(state.health);
        }
        return entity;
    }

    private static SuperObject createObject(SuperObject obj, SerializableObjectState state) {
        if (obj != null) {
            obj.worldX = state.worldX;
            obj.worldY = state.worldY;
            obj.collision = state.collision;
            obj.opened = state.opened;
            if (obj.opened && obj instanceof OBJ_Chest) {
                obj.image = obj.image2;
            }
            if (obj instanceof Wearable || obj instanceof Weapon) {
                obj.setDurability(state.durability);
                obj.setMaxDurability(state.maxDurability);
            }
            if (obj instanceof Weapon) {
                ((Weapon) obj).setDamage(state.damage);
            }
        }
        return obj;
    }

    private static Entity createEntityFromState(Engine gp, SerializableEntityState state) {
        BiFunction<Engine, SerializableEntityState, Entity> creator = entityCreators.get(state.type);
        return creator != null ? creator.apply(gp, state) : null;
    }

    private static SuperObject createObjectFromState(Engine gp, SerializableObjectState state) {
        BiFunction<Engine, SerializableObjectState, SuperObject> creator = objectCreators.get(state.name);
        return creator != null ? creator.apply(gp, state) : null;
    }

    private static void updatePlayerState(Player player, SerializablePlayerState state) {
        player.setWorldX(state.worldX);
        player.setWorldY(state.worldY);
        player.setSpeed(state.speed);
        player.direction = state.direction;
        player.setHealth(state.health);
        player.setMaxHealth(state.maxHealth);
    }

    public static void saveGame(Engine gp) {
        GameLogger.info(LOG_CONTEXT, "SAVING PENDING");
        String filename = JOptionPane.showInputDialog(gp, "Enter a name for your save file:");

        if (filename != null && !filename.trim().isEmpty()) {
            try {
                File saveDir = new File("res/save");
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                File saveFile = new File(saveDir, filename + SAVE_EXTENSION);

                if (saveFile.exists()) {
                    int choice = JOptionPane.showConfirmDialog(gp,
                            "A save file with this name already exists. Do you want to overwrite it?",
                            "Overwrite Save",
                            JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION) {
                        GameLogger.info(LOG_CONTEXT, "SAVE CANCELLED");
                        return;
                    }
                }

                saveGameState(gp, saveFile.getPath());
                GameLogger.info(LOG_CONTEXT, "GAME SAVED SUCCESSFULLY");
                JOptionPane.showMessageDialog(gp, "Game saved successfully.");
            } catch (IOException e) {
                GameLogger.error(LOG_CONTEXT, "ERROR SAVING GAME: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(gp, "Error saving game: " + e.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            GameLogger.warn(LOG_CONTEXT, "SAVE CANCELLED");
        }
    }

    public static boolean loadGame(Engine gp) {
        GameLogger.info(LOG_CONTEXT, "LOAD PENDING");
        File saveDir = new File("res/save");
        if (!saveDir.exists() || saveDir.list() == null ||
                Objects.requireNonNull(saveDir.list()).length == 0) {
            JOptionPane.showMessageDialog(gp, "No save files found.",
                    "Load Game", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        String[] saveFiles = saveDir.list((dir, name) -> name.endsWith(SAVE_EXTENSION));
        if (saveFiles == null) {
            return false;
        }

        String selectedFile = (String) JOptionPane.showInputDialog(gp,
                "Choose a save file to load:",
                "Load Game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                saveFiles,
                saveFiles[0]);

        if (selectedFile != null) {
            try {
                loadGameState(gp, new File(saveDir, selectedFile).getPath());
                GameLogger.info(LOG_CONTEXT, "GAME LOADED SUCCESSFULLY");
                JOptionPane.showMessageDialog(gp, "Game loaded successfully.");
                return true;
            } catch (IOException | ClassNotFoundException e) {
                GameLogger.error(LOG_CONTEXT, "ERROR LOADING GAME: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(gp, "Error loading game: " + e.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            GameLogger.warn(LOG_CONTEXT, "LOAD CANCELLED");
        }
        return false;
    }
}

// Új osztályok a szerializációhoz
class GameMetadata implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    Engine.GameMode gameMode;
    Engine.GameDifficulty difficulty;
    int currentStoryLevel;

    GameMetadata(Engine.GameMode gameMode, Engine.GameDifficulty difficulty, int currentStoryLevel) {
        this.gameMode = gameMode;
        this.difficulty = difficulty;
        this.currentStoryLevel = currentStoryLevel;
    }
}

class SerializablePlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    int worldX;
    int worldY;
    int speed;
    int health;
    int maxHealth;
    String direction;
    List<SerializableInventoryItem> inventoryState;

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

class SerializableInventoryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String name;
    int durability;
    int maxDurability;
    int damage;  // csak fegyverek esetén használt

    SerializableInventoryItem(String name, int durability, int maxDurability, int damage) {
        this.name = name;
        this.durability = durability;
        this.maxDurability = maxDurability;
        this.damage = damage;
    }
}

class SerializableTileState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    int[][] mapTileNum;

    SerializableTileState(int[][] mapTileNum) {
        this.mapTileNum = mapTileNum;
    }
}

class SerializableEntityState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String type;
    int worldX;
    int worldY;
    int speed;
    int health;
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
    int worldX;
    int worldY;
    boolean collision;
    boolean opened;  // új mező a chest állapotának tárolásához
    int durability;
    int maxDurability;
    int damage;

    SerializableObjectState(SuperObject obj) {
        this.name = obj.name;
        this.worldX = obj.worldX;
        this.worldY = obj.worldY;
        this.collision = obj.collision;
        this.opened = obj.opened;  // chest állapot mentése
        this.durability = obj.getDurability();
        this.maxDurability = obj.getMaxDurability();
        this.damage = (obj instanceof Weapon) ? ((Weapon) obj).getDamage() : 0;
    }
}