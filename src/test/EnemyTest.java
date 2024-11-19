package test;

import static org.junit.jupiter.api.Assertions.*;

import entity.attack.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import main.Engine;
import main.Button;
import entity.enemy.*;
import object.*;
import java.awt.Point;
import java.awt.Color;


class EnemyTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Test
    void testSmallEnemyInitialization() {
        SmallEnemy smallEnemy = new SmallEnemy(engine, 100, 100);
        assertEquals(100, smallEnemy.getHealth());
        assertEquals(100, smallEnemy.getMaxHealth());
        assertEquals("SmallEnemy", smallEnemy.getName());
        assertEquals(100, smallEnemy.getWorldX());
        assertEquals(100, smallEnemy.getWorldY());
    }

    @Test
    void testGiantEnemyInitialization() {
        GiantEnemy giantEnemy = new GiantEnemy(engine, 100, 100);
        assertEquals(500, giantEnemy.getHealth());
        assertEquals(500, giantEnemy.getMaxHealth());
        assertEquals("GiantEnemy", giantEnemy.getName());
    }

    @Test
    void testDragonEnemyInitialization() {
        DragonEnemy dragonEnemy = new DragonEnemy(engine, 100, 100);
        assertEquals(250, dragonEnemy.getHealth());
        assertEquals(250, dragonEnemy.getMaxHealth());
        assertEquals("DragonEnemy", dragonEnemy.getName());
    }

    @Test
    void testFriendlyEnemyInitialization() {
        FriendlyEnemy friendlyEnemy = new FriendlyEnemy(engine, 100, 100);
        assertEquals(80, friendlyEnemy.getHealth());
        assertEquals(80, friendlyEnemy.getMaxHealth());
        assertEquals("FriendlyEnemy", friendlyEnemy.getName());
    }

    @Test
    void testEnemyDamage() {
        SmallEnemy enemy = new SmallEnemy(engine, 100, 100);
        int initialHealth = enemy.getHealth();
        enemy.setHealth(initialHealth - 30);
        assertEquals(initialHealth - 30, enemy.getHealth());
    }
}

class ButtonTest {
    private Button button;

    @BeforeEach
    void setUp() {
        button = new Button(100, 100, 200, 50, "Test Button");
    }

    @Test
    void testButtonContains() {
        assertTrue(button.contains(new Point(150, 125)));

        assertFalse(button.contains(new Point(50, 125)));
        assertFalse(button.contains(new Point(350, 125)));
        assertFalse(button.contains(new Point(150, 75)));
        assertFalse(button.contains(new Point(150, 175)));
    }

    @Test
    void testButtonColor() {
        Color newColor = new Color(255, 0, 0);
        button.setBackgroundColor(newColor);
        assertDoesNotThrow(() -> button.setBackgroundColor(newColor));
    }
}

class AttackTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Test
    void testDragonEnemyAttack() {
        DragonEnemyAttack attack = new DragonEnemyAttack(engine, 100, 100, 200, 200);
        assertNotNull(attack);
        assertTrue(attack.damage > 0);
        assertEquals(100, attack.getWorldX());
        assertEquals(100, attack.getWorldY());
    }

    @Test
    void testSmallEnemyAttack() {
        SmallEnemyAttack attack = new SmallEnemyAttack(engine, 100, 100, 200, 200);
        assertNotNull(attack);
        assertEquals(100, attack.getWorldX());
        assertEquals(100, attack.getWorldY());
    }

    @Test
    void testGiantEnemyAttack() {
        GiantEnemyAttack attack = new GiantEnemyAttack(engine, 100, 100, 200, 200);
        assertNotNull(attack);
        assertTrue(attack.damage > 0);
        assertEquals(100, attack.getWorldX());
        assertEquals(100, attack.getWorldY());
    }
}

class ObjectTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Test
    void testKeyInitialization() {
        OBJ_Key key = new OBJ_Key(engine, 100, 100);
        assertEquals("key", key.name);
        assertEquals(100, key.worldX);
        assertEquals(100, key.worldY);
        assertFalse(key.collision);
    }

    @Test
    void testDoorInitialization() {
        OBJ_Door door = new OBJ_Door(engine, 100, 100);
        assertEquals("door", door.name);
        assertEquals(100, door.worldX);
        assertEquals(100, door.worldY);
        assertTrue(door.collision);
        assertNotNull(door.image2); // Opened door image
    }

    @Test
    void testChestInitialization() {
        OBJ_Chest chest = new OBJ_Chest(engine, 100, 100);
        assertEquals("chest", chest.name);
        assertEquals(100, chest.worldX);
        assertEquals(100, chest.worldY);
        assertFalse(chest.opened);
        assertNotNull(chest.image2); // Opened chest image
    }

    @Test
    void testBootsInitialization() {
        OBJ_Boots boots = new OBJ_Boots(engine, 100, 100);
        assertEquals("boots", boots.name);
        assertEquals(100, boots.worldX);
        assertEquals(100, boots.worldY);
        assertTrue(boots.getDurability() > 0);
        assertTrue(boots.getMaxDurability() > 0);
    }
}

class GameStateTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Test
    void testInitialGameState() {
        assertEquals(Engine.GameState.START, engine.getGameState());
    }

    @Test
    void testGameStateTransitions() {
        engine.setGameState(Engine.GameState.RUNNING);
        assertEquals(Engine.GameState.RUNNING, engine.getGameState());

        engine.setGameState(Engine.GameState.PAUSED);
        assertEquals(Engine.GameState.PAUSED, engine.getGameState());

        engine.setGameState(Engine.GameState.FINISHED_LOST);
        assertEquals(Engine.GameState.FINISHED_LOST, engine.getGameState());
    }

    @Test
    void testGameDifficulty() {
        engine.setGameDifficulty(Engine.GameDifficulty.EASY);
        assertEquals(Engine.GameDifficulty.EASY, engine.getGameDifficulty());

        engine.setGameDifficulty(Engine.GameDifficulty.HARD);
        assertEquals(Engine.GameDifficulty.HARD, engine.getGameDifficulty());
    }

    @Test
    void testGameMode() {
        engine.setGameMode(Engine.GameMode.STORY);
        assertEquals(Engine.GameMode.STORY, engine.getGameMode());

        engine.setGameMode(Engine.GameMode.CUSTOM);
        assertEquals(Engine.GameMode.CUSTOM, engine.getGameMode());
    }
}