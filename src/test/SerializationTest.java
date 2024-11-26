package test;

import entity.enemy.DragonEnemy;
import entity.enemy.GiantEnemy;
import entity.enemy.SmallEnemy;
import main.Engine;
import main.logger.GameLogger;
import object.OBJ_Chest;
import object.OBJ_Door;
import object.OBJ_Key;
import object.OBJ_Sword;
import org.junit.jupiter.api.*;
import serializable.FileManager;
import serializable.GameMetadata;
import serializable.SerializableObjectState;
import serializable.SerializablePlayerState;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game Serialization System Tests")
class SerializationTest {
    private Engine engine;
    private Path tempDir;
    private static final String SAVE_FILE = "test_save.sav";

    @BeforeEach
    void setUp() throws IOException {
        engine = new Engine();
        tempDir = Files.createTempDirectory("game-saves");

        Files.createDirectories(tempDir.resolve("res/save"));

        initializeTestGameState();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        GameLogger.error("SERIALIZATION TEST", "IOException", e);
                    }
                });
    }

    private void initializeTestGameState() {
        engine.setGameMode(Engine.GameMode.STORY);
        engine.setGameDifficulty(Engine.GameDifficulty.MEDIUM);
        engine.setStoryLevel(2);

        engine.player.setHealth(75);
        engine.player.setWorldX(100);
        engine.player.setWorldY(200);
        engine.player.setSpeed(4);
        engine.player.direction = "right";

        engine.player.getInventory().addItem(new OBJ_Sword(engine, 0, 0, 50));
        engine.player.getInventory().addItem(new OBJ_Key(engine, 0, 0));

        // Add entities
        engine.addEntity(new SmallEnemy(engine, 300, 300));
        engine.addEntity(new GiantEnemy(engine, 400, 400));
        engine.addEntity(new DragonEnemy(engine, 500, 500));

        engine.aSetter.list.add(new OBJ_Chest(engine, 600, 600));
        engine.aSetter.list.add(new OBJ_Door(engine, 700, 700));
    }

    @Nested
    @DisplayName("Save Game Tests")
    class SaveGameTests {

        @Test
        @DisplayName("Should save complete game state")
        void testCompleteGameSave() throws IOException {
            Path savePath = tempDir.resolve("res/save").resolve(SAVE_FILE);
            FileManager.saveGameState(engine, savePath.toString());

            assertTrue(Files.exists(savePath));
            assertTrue(Files.size(savePath) > 0);
        }

        @Test
        @DisplayName("Should save game metadata correctly")
        void testGameMetadataSave() throws IOException, ClassNotFoundException {
            Path savePath = tempDir.resolve("res/save").resolve(SAVE_FILE);
            FileManager.saveGameState(engine, savePath.toString());

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savePath.toFile()))) {
                GameMetadata metadata = (GameMetadata) ois.readObject();

                assertEquals(Engine.GameMode.STORY, metadata.gameMode);
                assertEquals(Engine.GameDifficulty.MEDIUM, metadata.difficulty);
                assertEquals(2, metadata.currentStoryLevel);
            }
        }

        @Test
        @DisplayName("Should save player state correctly")
        void testPlayerStateSave() throws IOException, ClassNotFoundException {
            Path savePath = tempDir.resolve("res/save").resolve(SAVE_FILE);
            FileManager.saveGameState(engine, savePath.toString());

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savePath.toFile()))) {
                ois.readObject(); // Skip metadata
                SerializablePlayerState playerState = (SerializablePlayerState) ois.readObject();

                assertEquals(75, playerState.health);
                assertEquals(100, playerState.worldX);
                assertEquals(200, playerState.worldY);
                assertEquals(4, playerState.speed);
                assertEquals("right", playerState.direction);
                assertEquals(2, playerState.inventoryState.size());
            }
        }

        @Test
        @DisplayName("Should handle save errors gracefully")
        void testSaveErrorHandling() {
            Path invalidPath = tempDir.resolve("nonexistent/directory/save.sav");

            assertThrows(IOException.class, () ->
                    FileManager.saveGameState(engine, invalidPath.toString()));
        }
    }

    @Nested
    @DisplayName("Load Game Tests")
    class LoadGameTests {

        @BeforeEach
        void saveTestGame() throws IOException {
            Path savePath = tempDir.resolve("res/save").resolve(SAVE_FILE);
            FileManager.saveGameState(engine, savePath.toString());

            // Reset engine state
            engine = new Engine();
        }

        @Test
        @DisplayName("Should load complete game state")
        void testCompleteGameLoad() throws IOException, ClassNotFoundException {
            Path savePath = tempDir.resolve("res/save").resolve(SAVE_FILE);
            FileManager.loadGameState(engine, savePath.toString());

            assertEquals(Engine.GameMode.STORY, engine.getGameMode());
            assertEquals(Engine.GameDifficulty.MEDIUM, engine.getGameDifficulty());
            assertEquals(2, engine.getStoryLevel());
            assertEquals(75, engine.player.getHealth());
            assertEquals(4, engine.player.getSpeed());

            assertTrue(engine.getEntity().stream()
                    .anyMatch(e -> e instanceof SmallEnemy));
            assertTrue(engine.getEntity().stream()
                    .anyMatch(e -> e instanceof GiantEnemy));
            assertTrue(engine.getEntity().stream()
                    .anyMatch(e -> e instanceof DragonEnemy));

            assertTrue(engine.aSetter.list.stream()
                    .anyMatch(o -> o instanceof OBJ_Chest));
            assertTrue(engine.aSetter.list.stream()
                    .anyMatch(o -> o instanceof OBJ_Door));
        }

        @Test
        @DisplayName("Should load inventory correctly")
        void testInventoryLoad() throws IOException, ClassNotFoundException {
            Path savePath = tempDir.resolve("res/save").resolve(SAVE_FILE);
            FileManager.loadGameState(engine, savePath.toString());

            assertEquals(2, engine.player.getInventory().getItems().size());
            assertTrue(engine.player.getInventory().getItems().stream()
                    .anyMatch(item -> item instanceof OBJ_Sword));
            assertTrue(engine.player.getInventory().getItems().stream()
                    .anyMatch(item -> item instanceof OBJ_Key));
        }

        @Test
        @DisplayName("Should handle corrupted save files")
        void testCorruptedSaveHandling() throws IOException {
            Path savePath = tempDir.resolve("res/save").resolve("corrupted.sav");
            try (FileOutputStream fos = new FileOutputStream(savePath.toFile())) {
                fos.write("corrupted data".getBytes());
            }

            assertThrows(IOException.class, () ->
                    FileManager.loadGameState(engine, savePath.toString()));
        }

        @Test
        @DisplayName("Should handle missing save files")
        void testMissingSaveHandling() {
            Path nonexistentPath = tempDir.resolve("res/save").resolve("nonexistent.sav");

            assertThrows(IOException.class, () ->
                    FileManager.loadGameState(engine, nonexistentPath.toString()));
        }
    }

    @Nested
    @DisplayName("Serializable Object Tests")
    class SerializableObjectTests {

        @Test
        @DisplayName("Should serialize weapon state correctly")
        void testWeaponSerialization() {
            OBJ_Sword sword = new OBJ_Sword(engine, 100, 100, 50);
            sword.setDurability(75);

            SerializableObjectState state = new SerializableObjectState(sword);

            assertEquals("sword", state.name);
            assertEquals(100, state.worldX);
            assertEquals(100, state.worldY);
            assertEquals(75, state.durability);
        }

        @Test
        @DisplayName("Should serialize chest state correctly")
        void testChestSerialization() {
            OBJ_Chest chest = new OBJ_Chest(engine, 100, 100);
            chest.opened = true;

            SerializableObjectState state = new SerializableObjectState(chest);

            assertEquals("chest", state.name);
            assertEquals(100, state.worldX);
            assertEquals(100, state.worldY);
            assertTrue(state.opened);
        }
    }

    @Test
    @DisplayName("Should handle version compatibility")
    void testVersionCompatibility() {
        assertDoesNotThrow(() -> {
            Class.forName("serializable.GameMetadata").getDeclaredField("serialVersionUID");
            Class.forName("serializable.SerializablePlayerState").getDeclaredField("serialVersionUID");
            Class.forName("serializable.SerializableInventoryItem").getDeclaredField("serialVersionUID");
            Class.forName("serializable.SerializableTileState").getDeclaredField("serialVersionUID");
            Class.forName("serializable.SerializableEntityState").getDeclaredField("serialVersionUID");
            Class.forName("serializable.SerializableObjectState").getDeclaredField("serialVersionUID");
        });
    }
}