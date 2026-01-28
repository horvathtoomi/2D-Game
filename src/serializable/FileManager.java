package serializable;

import entity.Entity;
import entity.Inventory;
import entity.Player;
import entity.enemy.DragonEnemy;
import entity.enemy.GiantEnemy;
import entity.enemy.SmallEnemy;
import entity.npc.NPC_Wayfarer;
import main.Engine;
import main.logger.GameLogger;
import main.GameMode;
import object.*;
import tile.TileManager;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A FileManager osztály felelős a játék állapotának mentéséért és betöltéséért.
 * Kezeli a játék adatainak szerializációját és deszerializációját.
 */
public class FileManager {
    private static final String LOG_CONTEXT = "[FILE MANAGER]";
    private static final String SAVE_EXTENSION = ".sav";

    /**
     * Privát konstruktor a példányosítás megakadályozásához.
     * A FileManager csak statikus metódusokat tartalmaz.
     */
    private FileManager() {
    }

    /**
     * EntityCreator térkép az entitások létrehozásához.
     * Kulcs: entitás típus neve, Érték: entitás létrehozó függvény
     */
    private static final Map<String, BiFunction<Engine, SerializableEntityState, Entity>> entityCreators = Map.of(
            "NPC_Wayfarer", (eng, state) -> createEntity(new NPC_Wayfarer(eng), state),
            "DragonEnemy", (eng, state) -> createEntity(new DragonEnemy(eng, state.worldX, state.worldY), state),
            "SmallEnemy", (eng, state) -> createEntity(new SmallEnemy(eng, state.worldX, state.worldY), state),
            "GiantEnemy", (eng, state) -> createEntity(new GiantEnemy(eng, state.worldX, state.worldY), state));

    /**
     * ObjectCreator térkép az objektumok létrehozásához.
     * Kulcs: objektum típus neve, Érték: objektum létrehozó függvény
     */
    private static final Map<String, BiFunction<Engine, SerializableObjectState, GameObject>> objectCreators = Map.of(
            "key", (eng, state) -> createObject(new OBJ_Key(eng, state.worldX, state.worldY), state),
            "door", (eng, state) -> createObject(new OBJ_Door(eng, state.worldX, state.worldY), state),
            "chest", (eng, state) -> createObject(new OBJ_Chest(eng, state.worldX, state.worldY), state),
            "boots", (eng, state) -> createObject(new OBJ_Boots(eng, state.worldX, state.worldY), state),
            "sword", (eng, state) -> createObject(new OBJ_Sword(eng, state.worldX, state.worldY, state.damage), state));

    /**
     * Elmenti a játék aktuális állapotát egy fájlba.
     * 
     * @param eng      a játékmotor példánya
     * @param filename a mentési fájl neve
     * @throws IOException ha a fájl írása sikertelen
     */
    public static void saveGameState(Engine eng, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            GameMetadata metadata = new GameMetadata(
                    eng.getGameMode(),
                    eng.getGameDifficulty(),
                    eng.getStoryLevel());
            oos.writeObject(metadata);

            SerializablePlayerState playerState = new SerializablePlayerState(
                    eng.player,
                    serializeInventory(eng.player.getInventory()));
            oos.writeObject(playerState);

            SerializableTileState tileState = new SerializableTileState(
                    TileManager.mapTileNum);
            oos.writeObject(tileState);

            if (eng.getGameMode() == GameMode.STORY) {
                oos.writeObject(new ArrayList<>(eng.getEntity().stream()
                        .filter(Objects::nonNull)
                        .map(SerializableEntityState::new)
                        .toList()));

                oos.writeObject(new ArrayList<>(eng.aSetter.list.stream()
                        .filter(Objects::nonNull)
                        .map(SerializableObjectState::new)
                        .toList()));
            }
        }
    }

    /**
     * Betölti a játék állapotát egy mentési fájlból.
     * 
     * @param eng      a játékmotor példánya
     * @param filename a mentési fájl neve
     * @throws IOException            ha a fájl olvasása sikertelen
     * @throws ClassNotFoundException ha az objektumok deszerializációja sikertelen
     */
    public static void loadGameState(Engine eng, String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            GameMetadata metadata = (GameMetadata) ois.readObject();
            eng.setGameMode(metadata.gameMode);
            eng.setGameDifficulty(metadata.difficulty);
            eng.setStoryLevel(metadata.currentStoryLevel);

            SerializablePlayerState playerState = (SerializablePlayerState) ois.readObject();
            updatePlayerState(eng.player, playerState);
            deserializeInventory(eng.player.getInventory(), playerState.inventoryState, eng);

            SerializableTileState tileState = (SerializableTileState) ois.readObject();
            TileManager.mapTileNum = tileState.mapTileNum;

            if (metadata.gameMode == GameMode.STORY) {
                List<SerializableEntityState> entityStates = (List<SerializableEntityState>) ois.readObject();
                eng.setEntities(entityStates.stream()
                        .map(state -> createEntityFromState(eng, state)).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new)));

                List<SerializableObjectState> objectStates = (List<SerializableObjectState>) ois.readObject();
                eng.aSetter.list = objectStates.stream()
                        .map(state -> createObjectFromState(eng, state)).filter(Objects::nonNull)
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
            }
        }
    }

    /**
     * Szerializálja a leltár tartalmát.
     * 
     * @param inventory a szerializálandó leltár
     * @return a szerializált leltárelemek listája
     */
    private static List<SerializableInventoryItem> serializeInventory(Inventory inventory) {
        return inventory.getItems().stream()
                .map(item -> new SerializableInventoryItem(
                        item.getName(),
                        // Durability for BootsItem and SwordItem
                        (item instanceof BootsItem || item instanceof SwordItem)
                                ? ((item instanceof BootsItem) ? ((BootsItem) item).getDurability().getCurrent()
                                        : ((SwordItem) item).getDurability().getCurrent())
                                : 0,
                        (item instanceof BootsItem || item instanceof SwordItem)
                                ? ((item instanceof BootsItem) ? ((BootsItem) item).getDurability().getMax()
                                        : ((SwordItem) item).getDurability().getMax())
                                : 0,
                        item instanceof SwordItem ? ((SwordItem) item).getDamage() : 0,
                        item instanceof GunItem ? ((GunItem) item).getMagazine().getReserve() : 0,
                        item instanceof GunItem ? ((GunItem) item).getMagazine().getCurrentMag() : 0))
                .toList();
    }

    /**
     * Deszerializálja és helyreállítja a leltár tartalmát.
     * 
     * @param inventory a célként szolgáló leltár
     * @param items     a szerializált leltárelemek listája
     * @param eng       a játékmotor példánya
     */
    private static void deserializeInventory(Inventory inventory, List<SerializableInventoryItem> items, Engine eng) {
        inventory.getItems().clear();
        for (SerializableInventoryItem item : items) {
            Item obj = switch (item.name) {
                case "Sword" -> new SwordItem(eng, item.damage, WeaponRarity.COMMON);
                case "Boots" -> new BootsItem(eng);
                case "Key" -> new KeyItem();
                case "Pistol" -> new PistolItem(eng, item.inMagAmmo, item.leftoverAmmo);
                case "Rifle" -> new RifleItem(eng, item.inMagAmmo, item.leftoverAmmo);
                default -> null;
            };
            if (obj != null) {
                inventory.addItem(obj);
            }
        }
    }

    /**
     * Létrehoz egy entitást a szerializált állapotából.
     * 
     * @param entity az alapértelmezett entitás
     * @param state  a szerializált állapot
     * @return a helyreállított entitás
     */
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

    /**
     * Létrehoz egy objektumot a szerializált állapotából.
     * 
     * @param obj   az alapértelmezett objektum
     * @param state a szerializált állapot
     * @return a helyreállított objektum
     */
    private static GameObject createObject(GameObject obj, SerializableObjectState state) {
        if (obj != null) {
            obj.worldX = state.worldX;
            obj.worldY = state.worldY;
            // Durability/damage restoration removed - handled by constructor
        }
        return obj;
    }

    private static Entity createEntityFromState(Engine eng, SerializableEntityState state) {
        BiFunction<Engine, SerializableEntityState, Entity> creator = entityCreators.get(state.type);
        return creator != null ? creator.apply(eng, state) : null;
    }

    private static GameObject createObjectFromState(Engine eng, SerializableObjectState state) {
        BiFunction<Engine, SerializableObjectState, GameObject> creator = objectCreators.get(state.name);
        return creator != null ? creator.apply(eng, state) : null;
    }

    /**
     * Frissíti a játékos állapotát a szerializált adatok alapján.
     * 
     * @param player a frissítendő játékos
     * @param state  a szerializált játékos állapot
     */
    private static void updatePlayerState(Player player, SerializablePlayerState state) {
        player.setWorldX(state.worldX);
        player.setWorldY(state.worldY);
        player.setSpeed(state.speed);
        player.direction = state.direction;
        player.setHealth(state.health);
        player.setMaxHealth(state.maxHealth);
    }

    /**
     * Felhasználói felülettel ellátott mentési funkció.
     * Megjeleníti a mentési párbeszédablakot és kezeli a felhasználói interakciót.
     * 
     * @param eng a játékmotor példánya
     */
    public static void saveGame(Engine eng) {
        GameLogger.info(LOG_CONTEXT, "SAVING PENDING");
        String filename = JOptionPane.showInputDialog(eng, "Enter a name for your save file:");

        if (filename != null && !filename.trim().isEmpty()) {
            try {
                File saveDir = new File("res/save");
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                File saveFile = new File(saveDir, filename + SAVE_EXTENSION);

                if (saveFile.exists()) {
                    int choice = JOptionPane.showConfirmDialog(eng,
                            "A save file with this name already exists. Do you want to overwrite it?",
                            "Overwrite Save",
                            JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION) {
                        GameLogger.info(LOG_CONTEXT, "SAVE CANCELLED");
                        return;
                    }
                }

                saveGameState(eng, saveFile.getPath());
                GameLogger.info(LOG_CONTEXT, "GAME SAVED SUCCESSFULLY");
                JOptionPane.showMessageDialog(eng, "Game saved successfully.");
            } catch (IOException e) {
                GameLogger.error(LOG_CONTEXT, "ERROR SAVING GAME: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(eng, "Error saving game: " + e.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            GameLogger.warn(LOG_CONTEXT, "SAVE CANCELLED");
        }
    }

    /**
     * Felhasználói felülettel ellátott betöltési funkció.
     * Megjeleníti a betöltési párbeszédablakot és kezeli a felhasználói
     * interakciót.
     * 
     * @param eng a játékmotor példánya
     * @return true ha a betöltés sikeres volt, false egyébként
     */
    public static boolean loadGame(Engine eng) {
        GameLogger.info(LOG_CONTEXT, "LOAD PENDING");
        File saveDir = new File("res/save");
        if (!saveDir.exists() || saveDir.list() == null ||
                Objects.requireNonNull(saveDir.list()).length == 0) {
            JOptionPane.showMessageDialog(eng, "No save files found.",
                    "Load Game", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        String[] saveFiles = saveDir.list((dir, name) -> name.endsWith(SAVE_EXTENSION));
        if (saveFiles == null) {
            return false;
        }

        String selectedFile = (String) JOptionPane.showInputDialog(eng,
                "Choose a save file to load:",
                "Load Game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                saveFiles,
                saveFiles[0]);

        if (selectedFile != null) {
            try {
                loadGameState(eng, new File(saveDir, selectedFile).getPath());
                GameLogger.info(LOG_CONTEXT, "GAME LOADED SUCCESSFULLY");
                JOptionPane.showMessageDialog(eng, "Game loaded successfully.");
                return true;
            } catch (IOException | ClassNotFoundException e) {
                GameLogger.error(LOG_CONTEXT, "ERROR LOADING GAME: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(eng, "Error loading game: " + e.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            GameLogger.warn(LOG_CONTEXT, "LOAD CANCELLED");
        }
        return false;
    }
}