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

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        engine = new Engine();
        consoleHandler = new ConsoleHandler(engine);
        commands = new Commands(engine, consoleHandler) {};

        new File(tempDir.toString() + "/res/save").mkdirs();
        new File(tempDir.toString() + "/res/scripts").mkdirs();
    }

    @Test
    void testRemoveEntities() {
        engine.addEntity(new SmallEnemy(engine, 100, 100));
        engine.addEntity(new GiantEnemy(engine, 200, 200));
        engine.addEntity(new DragonEnemy(engine, 300, 300));

        int initialSize = engine.getEntity().size();

        commands.removeEntities("SmallEnemy", false);
        assertEquals(initialSize - 1, engine.getEntity().size());

        commands.removeEntities("all", true);
        assertTrue(engine.getEntity().isEmpty());
    }

    @Test
    void testAddEntities() {
        commands.add("smallenemy", 5, 5);
        commands.add("giantenemy", 10, 10);
        commands.add("dragonenemy", 15, 15);
        commands.add("friendlyenemy", 20, 20);

        assertEquals(4, engine.getEntity().size());

        commands.add("smallenemy", -1, -1);
        assertEquals(4, engine.getEntity().size());

        commands.add("invalidenemy", 5, 5);
        assertEquals(4, engine.getEntity().size());
    }

    @Test
    void testAddObjects() {
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

        commands.teleport(-1, -1);
        commands.teleport(1000, 1000);
    }

    @Test
    void testSetCommands() {
        commands.setGameValue("health", "50");
        assertEquals(50, engine.player.getHealth());

        commands.setGameValue("speed", "5");
        assertEquals(5, engine.player.getSpeed());

        commands.setAll("speed", 4);
        commands.setAll("health", 100);

        engine.addEntity(new SmallEnemy(engine, 100, 100));
        commands.setEntity("SmallEnemy", "health", 75);
        commands.setEntity("SmallEnemy", "speed", 3);
    }

    @Test
    void testScriptCommands() throws IOException {
        File scriptFile = new File(tempDir.toString() + "/res/scripts/test.txt");
        try (FileWriter writer = new FileWriter(scriptFile)) {
            writer.write("add smallenemy 10 10\n");
            writer.write("add giantenemy 20 20\n");
            writer.write("teleport 15 15\n");
        }

        commands.runScript("test");

        ConsoleGUI mockGui = new ConsoleGUI(engine, consoleHandler);
        commands.createFile("newscript", mockGui);
    }

    @Test
    void testGameStateCommands() {
        assertEquals(Engine.GameState.START, engine.getGameState());

        engine.setGameState(Engine.GameState.RUNNING);
        assertEquals(Engine.GameState.RUNNING, engine.getGameState());

        engine.setGameDifficulty(Engine.GameDifficulty.EASY);
        assertEquals(Engine.GameDifficulty.EASY, engine.getGameDifficulty());

        engine.setGameDifficulty(Engine.GameDifficulty.HARD);
        assertEquals(Engine.GameDifficulty.HARD, engine.getGameDifficulty());
    }
}