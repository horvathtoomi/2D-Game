package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import main.Engine;
import main.console.*;
import entity.enemy.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

class ConsoleSystemTest {
    private Engine engine;
    private ConsoleHandler consoleHandler;
    private Commands commands;
    private boolean messageReceived;
    private String lastMessage;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        engine = new Engine();
        consoleHandler = new ConsoleHandler(engine);
        commands = new Commands(engine, consoleHandler) {};
        messageReceived = false;
        lastMessage = "";

        // Ensure test directory structure exists
        new File(tempDir.toString() + "/res/save").mkdirs();
        new File(tempDir.toString() + "/res/scripts").mkdirs();
    }

    @Test
    void testRemoveEntities() {
        // Add test entities
        engine.addEntity(new SmallEnemy(engine, 100, 100));
        engine.addEntity(new GiantEnemy(engine, 200, 200));
        engine.addEntity(new DragonEnemy(engine, 300, 300));

        int initialSize = engine.getEntity().size();

        // Test removing specific entity type
        commands.removeEntities("SmallEnemy", false);
        assertEquals(initialSize - 1, engine.getEntity().size());

        // Test removing all entities
        commands.removeEntities("all", true);
        assertTrue(engine.getEntity().isEmpty());
    }

    @Test
    void testAddEntities() {
        // Test adding different entity types
        commands.add("smallenemy", 5, 5);
        commands.add("giantenemy", 10, 10);
        commands.add("dragonenemy", 15, 15);
        commands.add("friendlyenemy", 20, 20);

        assertEquals(4, engine.getEntity().size());

        // Test invalid coordinates
        commands.add("smallenemy", -1, -1);
        assertEquals(4, engine.getEntity().size());

        // Test invalid entity type
        commands.add("invalidenemy", 5, 5);
        assertEquals(4, engine.getEntity().size());
    }

    @Test
    void testAddObjects() {
        // Test adding different object types
        commands.add("key", 5, 5);
        commands.add("door", 10, 10);
        commands.add("chest", 15, 15);
        commands.add("boots", 20, 20);
        commands.add("sword", 25, 25);

        assertEquals(5, engine.aSetter.list.size());
    }

    @Test
    void testTeleport() {
        int initialX = engine.player.getWorldX();
        int initialY = engine.player.getWorldY();

        for(int i = 0; i < 12; i++) {
                engine.tileman.getTile(i).collision = false;
        }

        commands.teleport(11, 11);
        assertNotEquals(initialX, engine.player.getWorldX());
        assertNotEquals(initialY, engine.player.getWorldY());

        // Test invalid coordinates
        commands.teleport(-1, -1);
        commands.teleport(1000, 1000);
    }

    @Test
    void testSetCommands() {
        // Test setting player values
        commands.setGameValue("health", "50");
        assertEquals(50, engine.player.getHealth());

        commands.setGameValue("speed", "5");
        assertEquals(5, engine.player.getSpeed());

        // Test setting entity values
        commands.setAll("speed", 4);
        commands.setAll("health", 100);

        // Test setting specific entity
        engine.addEntity(new SmallEnemy(engine, 100, 100));
        commands.setEntity("SmallEnemy", "health", 75);
        commands.setEntity("SmallEnemy", "speed", 3);
    }

    @Test
    void testGetCommands() {
        // Test getting player values
        commands.getGameValue("health");
        assertTrue(messageReceived);

        commands.getGameValue("speed");
        assertTrue(messageReceived);

        // Test getting entity values
        engine.addEntity(new SmallEnemy(engine, 100, 100));
        commands.getEntity("smallenemy", "health");
        assertTrue(messageReceived);

        messageReceived = false;
        commands.getEntity("invalidenemy", "health");
        assertTrue(messageReceived);
        assertEquals("Entity not found", lastMessage);
    }

    @Test
    void testHelpCommand() {
        // Test general help
        commands.printHelp("");
        assertTrue(messageReceived);

        // Test specific command help
        String[] helpCommands = {"script", "make", "set", "get", "add", "remove",
                "teleport", "reset", "save", "load", "exit", "exit_game"};

        for (String cmd : helpCommands) {
            messageReceived = false;
            commands.printHelp(cmd);
            assertTrue(messageReceived, "Help not shown for: " + cmd);
        }
    }

    @Test
    void testSaveAndLoad() {
        // Create test save file
        File saveFile = new File("/res/save/test.sav");

        // Test save
        commands.saveFile("test");
        assertTrue(saveFile.exists());

        // Test load
        commands.loadFile("test");
        assertTrue(messageReceived);

        // Test invalid load
        commands.loadFile("nonexistent");
        assertEquals("nonexistent not found", lastMessage);
    }

    @Test
    void testScriptCommands() throws IOException {
        // Create test script file
        File scriptFile = new File(tempDir.toString() + "/res/scripts/test.txt");
        try (FileWriter writer = new FileWriter(scriptFile)) {
            writer.write("add smallenemy 10 10\n");
            writer.write("add giantenemy 20 20\n");
            writer.write("teleport 15 15\n");
        }

        // Test script execution
        commands.runScript("test");

        // Test creating script
        ConsoleGUI mockGui = new ConsoleGUI(engine, consoleHandler);
        commands.createFile("newscript", mockGui);
    }

    @Test
    void testGameStateCommands() {
        // Test game state changes
        assertEquals(Engine.GameState.START, engine.getGameState());

        engine.setGameState(Engine.GameState.RUNNING);
        assertEquals(Engine.GameState.RUNNING, engine.getGameState());

        // Test difficulty changes
        engine.setGameDifficulty(Engine.GameDifficulty.EASY);
        assertEquals(Engine.GameDifficulty.EASY, engine.getGameDifficulty());

        engine.setGameDifficulty(Engine.GameDifficulty.HARD);
        assertEquals(Engine.GameDifficulty.HARD, engine.getGameDifficulty());
    }
}